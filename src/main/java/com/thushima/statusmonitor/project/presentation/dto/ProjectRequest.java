package com.thushima.statusmonitor.project.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    String name,
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    String description,
    
    @NotBlank(message = "URL is required")
    @Size(max = 255, message = "URL must be less than 255 characters")
    String url,
    
    boolean active,
    
    @Positive(message = "Check interval must be a positive number")
    Integer checkIntervalInMinutes,
    
    @Positive(message = "Timeout must be a positive number")
    Integer timeoutInSeconds,
    
    @Positive(message = "Success threshold must be a positive number")
    Integer successThreshold,
    
    @Positive(message = "Failure threshold must be a positive number")
    Integer failureThreshold
) {}
