package com.example.sharesnotesapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TagRequestDto {
    @NotBlank
    private String name;
}
