package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.NewPasswordResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private UserMapper mapper;

    /**
     * Fetches all users in paginated and sorted form.
     *
     * @param pageNumber The page number
     * @param pageSize   The page size
     * @param sortBy     The field to sort by
     * @param sortDir    The sort direction ("asc" or "desc")
     * @return List of UserResponseDto objects
     */
    @Override
    public BasicRestResponse fetchUsers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortDir
    ) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            Sort sort = null;
            if (sortDir.equalsIgnoreCase("asc")) {
                sort = Sort.by(sortBy).ascending();
            } else {
                sort = Sort.by(sortBy).descending();
            }
            Pageable p = PageRequest.of(pageNumber, pageSize, sort);
            Page<User> userList = userRepository.findAll(p);
            log.info(String.format("fetch all users"));
            List<UserResponseDto> userResponseDtoList = new ArrayList<>();
            if (!userList.isEmpty())
                userResponseDtoList = userList.stream().map(this::customMapToUserResponseDto).collect(Collectors.toList());
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
            UserResponseDto userResponseDto = UserResponseDto.builder().build();

            userResponseDto.setFirstname(user.getFirstname());
            userResponseDto.setLastname(user.getLastname());
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
    public BasicRestResponse saveUser(UserRequestDto userRequestDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final User user = User.builder().build();
            Optional<User> optionalEmp = userRepository.findByEmail(user.getEmail());

            if (optionalEmp.isPresent()) {
                log.info(String.format("Email already present in DB"));
                throw new RuntimeException("Email already present in DB");
            }

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
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            log.info(String.format("delete user with given userId"));
            List<Token> tokenList = tokenRepository.findByUserId(userId);
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
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            User user = null;
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
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
    public BasicRestResponse updatePassword(String token, NewPasswordRequest newPasswordRequest) throws Exception {
        BasicRestResponse passwordResponse = BasicRestResponse.builder().build();
        passwordResponse.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            String email = jwtUtil.getUsernameFromToken(token);
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
    public BasicRestResponse checkEmailAndTokenValid(String token) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            String email = jwtUtil.getUsernameFromToken(token);
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
    public User performSave(UserRequestDto userRequestDto, User user, Integer userId) {
        mapper.mapToUser(user, userRequestDto);
        user.setLastModifiedDate(new Date(System.currentTimeMillis()));
        if (userId != null) {
            return userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            user.setCreationDate(new Date());
            Role role = roleRepository.findByName("ADMINISTRATOR");
            user.setRole(role);
            return userRepository.save(user);
        }
    }
}
