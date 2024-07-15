package com.marinamooringmanagement.service;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.metadata.State;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.*;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.model.AuthenticationDetails;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.JwtUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.impl.UserServiceImpl;
import com.marinamooringmanagement.utils.SortUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private LoggedInUserUtil loggedInUserUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private SortUtils sortUtils;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private AuthorizationUtil authorizationUtil;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void fetchUsers() {
        BaseSearchRequest baseSearchRequest = baseSearchRequestInstance();

        UserResponseDto userResponseDto = userResponseDtoInstance();
        List<UserResponseDto> userResponseDtoList=new ArrayList<>();
        userResponseDtoList.add(userResponseDto);

        BasicRestResponse basicRestResponse= BasicRestResponse.builder().build();
        basicRestResponse.setContent(userResponseDtoList);

        User user=userInstance();
        List<User> userList = new ArrayList<>();
        userList.add(user);
        Sort sort = Sort.by("id");

        Page<User> userPage = new PageImpl<>(userList);

        when(sortUtils.getSort(anyString(), anyString())).thenReturn(sort);
        when(request.getIntHeader("CUSTOMER_OWNER_ID")).thenReturn(1);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        BasicRestResponse result = userServiceImpl.fetchUsers(baseSearchRequest, "", request);

        assertEquals("Users fetched successfully", result.getMessage());

        List<UserResponseDto> resultContent = (List<UserResponseDto>) result.getContent();

        assertEquals(userResponseDtoList.size(), resultContent.size());

        verify(userRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void saveUser() {
        UserRequestDto userRequestDto = userRequestDtoInstance();
        User user=userInstance();
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authorizationUtil.checkAuthority(anyInt())).thenReturn(user);
        when(roleRepository.findById(userRequestDto.getRoleId())).thenReturn(Optional.of(roleInstance()));
        when(loggedInUserUtil.getLoggedInUserRole()).thenReturn("ADMINISTRATOR");
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("12345432");
        when(stateRepository.findById(1)).thenReturn(Optional.ofNullable(newStateInstance()));
        when(countryRepository.findById(1)).thenReturn(Optional.ofNullable(newCountryInstance()));
        when(request.getIntHeader("CUSTOMER_OWNER_ID")).thenReturn(1);
        User savedUser = userServiceImpl.performSave(userRequestDto, user, null, request);
        assertEquals(savedUser.getEmail(), user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        AuthenticationDetails auth=new AuthenticationDetails();
        auth.setLoggedInUserRole("OWNER");
        auth.setLoggedInUserId(1);
        when(authentication.getDetails()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        User user = userInstance();

        when(authorizationUtil.checkAuthority(anyInt())).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(request.getIntHeader("CUSTOMER_OWNER_ID")).thenReturn(1);

        BasicRestResponse result = userServiceImpl.deleteUser(1, request);

        assertEquals("User Deleted Successfully!!!", result.getMessage());
        assertEquals(200, result.getStatus());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());

    }



    @Test
    void updateUser() {
        UserRequestDto userRequestDto = userRequestDtoInstance();
        userRequestDto.setEmail("test@example.com");
        userRequestDto.setPassword(null);

        User user = userInstance();
        user.setEmail("test@example.com");

        when(userRepository.save(user)).thenReturn(user);
        when(roleRepository.findById(userRequestDto.getRoleId())).thenReturn(Optional.of(roleInstance()));
        when(loggedInUserUtil.getLoggedInUserRole()).thenReturn("ADMINISTRATOR");
        when(stateRepository.findById(1)).thenReturn(Optional.ofNullable(newStateInstance()));
        when(authorizationUtil.checkAuthority(anyInt())).thenReturn(user);
        when(countryRepository.findById(1)).thenReturn(Optional.ofNullable(newCountryInstance()));

        User result = userServiceImpl.performSave(userRequestDto, user, 1, request);

        assertEquals(userRequestDto.getName(),result.getName());
        assertEquals(userRequestDto.getEmail(),result.getEmail());

        verify(userRepository, times(1)).save(user);

    }

    @Test
    void findByEmailAddress() {
        User user = userInstance();

        UserDto userDto = new UserDto();
        userDto.setEmail("user@example.com");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.mapToUserDto(any(UserDto.class), eq(user))).thenReturn(userDto);

        UserDto result = userServiceImpl.findByEmailAddress(userDto.getEmail());

        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userMapper, times(1)).mapToUserDto(any(UserDto.class), eq(user));
    }

    @Test
    void updatePassword() throws Exception {
        User user=userInstance();
        user.setPassword("password");

        NewPasswordRequest newPasswordRequest=NewPasswordRequest.builder()
                .newPassword("new Password")
                .confirmPassword("new Password")
                .build();

        when(jwtUtil.getUsernameFromToken(any(String.class))).thenReturn("user@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(jwtUtil.validateToken("user@example.com")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        BasicRestResponse basicRestResponse=userServiceImpl.updatePassword("user@example.com",newPasswordRequest);

        assertEquals("Password changed Successfully!!!",basicRestResponse.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    void checkEmailAndTokenValid() {

        User user=userInstance();

        when(jwtUtil.getUsernameFromToken(any(String.class))).thenReturn("user@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(jwtUtil.validateToken("user@example.com")).thenReturn(true);

        BasicRestResponse basicRestResponse=userServiceImpl.checkEmailAndTokenValid("user@example.com");

        assertEquals("Email and Token Valid. Please proceed ahead...",basicRestResponse.getMessage());

        verify(userRepository,times(1)).findByEmail(user.getEmail());
    }

    public RoleResponseDto roleResponseDtoInstance() {
        return RoleResponseDto.builder()
                .id(2)
                .name("CUSTOMER_OWNER")
                .description("Customer Owner")
                .build();

    }

    public UserResponseDto userResponseDtoInstance(){
        UserResponseDto userResponseDto=UserResponseDto.builder().build();
        userResponseDto.setCustomerOwnerId(1);
        userResponseDto.setEmail("user@example.com");
        userResponseDto.setName("user");
        userResponseDto.setRoleResponseDto(roleResponseDtoInstance());
        return userResponseDto;
    }

    public Role roleInstance(){
        Role role=Role.builder().build();
        role.setName(AppConstants.Role.CUSTOMER_OWNER);
        return role;
    }

    public  User userInstance()
    {
        User user=User.builder().build();
        user.setId(1);
        user.setCustomerOwnerId(1);
        user.setName("User");
        user.setEmail("user@example.com");
        user.setRole(roleInstance());
        user.setState(newStateInstance());
        user.setCountry(newCountryInstance());
        user.setCompanyName("Test");

        return user;

    }

    public BaseSearchRequest baseSearchRequestInstance(){
        BaseSearchRequest baseSearchRequest=BaseSearchRequest.builder().build();
        baseSearchRequest.setPageNumber(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM));
        baseSearchRequest.setPageSize(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE));
        baseSearchRequest.setSortBy("id");
        baseSearchRequest.setSortDir("asc");
        return baseSearchRequest;
    }

    public UserRequestDto userRequestDtoInstance(){
        UserRequestDto userRequestDto=UserRequestDto.builder().build();

        userRequestDto.setId(1);
        userRequestDto.setName("User");
        userRequestDto.setEmail("user@example.com");
        userRequestDto.setPassword("VGVzdGluZ0AxMzQ=");
        userRequestDto.setConfirmPassword("VGVzdGluZ0AxMzQ=");
        userRequestDto.setCompanyName("Test");
        userRequestDto.setRoleId(3);
        userRequestDto.setStateId(1);
        userRequestDto.setCountryId(1);

        return userRequestDto;
    }

    public State newStateInstance() {
        return State.builder()
                .name("New York")
                .label("New York")
                .build();
    }

    public Country newCountryInstance() {
        return Country.builder()
                .name("USA")
                .label("United States of America")
                .build();
    }
}