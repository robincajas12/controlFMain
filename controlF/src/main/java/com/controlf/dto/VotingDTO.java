package com.controlf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Votación tal como la expone la fuente de datos externa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VotingDTO {
    private Long id;
    private String votingDate;
    private String proposalDescription;
    private String themeDescription;
    private String description;
}
