package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


/**
 * Configuration class for Security purposes.
 * This class contains configuration beans for authentication and password encoding.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final UserRepository repository;

    /**
     * Bean for {@link UserDetailsService} built after finding the user from the database using the username.
     *
     * @return {@link UserDetailsService} instance
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return (UserDetailsService) username -> {
            User user = repository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username NOT FOUND!!!"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(String.valueOf(user.getRole()))
                    .build();
        };
    }

    /**
     * Bean for {@link AuthenticationProvider}.
     *
     * @return {@link DaoAuthenticationProvider} instance for username and password authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean for {@link AuthenticationManager}.
     *
     * @param config {@link AuthenticationConfiguration}
     * @return {@link AuthenticationManager} instance
     * @throws Exception if an error occurs while getting the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean for {@link PasswordEncoder}.
     *
     * @return {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
