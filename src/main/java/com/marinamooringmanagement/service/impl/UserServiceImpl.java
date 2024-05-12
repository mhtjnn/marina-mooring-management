package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.UserSearchRequest;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationDetails;
import com.marinamooringmanagement.service.UserService;
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

    /**
     * Fetches users based on the provided search criteria.
     *
     * @param userSearchRequest An instance of {@code UserSearchRequest} containing the search criteria.
     * @return A {@code BasicRestResponse} object containing the response data, including the list of users matching the search criteria.
     * @throws IllegalArgumentException if {@code userSearchRequest} is {@code null}.
     * @implNote This method interacts with the database to retrieve users based on the search criteria provided in the {@code userSearchRequest}.
     * It constructs a {@code BasicRestResponse} object with information about the status of the operation and the fetched user data.
     * @apiNote The returned {@code BasicRestResponse} includes a list of {@code UserResponseDto} objects representing the fetched users.
     * @see UserSearchRequest
     * @see BasicRestResponse
     * @see User
     * @see UserResponseDto
     */
    @Override
    public BasicRestResponse fetchUsers(final UserSearchRequest userSearchRequest, final Integer customerAdminId) {

        final String role = getLoggedInUserRole();

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            Specification<User> spec = new Specification<User>() {
                @Override
                public Predicate toPredicate(Root<User> user, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                    List<Predicate> predicates = new ArrayList<>();

                    if (null != userSearchRequest.getId()) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(user.get("id"), "%" + userSearchRequest.getId() + "%")));
                    }
                    if (StringUtils.isNotEmpty(userSearchRequest.getName())) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(user.get("name"), "%" + userSearchRequest.getName() + "%")));
                    }
                    if (StringUtils.isNotEmpty(userSearchRequest.getEmail())) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(user.get("email"), "%" + userSearchRequest.getEmail() + "%")));
                    }
                    if (StringUtils.isNotEmpty(userSearchRequest.getPhoneNumber())) {
                        predicates.add(criteriaBuilder.and(criteriaBuilder.like(user.get("phoneNumber"), "%" + userSearchRequest.getPhoneNumber() + "%")));
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            List<User> userList = userRepository.findAll(spec);

            List<UserResponseDto> filteredUsers = null;

            if (role.equals(AppConstants.Role.OWNER)) {
                if (customerAdminId != null) {
                    filteredUsers = findByCustomerAdminID(customerAdminId, userList);
                } else {
                    filteredUsers = findByCUSTOMER_ADMINRole(userList);
                }
            } else if (role.equals(AppConstants.Role.CUSTOMER_ADMIN)) {
                filteredUsers = findByCustomerAdminID(customerAdminId, userList);
            } else {
                throw new RuntimeException("Not Authorized!!!");
            }

            final Pageable p = PageRequest.of(userSearchRequest.getPageNumber(), userSearchRequest.getPageSize(), userSearchRequest.getSort());
            Page<UserResponseDto> pageOfUser = new PageImpl<>(filteredUsers, p, filteredUsers.size());

            response.setContent(pageOfUser);
            response.setMessage("Users fetched successfully");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    private List<UserResponseDto> findByCUSTOMER_ADMINRole(List<User> userList) {
        return userList.stream()
                .filter(user -> user.getRole().getName().equals(AppConstants.Role.CUSTOMER_ADMIN))
                .map(user -> {
                    UserResponseDto userResponseDto = UserResponseDto.builder().build();
                    mapper.maptToUserResponseDto(userResponseDto, user);
                    userResponseDto.setRole(user.getRole().getName());
                    if (null != user.getState()) userResponseDto.setState(user.getState().getName());
                    else userResponseDto.setState("state");
                    if (null != user.getCountry()) userResponseDto.setCountry(user.getCountry().getName());
                    else userResponseDto.setCountry("country");
                    return userResponseDto;
                })
                .toList();
    }

    private List<UserResponseDto> findByCustomerAdminID(Integer customerAdminId, List<User> userList) {
        return userList.stream()
                .filter(user -> null != user.getCustomerAdminId() && user.getCustomerAdminId().equals(customerAdminId))
                .map(user -> {
                    UserResponseDto userResponseDto = UserResponseDto.builder().build();
                    mapper.maptToUserResponseDto(userResponseDto, user);
                    userResponseDto.setRole(user.getRole().getName());
                    if (null != user.getState()) userResponseDto.setState(user.getState().getName());
                    else userResponseDto.setState("state");
                    if (null != user.getCountry()) userResponseDto.setCountry(user.getCountry().getName());
                    else userResponseDto.setCountry("country");
                    return userResponseDto;
                })
                .toList();
    }

    /**
     * Function to save User in the database
     *
     * @param userRequestDto {@link UserRequestDto}
     * @return
     */
    @Override
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final Integer customerAdminId) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            final User user = User.builder().build();
            final Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());

            if (optionalUser.isPresent()) {
                log.info(String.format("Email already presenti"));
                throw new RuntimeException("Email already present");
            }

            log.info(String.format("saving user in DB"));
            performSave(userRequestDto, user, null, customerAdminId);

            response.setMessage("User saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Function to delete User from the database
     *
     * @param userId ID of the user which needs deletion.
     */
    @Override
    public BasicRestResponse deleteUser(final Integer userId, final Integer customerAdminId) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));

            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isEmpty()) throw new ResourceNotFoundException("No user found with the given ID");

            User userToBeDeleted = optionalUser.get();

            if(!checkForAuthority(customerAdminId, userToBeDeleted.getRole().getName())) throw new RuntimeException("Not authorized");

            boolean roleTechnicianOrFinance = userToBeDeleted.getRole().getName().equals(AppConstants.Role.TECHNICIAN)
                    || userToBeDeleted.getRole().getName().equals(AppConstants.Role.FINANCE);

            if(roleTechnicianOrFinance) {
                if(!Objects.equals(userToBeDeleted.getCustomerAdminId(), getLoggedInUserID())) throw new RuntimeException("Not authorized to delete user with different customer admin ID");
            }

            if(userToBeDeleted.getRole().getName().equals(AppConstants.Role.CUSTOMER_ADMIN)) {
                userRepository.deleteAll(userRepository.findAll().stream().filter(user ->  null != user.getCustomerAdminId() && user.getCustomerAdminId().equals(userToBeDeleted.getId())).toList());
            }

            final List<Token> tokenList = tokenRepository.findByUserId(userId);
            tokenRepository.deleteAll(tokenList);
            userRepository.deleteById(userId);
            response.setMessage("User Deleted Successfully!!!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private UserRequestDto customMapToUserRequestDto(final User user) {
        return UserRequestDto.builder()
                .id(user.getId())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, Integer userId, final Integer customerAdminId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                final User user = optionalUser.get();
                log.info(String.format("update user"));
                if (null != userRequestDto.getEmail() && !userRequestDto.getEmail().equals(user.getEmail())) {
                    response.setMessage("Email cannot be changed!!!");
                    response.setStatus(400);
                    return response;
                }
                performSave(userRequestDto, user, userId, customerAdminId);
                response.setMessage("User updated successfully!!!");
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new DBOperationException("No User found with given User ID");
            }
        } catch (Exception e) {
            response.setMessage("Error occurred while updating the user");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Function to find Email Address of a user from the database
     *
     * @param email Email given by the user
     * @return {@link UserDto}
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
     * Function to update password for the {@link User} having email as subject of the token.
     *
     * @param token              Reset Password Token
     * @param newPasswordRequest {@link NewPasswordRequest}
     * @return {@link NewPasswordResponse}
     * @throws Exception
     */
    @Override
    public BasicRestResponse updatePassword(final String token, final NewPasswordRequest newPasswordRequest) throws Exception {
        final BasicRestResponse passwordResponse = BasicRestResponse.builder().build();
        passwordResponse.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String email = jwtUtil.getUsernameFromToken(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new ResourceNotFoundException("No User found with the given email ID");
            }

            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Token is invalid");
            }
            if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
                passwordResponse.setMessage("Confirm password doesn't match with New password");
                passwordResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            } else {
                final User user = optionalUser.get();
                if (passwordEncoder.matches(newPasswordRequest.getNewPassword(), user.getPassword())) {
                    passwordResponse.setMessage("New password is same as old password");
                    passwordResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                } else {
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
     * Function to validate email and token.
     *
     * @param token Reset Password Token
     * @return {@link SendEmailResponse}
     */
    @Override
    public BasicRestResponse checkEmailAndTokenValid(final String token) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String email = jwtUtil.getUsernameFromToken(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new ResourceNotFoundException("No user found with the email extracted from token");
            }
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
     * Helper function to save the user in the database also update the existing user
     *
     * @param userRequestDto {@link UserResponseDto}
     * @param user           {@link User}
     */
    public User performSave(final UserRequestDto userRequestDto, final User user, final Integer userId , Integer customerAdminId) {

        try {
            if (!checkForAuthority(customerAdminId, userRequestDto.getRole()))
                throw new RuntimeException("Not authorized!!!");

            if(null == userId) user.setCreationDate(new Date(System.currentTimeMillis()));

            mapper.mapToUser(user, userRequestDto);
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));

            if(customerAdminId == null) customerAdminId = getLoggedInUserID();

            if(null != customerAdminId) user.setCustomerAdminId(customerAdminId);

            if (null != userRequestDto.getPassword()) {
                if (userRequestDto.getConfirmPassword().isEmpty() || userRequestDto.getPassword().isBlank())
                    throw new RuntimeException("Confirm Password is empty");
                if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword()))
                    throw new RuntimeException("New Password and confirm password ");
                user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            }

            if (null != userRequestDto.getRole()) {
                final Optional<Role> optionalRole = roleRepository.findByName(userRequestDto.getRole());
                if (optionalRole.isEmpty())
                    throw new ResourceNotFoundException("No Role found with the given role");
                final Role savedRole = optionalRole.get();
                user.setRole(savedRole);

                if (user.getRole().getName().equals(AppConstants.Role.OWNER) || user.getRole().getName().equals(AppConstants.Role.CUSTOMER_ADMIN)) user.setCustomerAdminId(null);
            }

            if (null != userRequestDto.getState()) {
                final Optional<State> optionalState = stateRepository.findByName(userRequestDto.getState());
                if (optionalState.isPresent()) {
                    user.setState(optionalState.get());
                } else {
                    State state = State.builder()
                            .name(userRequestDto.getState())
                            .build();
                    user.setState(stateRepository.save(state));
                }
            }

            if (null != userRequestDto.getCountry()) {
                final Optional<Country> optionalCountry = countryRepository.findByName(userRequestDto.getCountry());
                if (optionalCountry.isPresent()) {
                    user.setCountry(optionalCountry.get());
                } else {
                    Country country = Country.builder()
                            .name(userRequestDto.getCountry())
                            .build();
                    user.setCountry(countryRepository.save(country));
                }
            }


        } catch (Exception e) {
            log.error("Error occurred during perform save method {}", e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }

        return userRepository.save(user);
    }

    public boolean checkForAuthority(Integer customerAdminId, final String role) {
        String loggedInUserRole = getLoggedInUserRole();

        boolean roleTechnicianOrFinance = role.equals(AppConstants.Role.TECHNICIAN)
                || role.equals(AppConstants.Role.FINANCE);

        if (loggedInUserRole.equals(AppConstants.Role.CUSTOMER_ADMIN)) {
            return roleTechnicianOrFinance;
        } else if (loggedInUserRole.equals(AppConstants.Role.OWNER)) {
            if(roleTechnicianOrFinance) {
                if(null == customerAdminId) throw new RuntimeException("Customer Admin ID not provided");
            }
        } else {
            return false;
        }

        return true;
    }

    public boolean findByEmailId(final String emailId) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(emailId);
            return optionalUser.isPresent();
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    protected String getLoggedInUserRole() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserRole();
    }

    protected Integer getLoggedInUserID() {
        final AuthenticationDetails authDetails = (AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return authDetails.getLoggedInUserId();
    }

}
