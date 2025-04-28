package com.example.sharesnotesapp.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TagRequestDto {
    @NotBlank
    private String name;
}
