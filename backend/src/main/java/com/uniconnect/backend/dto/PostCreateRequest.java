package com.uniconnect.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCreateRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private String type = "NORMAL"; // NORMAL, CONFESSION, FRESHERS_QA
    private Boolean isAnonymous = false;
}
