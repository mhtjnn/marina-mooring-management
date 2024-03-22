package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.dto.RoleDto;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.request.UserRequestDto;
import com.marinamooringmanagement.response.UserResponseDto;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper mapper;

    /**
     * Fetches all users in paginated and sorted form.
     * @param pageNumber The page number
     * @param pageSize The page size
     * @param sortBy The field to sort by
     * @param sortDir The sort direction ("asc" or "desc")
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
        if(sortDir.equalsIgnoreCase("asc")) {
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
     * @param employee
     * @return {@link UserDto}
     */
    private UserDto mapToUserDto(User employee) {
        UserDto userDto = UserDto.builder().build();
        mapper.mapToUserDto(userDto, employee);

        RoleDto roleDto = RoleDto.builder().build();

        Optional<Role> optionalRole = roleRepository.findById(employee.getRole().getId());

        if(optionalRole.isPresent()) {
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
     * @param user
     * @return
     */
    @Override
    public String saveUser(UserRequestDto user) {

        final User employee = com.marinamooringmanagement.model.entity.User.builder().build();
        Optional<User> optionalEmp = userRepository.findByEmail(user.getEmail());

        if(optionalEmp.isPresent()) {
            log.info(String.format("Email already present in DB"));
            return "Email Already Exists";
        }

        log.info(String.format("saving employee in DB"));
        performSave(user, employee, null);
        return "Employee Saved Successfully";
    }

    /**
     * Functoin to delete User from the database
     * @param empId
     */
    @Override
    public void deleteUser(Integer empId) {
        log.info(String.format("delete employee with given empId"));
        userRepository.deleteById(empId);
    }

    /**
     * Function to update user in the database
     * @param userDto
     */
    @Override
    public void updateUser(UserRequestDto userDto) {
        User user = userRepository.findById(userDto.getId()).get();
        log.info(String.format("update employee"));
        performSave(userDto, user, userDto.getId());
    }

    /**
     * Functtion to find Email Address of a user from the database
     * @param email
     * @return
     */
    @Override
    public UserDto findByEmailAddress(String email) {
        UserDto employee = null;
        if(null != email) {
            User empEntity = userRepository.findByEmail(email).get();
            if(null != empEntity) {
                employee = mapper.mapToUserDto(UserDto.builder().build(), empEntity);
            }
        }
        return employee;
    }

    /**
     * Helper function to save the user in the database also update the existing user
     * @param userRequestDto
     * @param user
     * @param userId
     */
    private void performSave(UserRequestDto userRequestDto, User user, Integer userId) {
        User savedUser = null;

        if(userRequestDto.getId() != null) {
            Optional<User> optionalUser = userRepository.findById(userRequestDto.getId());
            if(optionalUser.isPresent()) {
                savedUser = optionalUser.get();
                mapper.mapToUser(savedUser, userRequestDto);
                userRepository.save(savedUser);
            } else {
                throw new RuntimeException("User NOT FOUND!!!");
            }
        } else {
            mapper.mapToUser(user, userRequestDto);
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

            Role role = Role.builder().build();
            role.setId(1);
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
