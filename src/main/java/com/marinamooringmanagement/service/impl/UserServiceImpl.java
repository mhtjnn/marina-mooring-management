package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.dto.RoleDto;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.request.NewPasswordRequest;
import com.marinamooringmanagement.request.UserRequestDto;
import com.marinamooringmanagement.response.EmailLinkResponse;
import com.marinamooringmanagement.response.NewPasswordResponse;
import com.marinamooringmanagement.response.UserResponseDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.service.UserService;
import org.aspectj.weaver.bcel.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
        Page<User> employeeList = userRepository.findAll(p);
        log.info(String.format("fetch all employees"));
        List<UserResponseDto> userResponseDtoList = employeeList.stream().map(this::mapToUserResponseDto).collect(Collectors.toList());
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
     * Function to map User Entity to User Dto
     *
     * @param employee
     * @return {@link UserDto}
     */
    private UserDto mapToUserDto(User employee) {
        UserDto userDto = UserDto.builder().build();
        mapper.mapToUserDto(userDto, employee);

        RoleDto roleDto = RoleDto.builder().build();

        Optional<Role> optionalRole = roleRepository.findById(employee.getRole().getId());

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            roleDto.setId(employee.getRole().getId());
            roleDto.setName(role.getName());
            roleDto.setDescription(role.getDescription());
            userDto.setRole(roleDto);
        } else {
            throw new RuntimeException("Role NOT FOUND!!!");
        }

        return userDto;
    }

    /**
     * Function to save User in the database
     *
     * @param user
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
     * @param empId
     */
    @Override
    public void deleteUser(Integer empId) {
        log.info(String.format("delete employee with given empId"));
        userRepository.deleteById(empId);
    }

    /**
     * Function to update user in the database
     *
     * @param userDto
     */
    @Override
    public void updateUser(UserRequestDto userDto) {
        User user = userRepository.findById(userDto.getId()).get();
        log.info(String.format("update employee"));
        performSave(userDto, user, userDto.getId());
    }

    /**
     * Function to find Email Address of a user from the database
     *
     * @param email
     * @return
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
                    passwordResponse.setResponse("New password doesn't match");
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

    @Override
    public EmailLinkResponse checkEmailAndTokenValid(String token) {
        final EmailLinkResponse response = new EmailLinkResponse();
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
     * @param userRequestDto
     * @param user
     * @param userId
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
