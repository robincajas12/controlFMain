package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComentarioRequestDTO {
    @NotBlank(message = "El texto del comentario es obligatorio")
    private String texto;
}
