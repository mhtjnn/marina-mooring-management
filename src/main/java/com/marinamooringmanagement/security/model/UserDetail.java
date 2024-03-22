package com.marinamooringmanagement.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class representing user details retrieved from the UserDetailsService.
 * This class is used to map user information retrieved from external sources like UserDetailsService.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetail {
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;
    /**
     * Get the user's ID.
     *
     * @return the user ID
     */
    public String getId() {
        return id;
    }
    /**
     * Set the user's ID.
     *
     * @param id the user ID to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * Get the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the user's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the user's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the user's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the user's password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
