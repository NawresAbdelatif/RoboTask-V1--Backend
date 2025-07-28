package com.example.acwa.mappers;

import com.example.acwa.Dto.SousAssemblageRequestDTO;
import com.example.acwa.Dto.SousAssemblageResponseDTO;
import com.example.acwa.entities.Assemblage;
import com.example.acwa.entities.SousAssemblage;
import com.example.acwa.entities.StatutSousAssemblage;
import com.example.acwa.entities.User;

public class SousAssemblageMapper {
    // Mapping Entity to Response DTO
    public static SousAssemblageResponseDTO toDTO(SousAssemblage entity) {
        SousAssemblageResponseDTO dto = new SousAssemblageResponseDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setOrdre(entity.getOrdre());
        dto.setDateCreation(entity.getDateCreation());
        dto.setStatut(entity.getStatut());
        dto.setAssemblageId(entity.getAssemblage() != null ? entity.getAssemblage().getId() : null);
        dto.setCreateurUsername(entity.getCreateur() != null ? entity.getCreateur().getUsername() : null);
        return dto;
    }

    // Mapping Request DTO to Entity
    public static SousAssemblage toEntity(SousAssemblageRequestDTO dto, User createur, Assemblage assemblage) {
        SousAssemblage entity = new SousAssemblage();
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setOrdre(dto.getOrdre());
        entity.setCreateur(createur);
        entity.setAssemblage(assemblage);
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutSousAssemblage.NON_DEMARRE);
        entity.setDateCreation(java.time.LocalDateTime.now());
        return entity;
    }
}
