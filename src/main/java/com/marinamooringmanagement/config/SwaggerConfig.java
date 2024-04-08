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

@OpenAPIDefinition(
        info = @Info(
                title = "Marina Mooring Management",
                description = "Rest API's for marina mooring management",
                summary = "This documentation contains information of REST API's in marina mooring management project",
                termsOfService = "T&C",
                contact = @Contact(
                        name = "Genboot",
                        email = "genboot@gmail.com"
                ),
                license = @License(
                        name = "Licence No."
                ),
                version = "v3"
        ),
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:8082"
                )
        }
)
@SecurityScheme(
        name = "auth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Bearer token required for authorization"
)
@Configuration
public class SwaggerConfig {
}
