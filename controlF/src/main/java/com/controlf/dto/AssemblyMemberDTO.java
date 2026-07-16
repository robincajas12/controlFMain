package com.controlf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Miembro de la Asamblea Nacional tal como lo expone la fuente de datos externa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyMemberDTO {
    private Long id;
    private String firstName;
    private String lastname;
    private String territorial;
}
