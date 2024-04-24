package com.marinamooringmanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger documentation.
 * This class provides OpenAPI definitions and security schemes for the Marina Mooring Management REST API.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "${swagger.title}",
                description = "${swagger.description}",
                summary = "${swagger.summary}",
                termsOfService = "${swagger.termsOfService}",

                contact = @Contact(
                        name = "${swagger.contact.name}",
                        email = "${swagger.contact.email}"
                ),
                license = @License(
                        name = "${swagger.license.name}"
                ),
                version = "${swagger.version}"
        ),
        servers = {
                @Server(
                        description = "${swagger.server.description}",
                        url = "${swagger.server.url}"
                )
        }
)
@SecurityScheme(
        name = "${swagger.security.name}",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "${swagger.security.bearerFormat}",
        scheme = "${swagger.security.scheme}",
        description = "${swagger.security.description}"
)
@Configuration
public class SwaggerConfig {
}

