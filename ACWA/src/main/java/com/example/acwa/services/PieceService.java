package com.example.acwa.services;

import com.example.acwa.Dto.PieceRequestDTO;
import com.example.acwa.Dto.PieceResponseDTO;
import com.example.acwa.entities.Piece;
import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import com.example.acwa.mappers.PieceMapper;
import com.example.acwa.repositories.PieceRepository;
import com.example.acwa.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class PieceService {

    @Autowired
    private PieceRepository pieceRepository;
    @Autowired
    private UserRepository userRepository;

    private void checkUserRole(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        boolean allowed = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN || role.getName() == RoleName.ROLE_CREATOR);
        if (!allowed) throw new RuntimeException("Non autorisé !");
    }

    @Transactional
    @CacheEvict(value = "pieces", allEntries = true)
    public PieceResponseDTO createPiece(PieceRequestDTO dto, String email) {
        checkUserRole(email);
        if (pieceRepository.existsByReference(dto.getReference())) {
            throw new RuntimeException("Référence déjà utilisée !");
        }
        Piece piece = PieceMapper.toEntity(dto);
        return PieceMapper.toDTO(pieceRepository.save(piece));
    }

    @Transactional
    @CacheEvict(value = "pieces", allEntries = true)
    public PieceResponseDTO updatePiece(Long id, PieceRequestDTO dto, String email) {
        checkUserRole(email);
        Piece piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable"));
        if (!piece.getReference().equals(dto.getReference()) &&
                pieceRepository.existsByReference(dto.getReference())) {
            throw new RuntimeException("Référence déjà utilisée !");
        }
        piece.setReference(dto.getReference());
        piece.setDesignation(dto.getDesignation());
        piece.setQuantite(dto.getQuantite());
        piece.setImageUrl(dto.getImageUrl());
        piece.setObservation(dto.getObservation());
        return PieceMapper.toDTO(pieceRepository.save(piece));
    }

    @Transactional
    @CacheEvict(value = "pieces", allEntries = true)
    public void deletePiece(Long id, String email) {
        checkUserRole(email);
        Piece piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable"));
        pieceRepository.delete(piece);
    }

    @Cacheable(value = "pieces", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Piece> getAllPieces(Pageable pageable) {
        long start = System.currentTimeMillis();
        System.out.println("[CACHE] Requête à la base de données pour les pièces page=" + pageable.getPageNumber()
                + ", size=" + pageable.getPageSize());

        Page<Piece> result = pieceRepository.findAll(pageable);

        long duration = System.currentTimeMillis() - start;
        System.out.println("[PERF] Récupération des pièces depuis la BD (hors cache) a pris " + duration + " ms");

        return result;
    }


    public PieceResponseDTO getPieceById(Long id) {
        Piece piece = pieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pièce introuvable"));
        return PieceMapper.toDTO(piece);
    }

    public Page<Piece> searchPieces(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return pieceRepository.findAll(pageable);
        }
        return pieceRepository.findByReferenceIgnoreCaseContainingOrDesignationIgnoreCaseContaining(
                search, search, pageable
        );
    }

    public long getTotalPieces() {
        return pieceRepository.count();
    }

    public Integer getTotalQuantite() {
        Integer sum = pieceRepository.sumQuantite();
        return sum != null ? sum : 0;
    }

}
