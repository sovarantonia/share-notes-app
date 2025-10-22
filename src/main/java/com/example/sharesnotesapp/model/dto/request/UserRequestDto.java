package com.example.sharesnotesapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * for user registration or credential update
 */
@Data
public class UserRequestDto {

    @NotBlank(message = "First name should not be empty")
    private String firstName = "";
    @NotBlank(message = "Last name should not be empty")
    private String lastName = "";
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email address should not be empty")
    private String email = "";
    @NotBlank(message = "Password should not be empty")
    @Size(min = 7, message = "Password must have at least 7 characters")
    private String password = "";
}
