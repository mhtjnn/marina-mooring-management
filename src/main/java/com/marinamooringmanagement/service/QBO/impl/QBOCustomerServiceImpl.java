package com.marinamooringmanagement.service.QBO.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import com.marinamooringmanagement.helper.QBOServiceHelper;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.QBO.QBOCustomerService;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class QBOCustomerServiceImpl implements QBOCustomerService {

    @Autowired
    OAuth2PlatformClientFactory factory;

    @Autowired
    public QBOServiceHelper helper;


    private static final Logger logger = LoggerFactory.getLogger(QBOCustomerServiceImpl.class);
    private static final String failureMsg="Failed";
    @Override
    public BasicRestResponse fetchCustomers(final HttpSession session) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            String realmId = (String) session.getAttribute("realmId");
            if (StringUtils.isEmpty(realmId)) {
                throw new RuntimeException("No realm ID.  QBO calls only work if the accounting scope was passed!");
            }
            String accessToken = (String) session.getAttribute("access_token");

            try {
                //get DataService
                DataService service = helper.getDataService(realmId, accessToken);

                // get all companyinfo
                String sql = "SELECT * FROM CompanyInfo";
                QueryResult queryResult = service.executeQuery(sql);
                String result = processResponse(failureMsg, queryResult);
                response.setContent(result);
                response.setStatus(HttpStatus.OK.value());
            }
            /*
             * Handle 401 status code -
             * If a 401 response is received, refresh tokens should be used to get a new access token,
             * and the API call should be tried again.
             */ catch (InvalidTokenException e) {
                logger.error("Error while calling executeQuery :: " + e.getMessage());

                //refresh tokens
                logger.info("received 401 during companyinfo call, refreshing tokens now");
                OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
                String refreshToken = (String) session.getAttribute("refresh_token");

                try {
                    BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
                    session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                    session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

                    //call company info again using new tokens
                    logger.info("calling companyinfo using new tokens");
                    DataService service = helper.getDataService(realmId, accessToken);

                    // get all companyinfo
                    String sql = "select CompanyName from companyinfo";
                    QueryResult queryResult = service.executeQuery(sql);
                    response.setMessage(processResponse(failureMsg, queryResult));

                } catch (OAuthException e1) {
                    logger.error("Error while calling bearer token :: " + e.getMessage());
                    throw new OAuthException("OAuth exception occurred!!!");
                } catch (FMSException e1) {
                    logger.error("Error while calling company currency :: " + e.getMessage());
                    throw new FMSException("FMS exception occurred!!!");
                }

            } catch (FMSException e) {
                List<Error> list = e.getErrorList();
                list.forEach(error -> logger.error("Error while calling executeQuery :: " + error.getMessage()));
                throw new FMSException("FMS exception occurred!!!");
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    private String processResponse(String failureMsg, QueryResult queryResult) {
        if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
            CompanyInfo companyInfo = (CompanyInfo) queryResult.getEntities().get(0);
            logger.info("Companyinfo -> CompanyName: " + companyInfo.getCompanyName());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonInString = mapper.writeValueAsString(companyInfo);
                return jsonInString;
            } catch (JsonProcessingException e) {
                logger.error("Exception while getting company info ", e);
                return new JSONObject().put("response",failureMsg).toString();
            }

        }
        return failureMsg;
    }
}
