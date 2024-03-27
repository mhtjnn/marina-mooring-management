package com.marinamooringmanagement.request;

import com.marinamooringmanagement.model.dto.BaseDto;
import com.marinamooringmanagement.model.dto.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO (Data Transfer Object) class for {@link com.marinamooringmanagement.model.entity.User}.
 * This class is used for transferring user-related request data between layers of the application.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto implements Serializable {
    private Integer id;
    @NotNull(message = "First Name can't be blank")
    private String firstname;
    private String lastname;
    @Email(message = "Email is not Valid")
    private String email;
    private String phoneNumber;
    @NotEmpty(message = "Password can't be blank")
    private String password;
    private Integer roleId;
}

