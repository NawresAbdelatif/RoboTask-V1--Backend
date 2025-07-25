package com.example.acwa.services;

import com.example.acwa.Dto.OutilRequestDTO;
import com.example.acwa.Dto.OutilResponseDTO;
import com.example.acwa.Dto.PieceRequestDTO;
import com.example.acwa.Dto.PieceResponseDTO;
import com.example.acwa.entities.Outil;
import com.example.acwa.entities.Piece;
import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import com.example.acwa.mappers.OutilMapper;
import com.example.acwa.mappers.PieceMapper;
import com.example.acwa.repositories.OutilRepository;
import com.example.acwa.repositories.PieceRepository;
import com.example.acwa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OutilService {

    @Autowired
    private OutilRepository outilRepository;
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
    @CacheEvict(value = "outils", allEntries = true)
    public OutilResponseDTO createOutil(OutilRequestDTO dto, String email) {
        checkUserRole(email);
        if (outilRepository.existsByReference(dto.getReference())) {
            throw new RuntimeException("Référence déjà utilisée !");
        }
        Outil outil = OutilMapper.toEntity(dto);
        return OutilMapper.toDTO(outilRepository.save(outil));
    }


    @Transactional
    @CacheEvict(value = "outils", allEntries = true)
    public OutilResponseDTO updateOutil(Long id, OutilRequestDTO dto, String email) {
        checkUserRole(email);
        Outil outil = outilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("outil introuvable"));
        if (!outil.getReference().equals(dto.getReference()) &&
                outilRepository.existsByReference(dto.getReference())) {
            throw new RuntimeException("Référence déjà utilisée !");
        }
        outil.setReference(dto.getReference());
        outil.setDesignation(dto.getDesignation());
        outil.setSpecification(dto.getSpecification());
        outil.setQuantite(dto.getQuantite());
        outil.setImageUrl(dto.getImageUrl());
        outil.setDescription(dto.getDescription());
        return OutilMapper.toDTO(outilRepository.save(outil));
    }


    @Transactional
    @CacheEvict(value = "outils", allEntries = true)
    public void deleteOutil(Long id, String email) {
        checkUserRole(email);
        Outil outil = outilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("outil introuvable"));
        outilRepository.delete(outil);
    }

    @Cacheable(value = "outils", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Outil> getAllOutils(Pageable pageable) {
        long start = System.currentTimeMillis();
        System.out.println("[CACHE] Requête à la base de données pour les outils page=" + pageable.getPageNumber()
                + ", size=" + pageable.getPageSize());

        Page<Outil> result = outilRepository.findAll(pageable);

        long duration = System.currentTimeMillis() - start;
        System.out.println("[PERF] Récupération des outils depuis la BD (hors cache) a pris " + duration + " ms");

        return result;
    }

    public OutilResponseDTO getOutilById(Long id) {
        Outil outil = outilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outil introuvable"));
        return OutilMapper.toDTO(outil);
    }

    public Page<Outil> searchOutils(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return outilRepository.findAll(pageable);
        }
        return outilRepository.findByReferenceIgnoreCaseContainingOrDesignationIgnoreCaseContaining(
                search, search, pageable
        );
    }
}
