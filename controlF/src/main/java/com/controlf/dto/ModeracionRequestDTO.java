package com.controlf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Solicitud del validador para cambiar el estado de moderación de un
 * comentario. {@code estado} admite los valores de
 * {@link com.controlf.db.schema.enums.EstadoModeracion}.
 */
@Data
public class ModeracionRequestDTO {
    @NotBlank(message = "El estado de moderación es obligatorio")
    private String estado;

    private String nota;
}
