package com.example.acwa.mappers;

import com.example.acwa.Dto.AssemblageRequestDTO;
import com.example.acwa.Dto.AssemblageResponseDTO;
import com.example.acwa.entities.Assemblage;
import com.example.acwa.entities.AssemblageStatut;

import java.util.List;
import java.util.stream.Collectors;


public class AssemblageMapper {
    public static AssemblageResponseDTO toDTO(Assemblage entity) {
        AssemblageResponseDTO dto = new AssemblageResponseDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setDateCreation(entity.getDateCreation());
        dto.setCreatorUsername(entity.getCreator() != null ? entity.getCreator().getUsername() : null);
        dto.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
        dto.setReference(entity.getReference());
        dto.setStatut(entity.getStatut() != null ? entity.getStatut().name() : null);
        if (entity.getSousAssemblages() != null && !entity.getSousAssemblages().isEmpty()) {
            List<AssemblageResponseDTO> sousDtos = entity.getSousAssemblages().stream()
                    .map(AssemblageMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setSousAssemblages(sousDtos);
        }
        dto.setStatutAvantArchive(
                entity.getStatutAvantArchive() != null ? entity.getStatutAvantArchive().name() : null
        );

        return dto;
    }

    public static Assemblage toEntity(AssemblageRequestDTO dto, Assemblage parent) {
        Assemblage entity = new Assemblage();
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setParent(parent);
        entity.setReference(dto.getReference());
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : AssemblageStatut.BROUILLON);

        return entity;
    }

}
