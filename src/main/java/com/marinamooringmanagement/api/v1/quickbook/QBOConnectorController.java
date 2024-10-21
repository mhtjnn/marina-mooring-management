package com.marinamooringmanagement.api.v1.quickbook;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.QBO.QBOUserRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(value = "/api/v1/QBO")
@Transactional
@Tag(name = "Quickbook Connector Controller", description = "These are API's for quickbook connection.")
public class QBOConnectorController {

    private static final Logger logger = LoggerFactory.getLogger(QBOConnectorController.class);

    @Autowired
    OAuth2PlatformClientFactory factory;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private QBOUserRepository qboUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "API to connect quickbook with the application",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }
    )
    @RequestMapping("/connectToQuickbooks")
    public View connectToQuickbooks(HttpSession session,
                                    @RequestParam("authToken") final String authToken) {
        logger.info("inside connectToQuickbooks ");
        OAuth2Config oauth2Config = factory.getOAuth2Config();

        String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");

        String csrf = oauth2Config.generateCSRFToken();
        session.setAttribute("csrfToken", csrf);

        final String userEmail = jwtTokenUtil.getUsernameFromToken(authToken);

        session.setAttribute("userEmail", userEmail);

        try {
            List<Scope> scopes = new ArrayList<Scope>();
            scopes.add(Scope.All);
            return new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true, false);
        } catch (InvalidRequestException e) {
            logger.error("Exception calling connectToQuickbooks ", e);
        }
        return null;
    }

    @Operation(
            summary = "API to fetch quickbook connection status",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }
    )
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

    @Operation(
            summary = "API to redirect to the OAUTH",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }
    )
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

                if(null == session.getAttribute("userEmail")) throw new ResourceNotFoundException("No user email found!!!");
                final String userEmail = session.getAttribute("userEmail").toString();

                final User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given email: %1$s", userEmail)));

                //check if user already exists
                final Optional<QBOUser> dbQboUser = qboUserRepository.findQBOUserByEmail(user.getEmail());
                dbQboUser.ifPresent(qboUser -> qboUserRepository.delete(qboUser));

                QBOUser qboUser = QBOUser.builder()
                        .email(user.getEmail())
                        .realmId(realmId)
                        .authCode(authCode)
                        .accessToken(bearerTokenResponse.getAccessToken())
                        .refreshToken(bearerTokenResponse.getRefreshToken())
                        .build();

                qboUser.setCreationDate(new Date(System.currentTimeMillis()));
                qboUser.setLastModifiedDate(new Date(System.currentTimeMillis()));
                qboUser.setCreatedBy(user.getEmail());

                qboUserRepository.save(qboUser);

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
