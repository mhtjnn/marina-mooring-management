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
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.JwtUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.*;
import com.marinamooringmanagement.utils.SortUtils;
import io.jsonwebtoken.io.Decoders;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
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

    @Autowired
    private LoggedInUserUtil loggedInUserUtil;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private BoatyardRepository boatyardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BoatyardService boatyardService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorService vendorService;

    /**
     * Fetches a list of users based on the provided search request parameters, customer admin ID, and search text.
     *
     * @param baseSearchRequest the base search request containing common search parameters such as filters, pagination, etc.
     * @param searchText        the text used to search for specific users by name, email, role, or other relevant criteria.
     * @return a BasicRestResponse containing the results of the user search.
     */
    @Override
    public BasicRestResponse fetchUsers(final BaseSearchRequest baseSearchRequest, final String searchText, final HttpServletRequest request) {
        final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
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
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("name")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("email")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("phoneNumber")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.join("role").get("name")), "%" + lowerCaseSearchText + "%")
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
                    predicates.add(authorizationUtil.fetchPredicateForUser(customerOwnerId, user, criteriaBuilder));

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
            response.setTotalSize(userRepository.findAll(spec).size());

            // Convert the filtered users to UserResponseDto
            List<UserResponseDto> filteredUserResponseDtoList = filteredUsers
                    .getContent()
                    .stream()
                    .map(user -> {
                        UserResponseDto userResponseDto = UserResponseDto.builder().build();
                        mapper.mapToUserResponseDto(userResponseDto, user);
                        RoleResponseDto roleResponseDto = RoleResponseDto.builder()
                                .id(user.getRole().getId())
                                .name(user.getRole().getName())
                                .description(user.getRole().getDescription())
                                .build();
                        userResponseDto.setRoleResponseDto(roleResponseDto);

                        StateResponseDto stateResponseDto = null;
                        if (null != user.getState()) stateResponseDto = StateResponseDto.builder()
                                .id(user.getState().getId())
                                .label(user.getState().getLabel())
                                .name(user.getState().getName())
                                .build();
                        if (null != user.getState()) userResponseDto.setStateResponseDto(stateResponseDto);

                        CountryResponseDto countryResponseDto = CountryResponseDto.builder()
                                .id(user.getCountry().getId())
                                .label(user.getCountry().getLabel())
                                .name(user.getCountry().getName())
                                .build();
                        if (null != user.getCountry()) userResponseDto.setCountryResponseDto(countryResponseDto);
                        return userResponseDto;
                    }).toList();


            response.setContent(filteredUserResponseDtoList);
            response.setMessage("Users fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            if(filteredUserResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(filteredUserResponseDtoList.size());

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
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final HttpServletRequest request) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {

            Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());

            if (optionalUser.isPresent())
                throw new RuntimeException(String.format("User with email Id as %1$s is already present", userRequestDto.getEmail()));

            final User user = User.builder().build();

            log.info(String.format("saving user in DB"));

            /*
             * Calling the function which saves user to database with modification if required.
             */
            performSave(userRequestDto, user, null, request);

            response.setMessage("User saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            if (e.getLocalizedMessage().startsWith("Query"))
                response.setMessage(String.format("User with email Id as %1$s is already present", userRequestDto.getEmail()));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    @Transactional
    public BasicRestResponse deleteUser(final Integer userId, final HttpServletRequest request) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            // Fetching the optional of user from the database.
            Optional<User> optionalUser = userRepository.findById(userId);

            /*
             * If no user exists with the given ID then this condition will throw an error
             * else this condition will be passed.
             */
            if (optionalUser.isEmpty()) throw new ResourceNotFoundException("No user found with the given ID");

            // Getting the user which is requested to be deleted.
            User userToBeDeleted = optionalUser.get();

            authorizationUtil.checkAuthorityForDeleteUser(customerOwnerId, userToBeDeleted);

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
            if (StringUtils.equals(loggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.CUSTOMER_OWNER) && roleTechnicianOrFinance) {
                if (!Objects.equals(userToBeDeleted.getCustomerOwnerId(), loggedInUserUtil.getLoggedInUserID()))
                    throw new RuntimeException("Not authorized to delete user with different customer admin ID");
            }

            /*
             * If the user(to be deleted) is of CUSTOMER_OWNER role then all the TECHNICIAN and FINANCE role
             * user are also deleted.
             */
            if (userToBeDeleted.getRole().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) {
                request.setAttribute("CUSTOMER_OWNER_ID", userToBeDeleted.getId());

                List<Vendor> vendorList = vendorRepository.findAll()
                        .stream()
                        .filter(vendor -> null != vendor.getUser() && null != vendor.getUser().getId() && vendor.getUser().getId().equals(userToBeDeleted.getId()))
                        .toList();

                for(Vendor vendor: vendorList) {
                    vendorService.deleteVendor(vendor.getId(), request);
                }

                List<Boatyard> boatyardList = boatyardRepository.findAll().stream()
                        .filter(boatyard -> null != boatyard.getUser() && null != boatyard.getUser().getId() && boatyard.getUser().getId().equals(userToBeDeleted.getId()))
                        .toList();

                for(Boatyard boatyard: boatyardList) {
                    boatyardService.deleteBoatyardById(boatyard.getId(), request);
                }

                List<Customer> customerList = customerRepository.findAll().stream()
                        .filter(customer -> null != customer.getUser() && null != customer.getUser().getId() && customer.getUser().getId().equals(userToBeDeleted.getId()))
                        .toList();

                for(Customer customer: customerList) {
                    customerService.deleteCustomerById(customer.getId(), request);
                }

                List<User> userAssociatedWithCurrCustomerOwner = userRepository.findAll().stream()
                        .filter(user -> null != user.getCustomerOwnerId() && user.getCustomerOwnerId().equals(userToBeDeleted.getId()))
                        .toList();

                for(User user: userAssociatedWithCurrCustomerOwner) {
                    List<Token> tokenList = tokenRepository.findByUserId(user.getId());
                    tokenRepository.deleteAll(tokenList);
                    userRepository.delete(user);
                }
            }

            //  Tokens assigned with the user(to be deleted) are deleted.
            final List<Token> tokenList = tokenRepository.findByUserId(userId);
            if(null != tokenList && !tokenList.isEmpty()) tokenRepository.deleteAll(tokenList);

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
     * @param userId         The ID of the user to update.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     */
    @Override
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, Integer userId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            //Fetching the optional of user from the database
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                // Getting the user
                final User user = optionalUser.get();

                log.info(String.format("update user"));

                //calling the performSave() function to update the changes and save the user.
                performSave(userRequestDto, user, userId, request);
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
     * @param token              The reset password token.
     * @param newPasswordRequest The new password details.
     * @return A {@code BasicRestResponse} indicating the status of the operation.
     * @throws Exception If an error occurs during password update.
     */
    @Override
    public BasicRestResponse updatePassword(final String token, final NewPasswordRequest newPasswordRequest) throws Exception {
        final BasicRestResponse passwordResponse = BasicRestResponse.builder().build();
        passwordResponse.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            byte[] keyBytesNewPassword = Decoders.BASE64.decode(newPasswordRequest.getNewPassword());
            final String newPassword = new String(keyBytesNewPassword, StandardCharsets.UTF_8);

            if (!isInPasswordFormat(newPassword)) throw new RuntimeException("Invalid Password Format");

            byte[] keyBytesConfirmPassword = Decoders.BASE64.decode(newPasswordRequest.getNewPassword());
            final String confirmPassword = new String(keyBytesConfirmPassword, StandardCharsets.UTF_8);

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
            if (!StringUtils.equals(newPassword, confirmPassword)) {
                throw new RuntimeException("Confirm password doesn't match with New password");
            } else {
                // Getting the user from the optional of user
                final User user = optionalUser.get();

                //If the new password is same as old password then exception is thrown as "New password is same as old password".
                if (passwordEncoder.matches(newPassword, user.getPassword())) {
                    throw new RuntimeException("New password is same as old password");
                } else {
                    // Setting the new password for the user and then saving it to the database.
                    user.setPassword(passwordEncoder.encode(newPassword));
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

    @Override
    public BasicRestResponse fetchUsersOfTechnicianRole(BaseSearchRequest baseSearchRequest, String searchText, HttpServletRequest request) {
        final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
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
                        String lowerCaseSearchText = "%" + searchText.toLowerCase() + "%";
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("name")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("email")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.get("phoneNumber")), "%" + lowerCaseSearchText + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(user.join("role").get("name")), "%" + lowerCaseSearchText + "%")
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
                    predicates.add(authorizationUtil.fetchPredicateForTechnician(customerOwnerId, user, criteriaBuilder));

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
                        RoleResponseDto roleResponseDto = RoleResponseDto.builder()
                                .id(user.getRole().getId())
                                .name(user.getRole().getName())
                                .description(user.getRole().getDescription())
                                .build();
                        userResponseDto.setRoleResponseDto(roleResponseDto);

                        StateResponseDto stateResponseDto = null;
                        if (null != user.getState()) stateResponseDto = StateResponseDto.builder()
                                .id(user.getState().getId())
                                .label(user.getState().getLabel())
                                .name(user.getState().getName())
                                .build();
                        if (null != user.getState()) userResponseDto.setStateResponseDto(stateResponseDto);

                        CountryResponseDto countryResponseDto = CountryResponseDto.builder()
                                .id(user.getCountry().getId())
                                .label(user.getCountry().getLabel())
                                .name(user.getCountry().getName())
                                .build();
                        if (null != user.getCountry()) userResponseDto.setCountryResponseDto(countryResponseDto);
                        return userResponseDto;
                    }).toList();


            response.setContent(filteredUserResponseDtoList);
            response.setMessage("Users of technician role fetched successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setTotalSize(userRepository.count());
            if(filteredUserResponseDtoList.isEmpty()) response.setCurrentSize(0);
            else response.setCurrentSize(filteredUserResponseDtoList.size());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
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
     * @param user           The {@code User} entity to update or save.
     * @param userId         The ID of the user to update, if applicable.
     * @return The saved or updated {@code User} entity.
     */
    public User performSave(final UserRequestDto userRequestDto, final User user, final Integer userId, final HttpServletRequest request) {
        User savedUser = null;
        try {
            Optional<Role> optionalRole = Optional.empty();

            //Getting the role of the logged-in role
            final String role = loggedInUserUtil.getLoggedInUserRole();

            Role savedRole = null;

            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");

            if (null != userRequestDto.getRoleId()) {
                optionalRole = roleRepository.findById(userRequestDto.getRoleId());
                if (optionalRole.isEmpty()) throw new RuntimeException("No Role found with the given role");
                if (null != user.getRole() && !StringUtils.equals(optionalRole.get().getName(), user.getRole().getName()))
                    throw new RuntimeException("Role cannot be updated");
                savedRole = optionalRole.get();
                user.setRole(savedRole);
                if (optionalRole.get().getName().equals(AppConstants.Role.CUSTOMER_OWNER)) {
                    if (null == userRequestDto.getCompanyName())
                        throw new RuntimeException("Company name cannot be null while saving a new user with CUSTOMER OWNER role");
                    user.setCompanyName(userRequestDto.getCompanyName());
                }
            } else {
                /* if the role in userRequestDto is null and userId is also null that means the user is getting
                 * saved for the first time. So we throw exception as for the first time of saving the user the
                 * role cannot be null.
                 */
                if (null == userId) throw new RuntimeException("Role cannot be null");
                if (null != user.getRole()) savedRole = user.getRole();
                else
                    throw new RuntimeException(String.format("No role found for the user with the given id: %1$s", userId));
            }

            //Checking if the logged-in user has the authority to perform save functionality.
            authorizationUtil.checkAuthorityForUser(customerOwnerId, savedRole.getName());

            //if userId is null that means user is getting saved for the first time. So, we are setting creation date here
            if (null == userId) user.setCreationDate(new Date(System.currentTimeMillis()));

            //mapping the simple properties of the user from the given userRequestDto
            mapper.mapToUser(user, userRequestDto);

            //setting the lastModifiedDate
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));

            //If the logged-in user role is CUSTOMER_OWNER then we are setting the given customerAdminId(can be null) to logged-in user Id
            if (role.equals(AppConstants.Role.CUSTOMER_OWNER)) {
                customerOwnerId = loggedInUserUtil.getLoggedInUserID();
            }

            //If the user(called for performSave()) is of TECHNICIAN or FINANCE role then we are setting customerAdminId.
            if (savedRole.getName().equals(AppConstants.Role.TECHNICIAN)
                    || savedRole.getName().equals(AppConstants.Role.FINANCE)) user.setCustomerOwnerId(customerOwnerId);

            //Updating email of the user
            if (null != userRequestDto.getEmail() && !StringUtils.equals(user.getEmail(), userRequestDto.getEmail())) {
                Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());
                if (optionalUser.isPresent() && !user.getId().equals(optionalUser.get().getId()))
                    throw new RuntimeException(String.format("Given email as: %1$s is already present for some other user", userRequestDto.getEmail()));
                user.setEmail(userRequestDto.getEmail());
            }

            // Setting the password if not null
            if (null != userRequestDto.getPassword() && null != userRequestDto.getConfirmPassword()) {

                byte[] keyBytesForPassword = Decoders.BASE64.decode(userRequestDto.getPassword());
                String password = new String(keyBytesForPassword, StandardCharsets.UTF_8);
                if (!isInPasswordFormat(password)) throw new RuntimeException("Invalid Password Format");

                byte[] keyBytesForConfirmPassword = Decoders.BASE64.decode(userRequestDto.getPassword());
                String confirmPassword = new String(keyBytesForConfirmPassword, StandardCharsets.UTF_8);
                if (!isInPasswordFormat(confirmPassword)) throw new RuntimeException("Invalid Password Format");

                userRequestDto.setPassword(password);
                userRequestDto.setConfirmPassword(confirmPassword);

                if (userRequestDto.getPassword().isBlank()) throw new RuntimeException("Password is blank");
                if (userRequestDto.getConfirmPassword().isBlank())
                    throw new RuntimeException("Confirm Password is blank");
                if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword()))
                    throw new RuntimeException("New Password and confirm password are not equal");

                // If the user(to be updated) tries to update its password then we throw exception as Password cannot be updated.
                if (null != userId) {
                    if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
                        throw new RuntimeException("Password cannot be updated");
                    }
                }

                user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            } else {
                /* if the password in userRequestDto is null and userId is also null that means the user is getting
                 * saved for the first time. So we throw exception as for the first time of saving the user the
                 * password cannot be null.
                 */
                if (null == userId) throw new RuntimeException("Password or confirm password cannot be null");
            }

            if(null != userRequestDto.getPhoneNumber()) {
                String givenPhoneNumber = userRequestDto.getPhoneNumber();
                int hyphenCount = 0;
                for(char ch: givenPhoneNumber.toCharArray()) {
                    if(ch != '-' && !Character.isDigit(ch)) throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
                    if(ch == '-') {
                        hyphenCount++;
                    }
                }

                if(hyphenCount == 0) {
                    if(givenPhoneNumber.length() == 12) throw new RuntimeException(String.format("Given phone number: %1$s contains 12 digits (should be 10 digits)", givenPhoneNumber));
                    givenPhoneNumber = givenPhoneNumber.substring(0, 3)
                            + "-" +
                            givenPhoneNumber.substring(3, 6)
                            + "-" +
                            givenPhoneNumber.substring(6, 10);
                } else if (hyphenCount == 2) {
                    if(givenPhoneNumber.length() != 12 || givenPhoneNumber.charAt(3) != '-' || givenPhoneNumber.charAt(7) != '-') throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
                } else {
                    throw new RuntimeException(String.format("Given phone number: %1$s is in wrong format", givenPhoneNumber));
                }

                Optional<User> optionalUser = userRepository.findByPhoneNumber(givenPhoneNumber);
                if(optionalUser.isPresent()) throw new RuntimeException(String.format("User already present with the given phone number: %1$s", givenPhoneNumber));

                user.setPhoneNumber(givenPhoneNumber);

            } else {
                if(null == userId) throw new RuntimeException(String.format("Phone cannot be blank during save"));
            }


            //Setting the state if not null
            if (null != userRequestDto.getStateId()) {
                final Optional<State> optionalState = stateRepository.findById(userRequestDto.getStateId());
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
                if (null == userId) throw new RuntimeException("State cannot be null");
            }

            //Setting the country if not null
            if (null != userRequestDto.getCountryId()) {
                final Optional<Country> optionalCountry = countryRepository.findById(userRequestDto.getCountryId());
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
                if (null == userId) throw new RuntimeException("State cannot be null");
            }

            savedUser = userRepository.save(user);

        } catch (Exception e) {
            log.error("Error occurred during perform save method {}", e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }

        return savedUser;
    }

    private boolean isInPasswordFormat(String password) {
        if (password == null) {
            return false;
        }
        // Define the regular expression for password validation
        String passwordPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$";

        // Validate the password against the pattern
        return password.matches(passwordPattern);
    }
}