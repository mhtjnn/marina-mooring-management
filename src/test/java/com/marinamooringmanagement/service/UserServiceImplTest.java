package com.marinamooringmanagement.service;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.entity.Role;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.request.UserRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.repositories.RoleRepository;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationDetails;
import com.marinamooringmanagement.service.impl.UserServiceImpl;
import com.marinamooringmanagement.utils.SortUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private TokenRepository tokenRepo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SortUtils sortUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_successfully_save_user() {
        final Role role = newRoleInstance();

        final User user = newUserInstance();

        final UserRequestDto userRequestDto = newUserRequestDtoInstance();

        when(userRepo.save(any(User.class))).thenReturn(user);

        when(roleRepo.findByName(any(String.class))).thenReturn(Optional.ofNullable(role));

        when(passwordEncoder.encode(null)).thenReturn("test");

//        when(SecurityContextHolder.)

        when(SecurityContextHolder.getContext().getAuthentication().getDetails()).thenReturn(newAuthenticationDetailsInstance());

//        when(service.getLoggedInUserRole()).thenReturn("OWNER");

        when(service.checkForAuthority(any(Integer.class), "OWNER")).thenReturn(true);

        final User savedUser = service.performSave(userRequestDto, user, null);

        Assertions.assertEquals(savedUser.getEmail(), user.getEmail());
        Assertions.assertEquals(savedUser.getName(), user.getName());
        Assertions.assertEquals(savedUser.getRole().getName(), user.getRole().getName());

        verify(userMapper, times(1)).mapToUser(any(User.class), any(UserRequestDto.class));
        verify(userRepo, times(1)).save(any(User.class));
        verify(roleRepo, times(1)).findByName(any(String.class));
        verify(passwordEncoder, times(1)).encode(null);
    }

    @Test
    public void should_fetch_all_users() {
        final List<UserResponseDto> userResponseDtoList = new ArrayList<>();

        final BaseSearchRequest baseSearchRequest =  newBaseSearchRequestInstance();

        final Role role = newRoleInstance();

        final UserResponseDto userResponseDto = newUserResponseDtoInstance();

        userResponseDtoList.add(userResponseDto);

        final User user = newUserInstance();

        final List<User> users = new ArrayList<>();
        users.add(user);

        final Page<User> pagedUserList = new PageImpl<>(users);

        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setContent(userResponseDtoList);

        when(userRepo.findAll(any(Pageable.class))).thenReturn(pagedUserList);

//        final BasicRestResponse getResponse = service.fetchUsers(baseSearchRequest);

//        Assertions.assertEquals(response.getContent(), getResponse.getContent());

        verify(userRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void should_successfully_update_user() {
        final User oldUser = newUserInstance();

        final User updateUser = newUserInstance();
        updateUser.setEmail("updated");

        final UserRequestDto userRequestDto = newUserRequestDtoInstance();
        userRequestDto.setEmail("updated");

        when(userRepo.save(any(User.class))).thenReturn(updateUser);

//        final User performSaveUser = service.performSave(userRequestDto, oldUser, 1);

//        Assertions.assertEquals(userRequestDto.getEmail(), performSaveUser.getEmail());

        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    public void should_successfully_update_password() throws Exception {
        final String token = "test";

        final NewPasswordRequest request = newNewPasswordRequestInstance();

        final User oldPasswordUser = newUserInstance();
        oldPasswordUser.setPassword("old");


        when(jwtUtil.getUsernameFromToken(any(String.class))).thenReturn("test");

        when(userRepo.findByEmail("test")).thenReturn(Optional.ofNullable(oldPasswordUser));

        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        final BasicRestResponse response = service.updatePassword(token, request);

        Assertions.assertEquals(response.getMessage(), String.format("Password changed Successfully!!!"));

        verify(userRepo, times(1)).save(any(User.class));
        verify(userRepo, times(1)).findByEmail(any(String.class));
        verify(jwtUtil, times(1)).getUsernameFromToken(any(String.class));
    }

    @Test
    public void should_successfully_check_email_token_valid() {
        final String token = "test";

        final User user = newUserInstance();

        when(jwtUtil.getUsernameFromToken(token)).thenReturn("test");

        when(userRepo.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(user));

        when(jwtUtil.validateToken(token)).thenReturn(true);

        final BasicRestResponse response = service.checkEmailAndTokenValid(token);

        Assertions.assertEquals(response.getMessage(), String.format("Email and Token Valid. Please proceed ahead..."));

        verify(userRepo, times(1)).findByEmail(any(String.class));
        verify(jwtUtil, times(1)).getUsernameFromToken(any(String.class));
        verify(jwtUtil, times(1)).validateToken(any(String.class));
    }

    public Role newRoleInstance() {
        return Role.builder()
                .name("test")
                .description("test")
                .build();
    }

    public User newUserInstance() {
        return User.builder()
                .name("test")
                .email("test")
                .role(newRoleInstance())
                .build();
    }

    public UserRequestDto newUserRequestDtoInstance() {
        return UserRequestDto.builder()
                .name("test")
                .email("test")
                .build();
    }

    public UserResponseDto newUserResponseDtoInstance() {
        return UserResponseDto.builder()
                .email("test")
                .name("test")
//                .role("test")
                .build();
    }

    public NewPasswordRequest newNewPasswordRequestInstance() {
        return NewPasswordRequest
                .builder()
                .newPassword("new")
                .confirmPassword("new")
                .build();
    }

    public BaseSearchRequest newBaseSearchRequestInstance() {
        final BaseSearchRequest userSearchRequest = BaseSearchRequest.builder().build();
        userSearchRequest.setPageNumber(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM));
        userSearchRequest.setPageSize(Integer.valueOf(AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE));
        userSearchRequest.setSortBy("id");
        userSearchRequest.setSortDir("asc");
        return userSearchRequest;
    }

    public AuthenticationDetails newAuthenticationDetailsInstance() {
        final AuthenticationDetails authDetails = new AuthenticationDetails();
        authDetails.setLoggedInUserEmail(any(String.class));
        authDetails.setLoggedInUserId(any(Integer.class));
        authDetails.setLoggedInUserRole(any(String.class));
        return authDetails;
    }
}
