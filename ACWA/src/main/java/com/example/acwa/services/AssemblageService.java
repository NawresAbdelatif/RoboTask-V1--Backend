package com.example.acwa.services;

import com.example.acwa.Dto.AssemblageRequestDTO;
import com.example.acwa.entities.*;
import com.example.acwa.repositories.AssemblageRepository;
import com.example.acwa.repositories.ProjectRepository;
import com.example.acwa.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AssemblageService {

    @Autowired
    private AssemblageRepository assemblageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "assemblages", allEntries = true)
    public Assemblage createAssemblage(Long projectId, AssemblageRequestDTO dto, String email) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreatorOrCollaborator =
                user.equals(project.getCreator()) || project.getCollaborators().contains(user);

        if (!(isAdmin || isCreatorOrCollaborator)) {
            throw new RuntimeException("Vous n'avez pas les droits de créer un assemblage !");
        }

        Assemblage assemblage = new Assemblage();
        assemblage.setNom(dto.getNom());
        assemblage.setDescription(dto.getDescription());
        assemblage.setDateCreation(LocalDateTime.now());
        assemblage.setCreator(user);
        assemblage.setProject(project);
//
//        // Gestion du parent (sous-assemblage)
//        if (dto.getParentId() != null) {
//            Assemblage parent = assemblageRepository.findById(dto.getParentId())
//                    .orElseThrow(() -> new RuntimeException("Parent non trouvé"));
//            assemblage.setParent(parent);
//        }

        return assemblageRepository.save(assemblage);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "assemblages")
    public Page<Assemblage> getAssemblagesForProject(Long projectId, String nom, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        if (nom != null && !nom.isEmpty()) {
            return assemblageRepository.findByProjectAndNomContainingIgnoreCase(project, nom, pageable);
        } else {
            return assemblageRepository.findByProject(project, pageable);
        }
    }
    @Transactional
    @CacheEvict(value = "assemblages", allEntries = true)
    public void deleteAssemblage(Long id, String email) {
        Assemblage assemblage = assemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreator = assemblage.getCreator().getEmail().equals(email);

        if (!(isAdmin || isCreator)) {
            throw new RuntimeException("Non autorisé à supprimer !");
        }
        assemblageRepository.delete(assemblage);
    }


    @Transactional
    @CacheEvict(value = "assemblages", allEntries = true)
    public Assemblage updateAssemblage(Long id, AssemblageRequestDTO dto, String email) {
        Assemblage assemblage = assemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assemblage non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean isAdmin = user.getRoles().stream().anyMatch(
                r -> r.getName().name().equals("ROLE_ADMIN")
        );
        boolean isCreator = assemblage.getCreator().getEmail().equals(email);

        if (!(isAdmin || isCreator)) {
            throw new RuntimeException("Non autorisé à modifier !");
        }

        assemblage.setNom(dto.getNom());
        assemblage.setDescription(dto.getDescription());
        return assemblageRepository.save(assemblage);
    }


    @Transactional(readOnly = true)
    public Assemblage getAssemblageById(Long id) {
        return assemblageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assemblage non trouvé"));
    }
}
