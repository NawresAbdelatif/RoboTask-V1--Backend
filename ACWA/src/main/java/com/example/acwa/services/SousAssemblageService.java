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

    @Transactional
    @CacheEvict(value = "sousAssemblages", allEntries = true)

    public SousAssemblageResponseDTO createSousAssemblage(SousAssemblageRequestDTO dto, String email) {
        Assemblage assemblage = assemblageRepository.findById(dto.getAssemblageId())
                .orElseThrow(() -> new RuntimeException("Assemblage parent non trouvé"));
        User createur = userRepository.findById(dto.getCreateurId())
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé"));

        boolean isAdmin = createur.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreatorOrCollaborator =
                createur.equals(assemblage.getCreator()) ||
                        (assemblage.getProject() != null && assemblage.getProject().getCollaborators().contains(createur));

        if (!(isAdmin || isCreatorOrCollaborator)) {
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

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreator = sa.getCreateur() != null && user.getId().equals(sa.getCreateur().getId());

        if (!(isAdmin || isCreator)) {
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

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreator = sa.getCreateur() != null && user.getId().equals(sa.getCreateur().getId());

        if (!(isAdmin || isCreator)) {
            throw new RuntimeException("Non autorisé à modifier !");
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

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreatorOrCollaborator =
                user.equals(assemblage.getCreator()) ||
                        (assemblage.getProject() != null && assemblage.getProject().getCollaborators().contains(user));

        if (!(isAdmin || isCreatorOrCollaborator)) {
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
}
