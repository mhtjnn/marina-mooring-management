package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.UserSearchRequest;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import com.marinamooringmanagement.model.response.NewPasswordResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public BasicRestResponse fetchUsers(final UserSearchRequest userSearchRequest) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Pageable p = PageRequest.of(userSearchRequest.getPageNumber(), userSearchRequest.getPageSize(), userSearchRequest.getSort());
            final Page<User> userList = userRepository.findAll(p);
            log.info("fetch all users");
            List<UserResponseDto> userResponseDtoList = new ArrayList<>();
            if (!userList.isEmpty())
                userResponseDtoList = userList.getContent().stream().map(this::customMapToUserResponseDto).collect(Collectors.toList());
            response.setMessage("Users fetched Successfully");
            response.setStatus(HttpStatus.OK.value());
            response.setContent(userResponseDtoList);
        } catch (Exception e) {
            response.setMessage("Error Occurred while fetching user from the database");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setErrorList(List.of(e.getMessage()));
        }
        return response;
    }

    /**
     * Maps a User entity to a UserResponseDto object.
     *
     * @param user The User entity
     * @return UserResponseDto object
     */
    public UserResponseDto customMapToUserResponseDto(User user) {
        if (null != user) {
            final UserResponseDto userResponseDto = UserResponseDto.builder().build();

            userResponseDto.setUserID(user.getUserID());
            userResponseDto.setCustomerAdminId(user.getCustomerAdminId());
            userResponseDto.setId(user.getId());
            userResponseDto.setName(user.getName());
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setPhoneNumber(user.getPhoneNumber());
            userResponseDto.setRole(user.getRole().getName());

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
//        String role = getRoleFromRequest(request);
//
//        if(null == role) throw new ResourceNotFoundException("No Role found in the given token");

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final User user = User.builder().build();
            final Optional<User> optionalUser = userRepository.findByEmail(userRequestDto.getEmail());

            if (optionalUser.isPresent()) {
                log.info(String.format("Email already present in DB"));
                throw new RuntimeException("Email already present in DB");
            }

//            if(userRequestDto.getCustomerAdminId().isEmpty() && !role.equals("OWNER")) throw new RuntimeException("NOT Authorized to save the user");
//            else if(!userRequestDto.getCustomerAdminId().isEmpty()) {
//
//                final Integer tokenId = jwtUtil.getUserIdFromToken(request.getHeader("Authorization").substring(7));
//
//                Optional<User> optionalUserFromtokenId =
//
//            }

            log.info(String.format("saving user in DB"));
            performSave(userRequestDto, user, null);

            response.setMessage("User saved successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setMessage("Error Occurred while saving user");
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
    public BasicRestResponse deleteUser(Integer userId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));
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

    /**
     * Function to update user in the database
     *
     * @param userDto {@link UserRequestDto}
     */
    @Override
    public BasicRestResponse updateUser(UserRequestDto userDto, Integer userId) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                final User user = optionalUser.get();
                log.info(String.format("update user"));
                if ((null != userDto.getEmail() && !userDto.getEmail().equals(user.getEmail())) ||
                        (null != userDto.getPassword() && !userDto.getPassword().equals(user.getPassword()))) {
                    response.setMessage("Email or Password cannot be changed!!!");
                    response.setStatus(400);
                    return response;
                }
                performSave(userDto, user, userDto.getId());
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
    public User performSave(final UserRequestDto userRequestDto, final User user, final Integer userId) {
        try {
            mapper.mapToUser(user, userRequestDto);
            user.setLastModifiedDate(new Date(System.currentTimeMillis()));
            if (userId != null) {
                return userRepository.save(user);
            } else {
                user.setCreationDate(new Date());

                if(null != userRequestDto.getPassword()) user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

                if(null != userRequestDto.getRole()) {
                    final Optional<Role> optionalRole = roleRepository.findByName(userRequestDto.getRole());
                    if (optionalRole.isEmpty()) throw new ResourceNotFoundException("No Role found with the given role");
                    final Role role = optionalRole.get();
                    user.setRole(role);
                }

                if(null != userRequestDto.getState()) {
                    final Optional<State> optionalState = stateRepository.findByName(userRequestDto.getState());
                    if(optionalState.isPresent()) {
                        user.setState(optionalState.get());
                    } else {
                        State state = State.builder()
                                .name(userRequestDto.getState())
                                .build();
                        user.setState(stateRepository.save(state));
                    }
                }

                if(null != userRequestDto.getCountry()) {
                    final Optional<Country> optionalCountry = countryRepository.findByName(userRequestDto.getCountry());
                    if(optionalCountry.isPresent()) {
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
            throw new RuntimeException("Error occurred during perform save method", e);
        }
    }

    public String getRoleFromRequest(HttpServletRequest request) {
        return jwtUtil.getRoleFromToken(request.getHeader("Authorization").substring(7));
    }
}