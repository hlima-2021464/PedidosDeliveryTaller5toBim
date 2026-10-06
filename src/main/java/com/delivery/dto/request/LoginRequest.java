package com.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}