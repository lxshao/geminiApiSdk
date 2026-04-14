package com.example.genaisdk.dto;

import jakarta.validation.constraints.NotBlank;

public record GroundingRequest(@NotBlank String prompt) {
}
