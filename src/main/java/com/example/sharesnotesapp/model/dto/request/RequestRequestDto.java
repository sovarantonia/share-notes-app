package com.example.sharesnotesapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RequestRequestDto {
    @NotBlank(message = "Field cannot be empty")
    private Long senderId;
    @NotBlank(message = "Field cannot be empty")
    private String receiverEmail;
}
