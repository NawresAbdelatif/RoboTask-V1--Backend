package com.example.acwa.mappers;

import com.example.acwa.Dto.AssemblageRequestDTO;
import com.example.acwa.Dto.AssemblageResponseDTO;
import com.example.acwa.entities.Assemblage;

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

//        // Mapper récursif les sous-assemblages directs
//        if (entity.getSousAssemblages() != null) {
//            List<AssemblageResponseDTO> sousDtos = entity.getSousAssemblages().stream()
//                    .map(AssemblageMapper::toDTO)
//                    .collect(Collectors.toList());
//            dto.setSousAssemblages(sousDtos);
//        }
        return dto;
    }

    public static Assemblage toEntity(AssemblageRequestDTO dto) {
        Assemblage entity = new Assemblage();
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
