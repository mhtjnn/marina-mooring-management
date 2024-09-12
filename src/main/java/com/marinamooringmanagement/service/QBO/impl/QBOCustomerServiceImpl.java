package com.marinamooringmanagement.service.QBO.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.helper.QBOServiceHelper;
import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import com.marinamooringmanagement.model.entity.QuickbookCustomer;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.QBO.QBOUserRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.QBO.QBOCustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class QBOCustomerServiceImpl implements QBOCustomerService {

    @Autowired
    OAuth2PlatformClientFactory factory;

    @Autowired
    public QBOServiceHelper helper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private QBOUserRepository qboUserRepository;


    private static final Logger logger = LoggerFactory.getLogger(QBOCustomerServiceImpl.class);
    private static final String failureMsg="Failed";

    @Override
    public BasicRestResponse fetchCustomers(final HttpSession session, final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;
            if(org.apache.commons.lang3.StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.FINANCE)) {
                final User financeUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No finance user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(financeUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", financeUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }

            final QBOUser qboUser = qboUserRepository.findQBOUserByEmail(user.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No QBO user found with the given email: %1$s", user.getEmail())));

            String realmId = qboUser.getRealmId();
            if (StringUtils.isEmpty(realmId)) {
                throw new RuntimeException("No realm ID.  QBO calls only work if the accounting scope was passed!");
            }
            String accessToken = qboUser.getAccessToken();

            try {
                //get DataService
                DataService service = helper.getDataService(realmId, accessToken);

                // get all companyinfo
                String sql = "SELECT id, givenName, familyName FROM Customer";
                QueryResult queryResult = service.executeQuery(sql);
                List<QuickbookCustomer> result = processResponseForCustomerList(failureMsg, queryResult);
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
                String refreshToken = qboUser.getRefreshToken();

                try {
                    BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
                    session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                    session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

                    qboUser.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    qboUser.setAccessToken(bearerTokenResponse.getAccessToken());
                    qboUser.setRefreshToken(bearerTokenResponse.getRefreshToken());

                    qboUserRepository.save(qboUser);

                    //call company info again using new tokens
                    logger.info("calling companyinfo using new tokens");
                    DataService service = helper.getDataService(realmId, accessToken);

                    // get all companyinfo
                    String sql = "select CompanyName from companyinfo";
                    QueryResult queryResult = service.executeQuery(sql);
                    List<QuickbookCustomer> result = processResponseForCustomerList(failureMsg, queryResult);
                    response.setContent(result);
                    response.setStatus(HttpStatus.OK.value());

                } catch (OAuthException e1) {
                    logger.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling bearer token :: " + e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                } catch (FMSException e1) {
                    logger.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling company currency :: " + e.getMessage());
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }

            } catch (FMSException e) {
                List<Error> list = e.getErrorList();
                list.forEach(error -> logger.error("Error while calling executeQuery :: " + error.getMessage()));
                response.setMessage("Error while calling executeQuery :: " + e.getMessage());
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    private List<QuickbookCustomer> processResponseForCustomerList(String failureMsg, QueryResult queryResult) {
        List<QuickbookCustomer>  quickbookCustomerList = new ArrayList<>();
        if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
            List<Customer> customerList = (List<Customer>) queryResult.getEntities();

            for(Customer customer: customerList) {
                QuickbookCustomer quickbookCustomer = QuickbookCustomer.builder().build();
                quickbookCustomer.setQuickbookCustomerId(customer.getId());
                quickbookCustomer.setQuickbookCustomerFirstName(customer.getGivenName());
                quickbookCustomer.setQuickbookCustomerLastName(customer.getFamilyName());

                quickbookCustomerList.add(quickbookCustomer);
            }
        }
        return quickbookCustomerList;
    }
}
