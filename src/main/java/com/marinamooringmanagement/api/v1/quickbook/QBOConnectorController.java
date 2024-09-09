package com.marinamooringmanagement.api.v1.quickbook;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Controller
@RequestMapping(value = "/api/v1/QBO")
public class QBOConnectorController {

    private static final Logger logger = LoggerFactory.getLogger(QBOConnectorController.class);

    @Autowired
    OAuth2PlatformClientFactory factory;

    @RequestMapping("/connected")
    public String connected() {
        return "connected";
    }

    @RequestMapping("/connectToQuickbooks")
    public View connectToQuickbooks(HttpSession session) {
        logger.info("inside connectToQuickbooks ");
        OAuth2Config oauth2Config = factory.getOAuth2Config();

        String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");

        String csrf = oauth2Config.generateCSRFToken();
        session.setAttribute("csrfToken", csrf);
        try {
            List<Scope> scopes = new ArrayList<Scope>();
            scopes.add(Scope.All);
            return new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true, false);
        } catch (InvalidRequestException e) {
            logger.error("Exception calling connectToQuickbooks ", e);
        }
        return null;
    }

    @RequestMapping("/status")
    public ResponseEntity<String> status(@RequestParam("result") String result) {
        String message = "QuickBooks connected successfully!";
        if ("failure".equals(result)) {
            message = "Failed to connect to QuickBooks.";
        }

        String script = "<html><body><script type=\"text/javascript\">" +
                "alert('" + message + "');" +
                "window.close();" +
                "window.location.href = 'http://localhost:3000/dashboard';" +
                "</script></body></html>";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    @RequestMapping("/oauth2redirect")
    public String callBackFromOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {
        logger.info("inside oauth2redirect of sample"  );
        try {
            String csrfToken = (String) session.getAttribute("csrfToken");
            if (csrfToken.equals(state)) {
                session.setAttribute("realmId", realmId);
                session.setAttribute("auth_code", authCode);

                OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
                String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");
                logger.info("inside oauth2redirect of sample -- redirectUri " + redirectUri  );

                BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);

                session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

                // Update your Data store here with user's AccessToken and RefreshToken along with the realmId

                return "redirect:/api/v1/QBO/status?result=success";
            }
            logger.info("csrf token mismatch " );
        } catch (OAuthException e) {
            logger.error("Exception in callback handler ", e);
            return "redirect:/api/v1/QBO/status?result=failure";
        }
        return "redirect:/api/v1/QBO/status?result=failure";
    }

}
