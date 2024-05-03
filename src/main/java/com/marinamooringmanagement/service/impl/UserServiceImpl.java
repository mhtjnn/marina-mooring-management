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
import com.marinamooringmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     *           It constructs a {@code BasicRestResponse} object with information about the status of the operation and the fetched user data.
     * @apiNote The returned {@code BasicRestResponse} includes a list of {@code UserResponseDto} objects representing the fetched users.
     * @see UserSearchRequest
     * @see BasicRestResponse
     * @see User
     * @see UserResponseDto
     */
    @Override
    public FetchUsersResponse fetchUsers(final UserSearchRequest userSearchRequest, final String customerAdminId, final HttpServletRequest request) {
        String role;
        Integer loggedInId;

        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            String jwt = bearerToken.substring(7);
            role = jwtUtil.getRoleFromToken(jwt);
            loggedInId = jwtUtil.getUserIdFromToken(jwt);
        } else {
            throw new RuntimeException("INVALID TOKEN");
        }

        if (role.isEmpty()) throw new ResourceNotFoundException("No Role found in the given token");

        if (null == loggedInId) throw new ResourceNotFoundException("No ID found in the given token");

        final FetchUsersResponse fetchUsersResponse = FetchUsersResponse.builder().build();
        fetchUsersResponse.setTime(new Timestamp(System.currentTimeMillis()));
        try {

            final Pageable p = PageRequest.of(userSearchRequest.getPageNumber(), userSearchRequest.getPageSize(), userSearchRequest.getSort());
            final Page<User> userList = userRepository.findAll(p);

            if (userList.isEmpty()) throw new ResourceNotFoundException("No Users found");

            List<UserResponseDto> userResponseDtoList = userList
                    .getContent()
                    .stream()
                    .map(this::customMapToUserResponseDto)
                    .toList();

            List<UserResponseDto> userWithSameCustomerAdminId = null;

            if (null != customerAdminId) {
                userWithSameCustomerAdminId = userResponseDtoList
                        .stream()
                        .filter(
                                user -> null != user.getCustomerAdminId()
                                        && !user.getCustomerAdminId().isEmpty()
                                        && user.getCustomerAdminId().equals(customerAdminId)
                                        && !Objects.equals(user.getId(), loggedInId)
                        )
                        .toList();
            }

            Page<UserResponseDto> userWithSameCustomerAdminIdPage = null;
            if (null != userWithSameCustomerAdminId) userWithSameCustomerAdminIdPage =
                    new PageImpl<>(userWithSameCustomerAdminId, p, userWithSameCustomerAdminId.size());

            if (role.equals(AppConstants.Role.CUSTOMER_ADMIN)) {
                return fetchUsersCustomerAdminRole(customerAdminId, fetchUsersResponse, userWithSameCustomerAdminIdPage);
            } else if (role.equals(AppConstants.Role.OWNER)) {
                return fetchUsersOwnerRole(userResponseDtoList, customerAdminId, p, userWithSameCustomerAdminId, loggedInId, userWithSameCustomerAdminIdPage, fetchUsersResponse);
            } else {
                throw new RuntimeException("Not authorized to fetch the users");
            }
        } catch (Exception e) {
            fetchUsersResponse.setMessage(e.getLocalizedMessage());
            fetchUsersResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return fetchUsersResponse;
    }

    /**
     * Fetches users for the customer admin role.
     *
     * @param customerAdminId           The customer admin ID.
     * @param fetchUsersResponse        The response object.
     * @param userWithSameCustomerAdminIdPage The page containing users with the same customer admin ID.
     * @return The response object containing fetched users.
     */
    private FetchUsersResponse fetchUsersCustomerAdminRole(
            final String customerAdminId,
            final FetchUsersResponse fetchUsersResponse,
            final Page<UserResponseDto> userWithSameCustomerAdminIdPage
    ) {
        try {
            if (null == customerAdminId || customerAdminId.isEmpty()) {
                throw new RuntimeException("Customer ID is null");
            }

            if (userWithSameCustomerAdminIdPage.isEmpty()) {
                throw new ResourceNotFoundException("No users found with the given customer ID");
            }

            fetchUsersResponse.setContent(List.of(userWithSameCustomerAdminIdPage));
            fetchUsersResponse.setMessage("Users fetched Successfully");
            fetchUsersResponse.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            fetchUsersResponse.setMessage(e.getLocalizedMessage());
            fetchUsersResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return fetchUsersResponse;
    }

    /**
     * Fetches users for the owner role.
     *
     * @param userResponseDtoList           The list of user response DTOs.
     * @param customerAdminId               The customer admin ID.
     * @param p                             The pageable object.
     * @param userWithSameCustomerAdminId   The list of users with the same customer admin ID.
     * @param loggedInId                    The logged-in user ID.
     * @param userWithSameCustomerAdminIdPage The page containing users with the same customer admin ID.
     * @param fetchUsersResponse            The response object.
     * @return The response object containing fetched users.
     */
    private FetchUsersResponse fetchUsersOwnerRole(
            final List<UserResponseDto> userResponseDtoList,
            String customerAdminId,
            final Pageable p,
            List<UserResponseDto> userWithSameCustomerAdminId,
            final Integer loggedInId,
            Page<UserResponseDto> userWithSameCustomerAdminIdPage,
            final FetchUsersResponse fetchUsersResponse
    ) {
        try {
            List<UserResponseDto> userWithCustomerAdminRole = userResponseDtoList
                    .stream()
                    .filter(
                            user -> null != user.getCustomerAdminId()
                                    && !user.getCustomerAdminId().isEmpty()
                                    && user.getRole().equals(AppConstants.Role.CUSTOMER_ADMIN)
                    )
                    .toList();

            Page<UserResponseDto> userWithCustomerAdminRolePage;
            userWithCustomerAdminRolePage = new PageImpl<>(userWithCustomerAdminRole, p, userWithCustomerAdminRole.size());

            if (null == customerAdminId || customerAdminId.isEmpty()) {
                customerAdminId = userWithCustomerAdminRole.get(0).getCustomerAdminId();
            }

            String finalCustomerAdminIdForOwner = customerAdminId;
            userWithSameCustomerAdminId = userResponseDtoList
                    .stream()
                    .filter(
                            user -> null != user.getCustomerAdminId()
                                    && !user.getCustomerAdminId().isEmpty()
                                    && user.getCustomerAdminId().equals(finalCustomerAdminIdForOwner)
                                    && !Objects.equals(user.getId(), loggedInId)
                                    && !user.getRole().equals(AppConstants.Role.OWNER)
                                    && !user.getRole().equals(AppConstants.Role.CUSTOMER_ADMIN)
                    )
                    .toList();

            userWithSameCustomerAdminIdPage = new PageImpl<>(userWithSameCustomerAdminId, p, userWithSameCustomerAdminId.size());

            fetchUsersResponse.setContent(List.of(userWithCustomerAdminRolePage, userWithSameCustomerAdminIdPage));
            fetchUsersResponse.setMessage("Users fetched Successfully");
            fetchUsersResponse.setStatus(HttpStatus.OK.value());

            fetchUsersResponse.setContent(List.of(userWithCustomerAdminRolePage, userWithSameCustomerAdminIdPage));
        } catch (Exception e) {
            fetchUsersResponse.setMessage(e.getLocalizedMessage());
            fetchUsersResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return fetchUsersResponse;
    }


    /**
     * Maps a User entity to a UserResponseDto object.
     *
     * @param user The User entity
     * @return UserResponseDto object
     */
    public UserResponseDto customMapToUserResponseDto(final User user) {
        if (null != user) {
            final UserResponseDto userResponseDto = UserResponseDto.builder().build();

            userResponseDto.setUserID(user.getUserID());
            userResponseDto.setCustomerAdminId(user.getCustomerAdminId());
            userResponseDto.setId(user.getId());
            userResponseDto.setName(user.getName());
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setPhoneNumber(user.getPhoneNumber());
            userResponseDto.setRole(user.getRole().getName());
            if (null != user.getState()) userResponseDto.setState(user.getState().getName());
            else userResponseDto.setState("state");
            if (null != user.getCountry()) userResponseDto.setCountry(user.getCountry().getName());
            else userResponseDto.setCountry("country");

            return userResponseDto;
        } else {
            throw new ResourceNotFoundException("User is null");
        }
    }

    /**
     * Function to save User in the database
     *
     * @param userRequestDto {@link UserRequestDto}
     * @return
     */
    @Override
    public BasicRestResponse saveUser(final UserRequestDto userRequestDto, final HttpServletRequest request) {

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            final User user = User.builder().build();
            final Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());

            if (optionalUser.isPresent()) {
                log.info(String.format("Email already present in DB"));
                throw new RuntimeException("Email already present in DB");
            }

            log.info(String.format("saving user in DB"));
            performSave(userRequestDto, user, null, request);

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
    public BasicRestResponse deleteUser(final Integer userId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));

            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isEmpty()) throw new ResourceNotFoundException("No user found with the given ID");

            User user = optionalUser.get();

            UserRequestDto userRequestDto = customMapToUserRequestDto(user);

            checkForAuthority(request, "delete", userRequestDto, user);

            final List<Token> tokenList = tokenRepository.findByUserId(userId);
            tokenRepository.deleteAll(tokenList);
            userRepository.deleteById(userId);
            response.setMessage("User Deleted Successfully!!!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage("Error occurred while deleting the user");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    private UserRequestDto customMapToUserRequestDto(final User user) {
        return UserRequestDto.builder()
                .id(user.getId())
                .role(user.getRole().getName())
                .customerAdminId(user.getCustomerAdminId())
                .build();
    }

    @Override
    public BasicRestResponse updateUser(final UserRequestDto userRequestDto, Integer userId, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                final User user = optionalUser.get();
                log.info(String.format("update user"));
                if ((null != userRequestDto.getEmail() && !userRequestDto.getEmail().equals(user.getEmail())) ||
                        (null != userRequestDto.getPassword() && !userRequestDto.getPassword().equals(user.getPassword()))) {
                    response.setMessage("Email or Password cannot be changed!!!");
                    response.setStatus(400);
                    return response;
                }
                performSave(userRequestDto, user, userRequestDto.getId(), request);
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
     * @param userId         ID of the user which requires update.
     */
    public User performSave(final UserRequestDto userRequestDto, final User user, final Integer userId, final HttpServletRequest request) {

        final String task = (userId == null) ? "save" : "update";

        try {
            checkForAuthority(request, task, userRequestDto, user);

            mapper.mapToUser(user, userRequestDto);
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));
            if (userId != null) {
                return userRepository.save(user);
            } else {
                user.setCreationDate(new Date());

                if (null != userRequestDto.getPassword()) user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

                if (null != userRequestDto.getRole()) {
                    final Optional<Role> optionalRole = roleRepository.findByName(userRequestDto.getRole());
                    if (optionalRole.isEmpty()) throw new ResourceNotFoundException("No Role found with the given role");
                    final Role savedRole = optionalRole.get();
                    user.setRole(savedRole);

                    if(user.getRole().getName().equals(AppConstants.Role.OWNER)) user.setCustomerAdminId(null);
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

                return userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("Error occurred during perform save method {}", e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public void checkForAuthority(final HttpServletRequest request, final String task, final UserRequestDto userRequestDto, final User user) {
        String role;
        Integer loggedInId;

        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            String jwt = bearerToken.substring(7);
            role = jwtUtil.getRoleFromToken(jwt);

            if (role.equals(AppConstants.Role.FINANCE) || role.equals(AppConstants.Role.TECHNICIAN))
                throw new RuntimeException(String.format("Not authorized to %s user", task));

            loggedInId = jwtUtil.getUserIdFromToken(jwt);
        } else throw new ResourceNotFoundException("Either token is null or of different type than bearer");

        if (role.isEmpty()) throw new ResourceNotFoundException("No Role found in the given token");

        if (null == loggedInId) throw new ResourceNotFoundException("No ID found in the given token");

        if (userRequestDto.getRole().isEmpty()) {
            if (null == user || null == user.getRole()) throw new ResourceNotFoundException("No Role found for the user to be updated");
            else userRequestDto.setRole(user.getRole().getName());
        }

        if (userRequestDto.getRole().equals(AppConstants.Role.CUSTOMER_ADMIN) && userRequestDto.getCustomerAdminId().isEmpty())
            throw new RuntimeException("Customer admin Id cannot be null");

        final Optional<User> optionalLoggedInUser = userRepository.findById(loggedInId);

        if (optionalLoggedInUser.isEmpty()) throw new ResourceNotFoundException("No user found with the ID in the token");

        if (role.equals(AppConstants.Role.CUSTOMER_ADMIN)) {
            if (userRequestDto.getRole().equals(AppConstants.Role.CUSTOMER_ADMIN) || userRequestDto.getRole().equals(AppConstants.Role.OWNER)) {
                throw new RuntimeException(String.format("Not authorized to %s customer administrator or owner", task));
            } else if (userRequestDto.getRole().equals(AppConstants.Role.FINANCE) || userRequestDto.getRole().equals(AppConstants.Role.TECHNICIAN)) {
                if (!userRequestDto.getCustomerAdminId().equals(optionalLoggedInUser.get().getCustomerAdminId())) {
                    throw new RuntimeException("Customer admin ID doesn't match");
                }
            }
        } else if (!role.equals(AppConstants.Role.OWNER)) {
            throw new RuntimeException("Role not defined in the database");
        }
    }

}
