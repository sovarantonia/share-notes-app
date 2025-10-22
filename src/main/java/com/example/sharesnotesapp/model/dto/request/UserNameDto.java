package com.example.sharesnotesapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserNameDto {
    @NotBlank(message = "First name should not be empty")
    private String firstName = "";
    @NotBlank(message = "Last name should not be empty")
    private String lastName = "";
}
