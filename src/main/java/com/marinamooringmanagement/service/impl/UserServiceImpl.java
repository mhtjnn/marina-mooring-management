package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.SendEmailResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public List<UserResponseDto> getAllUser(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortDir
    ) {
        Sort sort = null;
        if (sortDir.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }
        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> userList = userRepository.findAll(p);
        log.info(String.format("fetch all employees"));
        List<UserResponseDto> userResponseDtoList = userList.stream().map(this::mapToUserResponseDto).collect(Collectors.toList());
        return userResponseDtoList;
    }

    /**
     * Maps a User entity to a UserResponseDto object.
     *
     * @param user The User entity
     * @return UserResponseDto object
     */
    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto userResponseDto = UserResponseDto.builder().build();

        userResponseDto.setFirstname(user.getFirstname());
        userResponseDto.setLastname(user.getLastname());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setRole(user.getRole().getName());

        return userResponseDto;
    }

    /**
     * Function to save User in the database
     *
     * @param user {@link UserRequestDto}
     * @return
     */
    @Override
    public String saveUser(UserRequestDto user) {

        final User employee = com.marinamooringmanagement.model.entity.User.builder().build();
        Optional<User> optionalEmp = userRepository.findByEmail(user.getEmail());

        if (optionalEmp.isPresent()) {
            log.info(String.format("Email already present in DB"));
            return "Email Already Exists";
        }

        log.info(String.format("saving employee in DB"));
        performSave(user, employee, null);
        return "Employee Saved Successfully";
    }

    /**
     * Function to delete User from the database
     *
     * @param userId ID of the user which needs deletion.
     */
    @Override
    public BasicRestResponse deleteUser(Integer userId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        log.info(String.format("delete employee with given userId"));
        List<Token> tokenList = tokenRepository.findByUserId(userId);
        tokenRepository.deleteAll(tokenList);
        userRepository.deleteById(userId);
        response.setMessage("User Deleted Successfully!!!");
        response.setStatus(200);
        return response;
    }

    /**
     * Function to update user in the database
     *
     * @param userDto {@link UserRequestDto}
     */
    @Override
    public BasicRestResponse updateUser(UserRequestDto userDto) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        User user = userRepository.findById(userDto.getId()).get();
        log.info(String.format("update employee"));
        if(!userDto.getEmail().equals(user.getEmail())) {
            response.setMessage("Email cannot be changed!!!");
            response.setStatus(400);
        }
        performSave(userDto, user, userDto.getId());
        response.setMessage("User updated successfully!!!");
        response.setStatus(200);
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
        UserDto employee = null;
        if (null != email) {
            User empEntity = userRepository.findByEmail(email).get();
            if (null != empEntity) {
                employee = mapper.mapToUserDto(UserDto.builder().build(), empEntity);
            }
        }
        return employee;
    }

    /**
     * Function to update password for the {@link User} having email as subject of the token.
     * @param token Reset Password Token
     * @param newPasswordRequest {@link NewPasswordRequest}
     * @return {@link NewPasswordResponse}
     * @throws Exception
     */
    @Override
    public NewPasswordResponse updatePassword(String token, NewPasswordRequest newPasswordRequest) throws Exception {
        try {
            final NewPasswordResponse passwordResponse = new NewPasswordResponse();
            String email = jwtUtil.getUsernameFromToken(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                passwordResponse.setResponse("User with given email doesn't exist!!!");
                passwordResponse.setSuccess(false);
                return passwordResponse;
            } else {
                if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
                    passwordResponse.setResponse("Confirm password doesn't match with New password");
                    passwordResponse.setSuccess(false);
                    return passwordResponse;
                } else {
                    final User user = optionalUser.get();
                    if(passwordEncoder.matches(newPasswordRequest.getNewPassword(), user.getPassword())) {
                        passwordResponse.setResponse("New password is same as old password");
                        passwordResponse.setSuccess(false);
                        return passwordResponse;
                    }
                    user.setPassword(passwordEncoder.encode(newPasswordRequest.getConfirmPassword()));
                    userRepository.save(user);
                    passwordResponse.setResponse("Password changed Successfully!!!");
                    passwordResponse.setSuccess(true);
                    return passwordResponse;
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * Function to validate email and token.
     * @param token Reset Password Token
     * @return {@link SendEmailResponse}
     */
    @Override
    public SendEmailResponse checkEmailAndTokenValid(String token) {
        final SendEmailResponse response = new SendEmailResponse();
        String email = jwtUtil.getUsernameFromToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            response.setResponse("No User found with given email!!!");
            response.setSuccess(false);
            return response;
        }

        if(!jwtUtil.validateToken(token)) {
            response.setResponse("Invalid Token!!!");
            response.setSuccess(false);
            return response;
        }

        response.setResponse("Email and Token Valid. Please proceed ahead...");
        response.setSuccess(true);
        return response;
    }

    /**
     * Helper function to save the user in the database also update the existing user
     *
     * @param userRequestDto {@link UserResponseDto}
     * @param user {@link User}
     * @param userId ID of the user which requires update.
     */
    private void performSave(UserRequestDto userRequestDto, User user, Integer userId) {
        User savedUser = null;
        if (userRequestDto.getId() != null) {
            Optional<User> optionalUser = userRepository.findById(userRequestDto.getId());
            if (optionalUser.isPresent()) {
                savedUser = optionalUser.get();
                mapper.mapToUser(savedUser, userRequestDto);
                user.setLastModifiedDate(new Date(System.currentTimeMillis()));
                userRepository.save(savedUser);
            } else {
                throw new RuntimeException("User NOT FOUND!!!");
            }
        } else {
            mapper.mapToUser(user, userRequestDto);
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            user.setCreationDate(new Date());
            user.setLastModifiedDate(new Date());
            Role role = Role.builder().build();
            role.setId(1);
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
