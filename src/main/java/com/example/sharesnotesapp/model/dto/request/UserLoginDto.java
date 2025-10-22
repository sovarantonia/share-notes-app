package com.example.sharesnotesapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 7)
    private String password;
}
