package com.controlf.dto;

import lombok.Data;

@Data
public class ComentarioRequestDTO {
    private String texto;
    private Integer usuarioId;
}
