package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDebateDTO {
    private String id;
    private String usuario;
    private String fecha;
    private String mensaje;
    private String avatarUrl;
}
