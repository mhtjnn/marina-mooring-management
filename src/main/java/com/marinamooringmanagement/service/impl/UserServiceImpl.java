package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationDetails;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * Implementation class for User Service Interface.
 * This class provides implementation for managing user-related operations.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private SortUtils sortUtils;

    /**
     * Fetches a list of users based on the provided search request parameters, customer admin ID, and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param customerAdminId the unique identifier of the customer admin whose associated users are to be fetched.
     * @param searchText the text used to search for specific users by name, email, role, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the user search.
     */
    @Override
    public BasicRestResponse fetchUsers(final BaseSearchRequest baseSearchRequest, final Integer customerAdminId, final String searchText) {

        // get the role of the logged-in user.
        final String role = getLoggedInUserRole();

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            Specification<User> spec = new Specification<User>() {
                @Override
                public Predicate toPredicate(Root<User> user, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                    List<Predicate> predicates = new ArrayList<>();

                    /*
                    * If the search text is Integer then we are checking if it matches any ID or email(since email consists of numerical values)
                    * else we are checking name, email and phone number(saved as string).
                    */
                    if (null != searchText) {
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(user.get("name"), "%" + searchText + "%"),
                                criteriaBuilder.like(user.get("email"), "%" + searchText + "%"),
                                criteriaBuilder.like(user.get("phoneNumber"), "%" + searchText + "%")
                        ));
                    }

                    /*
                    * If the logged-in user is of ADMINISTRATOR role then if the customerAdminId is not provided then
                    * it will add those users with CUSTOMER_OWNER role.
                    * and if customerAdminId is provided then it will add those users which are of TECHNICIAN
                    * and FINANCE role having customerAdminId as given customerAdminId.
                    *
                    * If the logged-in user if of role as CUSTOMER_OWNER then
                    * it will add those users which are of TECHNICIAN and FINANCE role having customerAdminId
                    * as logged-in user ID.
                    */
                    if(role.equals(AppConstants.Role.ADMINISTRATOR)) {
                        if(null == customerAdminId) {
                            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.CUSTOMER_OWNER)));
                        } else {
                            predicates.add(criteriaBuilder.or(criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.TECHNICIAN),
                                    criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.FINANCE)));
                            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(user.get("customerAdminId"), customerAdminId)));
                        }
                    } else if(role.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                        predicates.add(criteriaBuilder.or(criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.TECHNICIAN),
                                criteriaBuilder.equal(user.join("role").get("name"), AppConstants.Role.FINANCE)));
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(user.get("customerAdminId"), getLoggedInUserID())));
                    } else {
                        throw new RuntimeException("Not authorized");
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            final Pageable p = PageRequest.of(
                    baseSearchRequest.getPageNumber(),
                    baseSearchRequest.getPageSize(),
                    sortUtils.getSort(baseSearchRequest.getSortBy(), baseSearchRequest.getSortDir())
            );

            // Fetching the roles based on the specifications.
            Page<User> filteredUsers = userRepository.findAll(spec, p);

            // Convert the filtered users to UserResponseDto
            List<UserResponseDto> filteredUserResponseDtoList = filteredUsers
                    .getContent()
                    .stream()
                    .map(user -> {
                        UserResponseDto userResponseDto = UserResponseDto.builder().build();
                        mapper.mapToUserResponseDto(userResponseDto, user);
                        userResponseDto.setRole(user.getRole().getName());
                        if (null != user.getState()) userResponseDto.setState(user.getState().getName());
                        if (null != user.getCountry()) userResponseDto.setCountry(user.getCountry().getName());
                        return userResponseDto;
                    }).toList();


            // creating a pageable response of filtered user response dto
            final Page<UserResponseDto> pageOfUser = new PageImpl<>(filteredUserResponseDtoList, p, filteredUserResponseDtoList.size());

            response.setContent(pageOfUser);
            response.setMessage("Users fetched successfully");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    /**
     * Saves a new user in the database.
     *
     * @param userRequestDto The user details to save.
     * @param customerAdminId The ID of the customer admin if applicable.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final Integer customerAdminId) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            final User user = User.builder().build();

            log.info(String.format("saving user in DB"));

            /*
             * Calling the function which saves user to database with modification if required.
             */
            performSave(userRequestDto, user, null, customerAdminId);

            response.setMessage("User saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     * @param customerAdminId The ID of the customer admin if applicable.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse deleteUser(final Integer userId, final Integer customerAdminId) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));

            // Fetching the optional of user from the database.
            Optional<User> optionalUser = userRepository.findById(userId);

            /*
            * If no user exists with the given ID then this condition will throw an error
            * else this condition will be passed.
            */
            if (optionalUser.isEmpty()) throw new ResourceNotFoundException("No user found with the given ID");

            // Getting the user which is requested to be deleted.
            User userToBeDeleted = optionalUser.get();

            /*
            * Calling the function to check if the logged-in user has the authority to perform the delete
            *  operation on the user which is requested to be deleted.
            */
            if(!checkForAuthority(customerAdminId, userToBeDeleted.getRole().getName())) throw new RuntimeException("Not authorized");

            /*
            * A boolean variable which is true if the user(to be deleted) is of FINANCE or TECHNICIAN role.
            */
            boolean roleTechnicianOrFinance = userToBeDeleted.getRole().getName().equals(AppConstants.Role.TECHNICIAN)
                    || userToBeDeleted.getRole().getName().equals(AppConstants.Role.FINANCE);

            /*
            * If the logged-in user role is CUSTOMER_OWNER and the user(to be deleted) is of TECHNICIAN and FINANCE
            * role then if the user(to be deleted) customerAdminId doesn't match the logged-in user Id then this will
            * throw error as logged-in user has no authority to delete user with different customerAdminId.
            */
            if(StringUtils.equals(getLoggedInUserRole(), AppConstants.Role.CUSTOMER_OWNER) && roleTechnicianOrFinance) {
                if(!Objects.equals(userToBeDeleted.getCustomerAdminId(), getLoggedInUserID()))
                    throw new RuntimeException("Not authorized to delete user with different customer admin ID");
            }

            /*
            * If the user(to be deleted) is of CUSTOMER_OWNER role then all the TECHNICIAN and FINANCE role
            * user are also deleted.
            */
            if(userToBeDeleted.getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) {
                userRepository.deleteAll(
                        userRepository.findAll().stream()
                        .filter(user ->  null != user.getCustomerAdminId() && user.getCustomerAdminId().equals(userToBeDeleted.getId()))
                        .toList()
                );
            }

            //  Tokens assigned with the user(to be deleted) are deleted.
            final List<Token> tokenList = tokenRepository.findByUserId(userId);
            tokenRepository.deleteAll(tokenList);

            // deleting the user
            userRepository.deleteById(userId);

            response.setMessage("User Deleted Successfully!!!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param userRequestDto The updated user details.
     * @param userId The ID of the user to update.
     * @param customerAdminId The ID of the customer admin if applicable.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, Integer userId, final Integer customerAdminId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            //Fetching the optional of user from the database
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                // Getting the user
                final User user = optionalUser.get();

                log.info(String.format("update user"));

                //This will throw error if the email is requested to change.
                if (null != userRequestDto.getEmail() && !userRequestDto.getEmail().equals(user.getEmail())) {
                    throw new RuntimeException("Email cannot be updated");
                }

                //calling the performSave() function to update the changes and save the user.
                performSave(userRequestDto, user, userId, customerAdminId);
                response.setMessage("User updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());
            } else {
                // This will throw error as user(to be updated) doesn't exist in the database.
                throw new DBOperationException("No User found with given User ID");
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find.
     * @return A {@code UserDto} representing the user if found, otherwise {@code null}.
     */
    @Override
    public UserDto findByEmailAddress(String email) {
        UserDto user = null;
        if (null != email) {
            User empEntity = userRepository.findByEmail(email).get();
            if (null != empEntity) {
                user = mapper.mapToUserDto(UserDto.builder().build(), empEntity);
            }
        }
        return user;
    }

    /**
     * Updates the password for a user.
     *
     * @param token The reset password token.
     * @param newPasswordRequest The new password details.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     * @throws Exception If an error occurs during password update.
     */
    @Override
    public BasicRestResponse updatePassword(final String token, final NewPasswordRequest newPasswordRequest) throws Exception {
        final BasicRestResponse passwordResponse = BasicRestResponse.builder().build();
        passwordResponse.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            // Checking if the token is valid or not.
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Token is invalid");
            }

            //Getting the email/username from the token.
            final String email = jwtUtil.getUsernameFromToken(token);

            //Fetching the optional of user with the username extracted earlier from the token.
            Optional<User> optionalUser = userRepository.findByEmail(email);

            /* If no user is present then exception will be thrown as no user with the extracted
            * email is present in the database.
             */
            if (optionalUser.isEmpty()) {
                throw new ResourceNotFoundException("No User found with the given email ID");
            }

            //If new password is different password then exception is thrown as "Confirm password doesn't match with New password"
            if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
                throw new RuntimeException("Confirm password doesn't match with New password");
            } else {
                // Getting the user from the optional of user
                final User user = optionalUser.get();

                //If the new password is same as old password then exception is thrown as "New password is same as old password".
                if (passwordEncoder.matches(newPasswordRequest.getNewPassword(), user.getPassword())) {
                    throw new RuntimeException("New password is same as old password");
                } else {
                    // Setting the new password for the user and then saving it to the database.
                    user.setPassword(passwordEncoder.encode(newPasswordRequest.getConfirmPassword()));
                    userRepository.save(user);
                    passwordResponse.setMessage("Password changed Successfully!!!");
                    passwordResponse.setStatus(HttpStatus.OK.value());
                }
            }
        } catch (Exception e) {
            passwordResponse.setMessage(e.getMessage());
            passwordResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return passwordResponse;
    }

    /**
     * Validates the email and reset password token.
     *
     * @param token The reset password token.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse checkEmailAndTokenValid(final String token) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            //Getting the email from the token
            final String email = jwtUtil.getUsernameFromToken(token);

            //Getting the optional of user from the database
            Optional<User> optionalUser = userRepository.findByEmail(email);

            //If optional of user is empty then exception is thrown as "No user found with the email extracted from token"
            if (optionalUser.isEmpty()) {
                throw new ResourceNotFoundException("No user found with the email extracted from token");
            }

            //Validating the token
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("INVALID TOKEN!!!");
            }
            response.setMessage("Email and Token Valid. Please proceed ahead...");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Helper function to perform save operation for a user.
     *
     * @param userRequestDto The user details to save or update.
     * @param user The {@code User} entity to update or save.
     * @param userId The ID of the user to update, if applicable.
     * @param customerAdminId The ID of the customer admin if applicable.
     * @return The saved or updated {@code User} entity.
     */
    public User performSave(final UserRequestDto userRequestDto, final User user, final Integer userId , Integer customerAdminId) {

        //Getting the role of the logged-in role
        final String role = getLoggedInUserRole();

        try {
            Optional<User> optionalUser = Optional.empty();

            if(null != userRequestDto.getUserID()) {
                optionalUser = userRepository.findByUserID(userRequestDto.getUserID());
                if(optionalUser.isPresent() && !StringUtils.equals(userRequestDto.getUserID(), optionalUser.get().getUserID())) throw new RuntimeException("Given user ID is already present for some other user");
            }

            //Checking if the logged-in user has the authority to perform save functionality.
            if (!checkForAuthority(customerAdminId, userRequestDto.getRole()))
                throw new RuntimeException("Not authorized!!!");

            //if userId is null that means user is getting saved for thw first time so we are setting creation date here
            if(null == userId) user.setCreationDate(new Date(System.currentTimeMillis()));

            //mapping the simple properties of the user from the given userRequestDto
            mapper.mapToUser(user, userRequestDto);

            //setting the lastModifiedDate
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));

            //If the logged-in user role is CUSTOMER_OWNER then we are setting the given customerAdminId(can be null) to logged-in user Id
            if(role.equals(AppConstants.Role.CUSTOMER_OWNER)) customerAdminId = getLoggedInUserID();

            //If the user(called for performSave()) is of TECHNICIAN or FINANCE role then we are setting customerAdminId.
            if (userRequestDto.getRole().equals(AppConstants.Role.TECHNICIAN)
                    || userRequestDto.getRole().equals(AppConstants.Role.FINANCE)) user.setCustomerAdminId(customerAdminId);

            // Setting the password if not null
            if(null != userRequestDto.getPassword() && null != userRequestDto.getConfirmPassword()) {
                if(null != userId) throw new RuntimeException("Password cannot be updated");
                if (userRequestDto.getPassword().isBlank()) throw new RuntimeException("Password is blank");
                if (userRequestDto.getConfirmPassword().isBlank()) throw new RuntimeException("Confirm Password is blank");
                if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword()))
                    throw new RuntimeException("New Password and confirm password are not equal");
                user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            } else {
                /* if the password in userRequestDto is null and userId is also null that means the user is getting
                * saved for the first time. So we throw exception as for the first time of saving the user the
                * password cannot be null.
                 */
                if(null == userId) throw new RuntimeException("Password or confirm password cannot be null");
            }

            //Setting the role if not null
            if (null != userRequestDto.getRole()) {
                final Optional<Role> optionalRole = roleRepository.findByName(userRequestDto.getRole());

                //If the optional role is empty then we throw exception as "No Role found with the given role"
                if (optionalRole.isEmpty())
                    throw new ResourceNotFoundException("No Role found with the given role");
                final Role savedRole = optionalRole.get();
                user.setRole(savedRole);
            } else {
                /* if the role in userRequestDto is null and userId is also null that means the user is getting
                 * saved for the first time. So we throw exception as for the first time of saving the user the
                 * role cannot be null.
                 */
                if(null == userId) throw new RuntimeException("Role cannot be null");
            }

            //Setting the state if not null
            if (null != userRequestDto.getState()) {
                final Optional<State> optionalState = stateRepository.findByName(userRequestDto.getState());
                if (optionalState.isPresent()) {
                    user.setState(optionalState.get());
                } else {
                    throw new RuntimeException("No state found with the given state name");
                }
            } else {
                /* if the state in userRequestDto is null and userId is also null that means the user is getting
                 * saved for the first time. So we throw exception as for the first time of saving the user the
                 * state cannot be null.
                 */
                if(null == userId) throw new RuntimeException("State cannot be null");
            }

            //Setting the country if not null
            if (null != userRequestDto.getCountry()) {
                final Optional<Country> optionalCountry = countryRepository.findByName(userRequestDto.getCountry());
                if (optionalCountry.isPresent()) {
                    user.setCountry(optionalCountry.get());
                } else {
                    throw new RuntimeException("No country found with the given country name");
                }
            } else {
                /* if the country in userRequestDto is null and userId is also null that means the user is getting
                 * saved for the first time. So we throw exception as for the first time of saving the user the
                 * country cannot be null.
                 */
                if(null == userId) throw new RuntimeException("State cannot be null");
            }


        } catch (Exception e) {
            log.error("Error occurred during perform save method {}", e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }

        return userRepository.save(user);
    }

    /**
     * Checks if the current user has the authority to perform the specified operation.
     *
     * @param customerAdminId The ID of the customer admin if applicable.
     * @param role The role for which authority is being checked.
     * @return {@code true} if the current user has the authority, otherwise {@code false}.
     */
    public boolean checkForAuthority(Integer customerAdminId, final String role) {
        //Getting the logged-in user role
        String loggedInUserRole = getLoggedInUserRole();

        /*
         * Boolean variable stating true if the role(of the user on which operations would be performed)
         * is TECHNICIAN or FINANCE and false for other role.
         */
        boolean roleTechnicianOrFinance = role.equals(AppConstants.Role.TECHNICIAN)
                || role.equals(AppConstants.Role.FINANCE);

        /*
        * If the logged-in user role is CUSTOMER_OWNER and then if the role(of the user on which operations would be performed)
        * is ADMINISTRATOR and CUSTOMER_OWNER then we are throwing error as "Not authorized to perform operations for ADMINISTRATOR/CUSTOMER_OWNER role"
        * If the logged-in user is ADMINISTRATOR and if the role(of the user on which operations would be performed) is TECHNICIAN/FINANCE
        * then if the customerAdminId is not provided then we throw exception as Customer Admin ID not provided but if the
        * customerAdminId is provided but no user exists with the provided customerAdminId then we throw exception as
        * No user exists with the given customer admin ID and if the user exists with the given customerAdminId but that user role
        * is different from CUSTOMER_OWNER then we throw error as No user with CUSTOMER_OWNER role exists with the given customer admin ID.
        */
        if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_OWNER)) {
            if(role.equals(AppConstants.Role.ADMINISTRATOR) || role.equals(AppConstants.Role.CUSTOMER_OWNER))
                throw new RuntimeException(String.format("Not authorized to perform operations for %1$s role", role));
        } else if (loggedInUserRole.equals(AppConstants.Role.ADMINISTRATOR)) {
            if(roleTechnicianOrFinance) {
                if(null == customerAdminId) throw new RuntimeException("Please select a CUSTOMER OWNER to save user with TECHNICIAN or FINANCE role.");
                Optional<User> optionalUser =  userRepository.findById(customerAdminId);
                if(optionalUser.isEmpty()) throw new RuntimeException("No user exists with the given customer admin ID");
                if(null == optionalUser.get().getRole()
                        || !optionalUser.get().getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER))
                    throw new RuntimeException("No user with CUSTOMER_OWNER role exists with the given customer admin ID");
            }
        } else {
            //User with TECHNICIAN/FINANCE have no authority to perform operation on user.
            return false;
        }

        return true;
    }

    /**
     * Retrieves the role of the currently logged-in user.
     *
     * @return The role of the currently logged-in user.
     */
    public String getLoggedInUserRole() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserRole();
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The ID of the currently logged-in user.
     */
    public Integer getLoggedInUserID() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserId();
    }

}
