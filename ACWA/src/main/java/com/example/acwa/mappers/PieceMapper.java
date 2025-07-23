package com.example.acwa.mappers;

import com.example.acwa.Dto.PieceRequestDTO;
import com.example.acwa.Dto.PieceResponseDTO;
import com.example.acwa.entities.Piece;

public class PieceMapper {

    public static Piece toEntity(PieceRequestDTO dto) {
        Piece piece = new Piece();
        piece.setReference(dto.getReference());
        piece.setDesignation(dto.getDesignation());
        piece.setQuantite(dto.getQuantite());
        piece.setImageUrl(dto.getImageUrl());
        piece.setObservation(dto.getObservation());
        return piece;
    }

    public static PieceResponseDTO toDTO(Piece entity) {
        PieceResponseDTO dto = new PieceResponseDTO();
        dto.setId(entity.getId());
        dto.setReference(entity.getReference());
        dto.setDesignation(entity.getDesignation());
        dto.setQuantite(entity.getQuantite());
        dto.setImageUrl(entity.getImageUrl());
        dto.setObservation(entity.getObservation());
        return dto;
    }

    public static void updateEntity(Piece entity, PieceRequestDTO dto) {
        entity.setReference(dto.getReference());
        entity.setDesignation(dto.getDesignation());
        entity.setQuantite(dto.getQuantite());
        entity.setImageUrl(dto.getImageUrl());
        entity.setObservation(dto.getObservation());
    }
}
