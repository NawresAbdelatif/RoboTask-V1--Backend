package com.example.acwa.mappers;
import com.example.acwa.Dto.OutilRequestDTO;
import com.example.acwa.Dto.OutilResponseDTO;
import com.example.acwa.entities.Outil;


public class OutilMapper {

    public static Outil toEntity(OutilRequestDTO dto) {
        Outil outil = new Outil();
        outil.setReference(dto.getReference());
        outil.setDesignation(dto.getDesignation());
        outil.setSpecification(dto.getSpecification());
//        outil.setQuantite(dto.getQuantite());
        outil.setImageUrl(dto.getImageUrl());
        outil.setDescription(dto.getDescription());
        return outil;
    }

    public static OutilResponseDTO toDTO(Outil entity) {
        OutilResponseDTO dto = new OutilResponseDTO();
        dto.setId(entity.getId());
        dto.setReference(entity.getReference());
        dto.setDesignation(entity.getDesignation());
        dto.setSpecification(entity.getSpecification());
//        dto.setQuantite(entity.getQuantite());
        dto.setImageUrl(entity.getImageUrl());
        dto.setDescription(entity.getDescription());
        return dto;
    }

}
