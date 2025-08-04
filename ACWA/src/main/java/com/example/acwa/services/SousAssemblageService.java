package com.example.acwa.services;

import com.example.acwa.Dto.SousAssemblageRequestDTO;
import com.example.acwa.Dto.SousAssemblageResponseDTO;
import com.example.acwa.entities.*;
import com.example.acwa.mappers.SousAssemblageMapper;
import com.example.acwa.repositories.SousAssemblageRepository;
import com.example.acwa.repositories.AssemblageRepository;
import com.example.acwa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SousAssemblageService {

    @Autowired
    private SousAssemblageRepository sousAssemblageRepository;

    @Autowired
    private AssemblageRepository assemblageRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean isAdminCreatorOrCollaborator(User user, Assemblage assemblage) {
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
        boolean isCreator = assemblage.getProject().getCreator().equals(user);
        boolean isCollaborator = assemblage.getProject().getCollaborators().contains(user);
        return isAdmin || isCreator || isCollaborator;
    }


    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)
    public SousAssemblageResponseDTO createSousAssemblage(SousAssemblageRequestDTO dto, String email) {
        if (dto.getReference() == null || dto.getReference().isEmpty()) {
            throw new RuntimeException("La référence est obligatoire !");
        }
        if (sousAssemblageRepository.existsByReference(dto.getReference())) {
            throw new RuntimeException("Référence déjà utilisée !");
        }
        Assemblage assemblage = assemblageRepository.findById(dto.getAssemblageId())
                .orElseThrow(() -> new RuntimeException("Assemblage parent non trouvé"));
        User createur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé"));

        if (!isAdminCreatorOrCollaborator(createur, assemblage)) {
            throw new RuntimeException("Vous n'avez pas les droits de créer un sous-assemblage !");
        }

        SousAssemblage entity = SousAssemblageMapper.toEntity(dto, createur, assemblage);
        entity.setDateCreation(LocalDateTime.now());

        int maxOrdre = sousAssemblageRepository.findByAssemblage_Id(assemblage.getId())
                .stream()
                .mapToInt(sa -> sa.getOrdre() != null ? sa.getOrdre() : 0)
                .max()
                .orElse(0);
        entity.setOrdre(maxOrdre + 1);

        SousAssemblage saved = sousAssemblageRepository.save(entity);
        return SousAssemblageMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "sousAssemblages")
    public List<SousAssemblageResponseDTO> getSousAssemblagesByAssemblage(Long assemblageId) {
        return sousAssemblageRepository.findByAssemblage_Id(assemblageId)
                .stream()
                .map(SousAssemblageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)
    public void deleteSousAssemblage(Long id, String email) {
        SousAssemblage sa = sousAssemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Assemblage assemblage = sa.getAssemblage();
        if (!isAdminCreatorOrCollaborator(user, assemblage)) {
            throw new RuntimeException("Non autorisé à supprimer !");
        }
        sousAssemblageRepository.delete(sa);
    }


    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)
    public SousAssemblageResponseDTO updateSousAssemblage(Long id, SousAssemblageRequestDTO dto, String email) {
        SousAssemblage sa = sousAssemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Assemblage assemblage = sa.getAssemblage();
        if (!isAdminCreatorOrCollaborator(user, assemblage)) {
            throw new RuntimeException("Non autorisé à modifier !");
        }

        if (dto.getReference() != null && !dto.getReference().equals(sa.getReference())) {
            boolean exists = sousAssemblageRepository.existsByReference(dto.getReference());
            if (exists) {
                throw new RuntimeException("La référence est déjà utilisée !");
            }
            sa.setReference(dto.getReference());
        }

        sa.setNom(dto.getNom());
        sa.setDescription(dto.getDescription());
        sa.setOrdre(dto.getOrdre());

        if (dto.getStatut() != null) {
            sa.setStatut(dto.getStatut());
        }

        SousAssemblage saved = sousAssemblageRepository.save(sa);
        return SousAssemblageMapper.toDTO(saved);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "sousAssemblages")
    public SousAssemblageResponseDTO getSousAssemblageById(Long id) {
        SousAssemblage sa = sousAssemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé"));
        return SousAssemblageMapper.toDTO(sa);
    }

    @Transactional
    public void reorderSousAssemblages(Long assemblageId, List<Long> orderedIds, String email) {
        Assemblage assemblage = assemblageRepository.findById(assemblageId)
                .orElseThrow(() -> new RuntimeException("Assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!isAdminCreatorOrCollaborator(user, assemblage)) {
            throw new RuntimeException("Non autorisé !");
        }

        for (int i = 0; i < orderedIds.size(); i++) {
            final Long id = orderedIds.get(i);
            SousAssemblage sa = sousAssemblageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé: " + id));
            sa.setOrdre(i);
        }
        sousAssemblageRepository.flush();
    }


    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)
    public SousAssemblage archiverSousAssemblage(Long id, String email) {
        SousAssemblage sa = sousAssemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Assemblage assemblage = sa.getAssemblage();
        if (!isAdminCreatorOrCollaborator(user, assemblage)) {
            throw new RuntimeException("Non autorisé !");
        }

        if (sa.getStatut() != StatutSousAssemblage.ARCHIVE) {
            sa.setStatutAvantArchive(sa.getStatut());
            sa.setStatut(StatutSousAssemblage.ARCHIVE);
            sousAssemblageRepository.save(sa);
        }
        return sa;
    }


    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)
    public SousAssemblage desarchiverSousAssemblage(Long id, String email) {
        SousAssemblage sa = sousAssemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Assemblage assemblage = sa.getAssemblage();
        if (!isAdminCreatorOrCollaborator(user, assemblage)) {
            throw new RuntimeException("Non autorisé !");
        }

        if (sa.getStatut() == StatutSousAssemblage.ARCHIVE) {
            StatutSousAssemblage old = sa.getStatutAvantArchive();
            sa.setStatut(old != null ? old : StatutSousAssemblage.BROUILLON);
            sa.setStatutAvantArchive(null);
            sousAssemblageRepository.save(sa);
        }
        return sa;
    }

}
