package com.example.acwa.repositories;

import com.example.acwa.entities.Assemblage;
import com.example.acwa.entities.AssemblageStatut;
import com.example.acwa.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssemblageRepository extends JpaRepository<Assemblage, Long> {
    Page<Assemblage> findByProjectAndNomContainingIgnoreCase(Project project, String nom, Pageable pageable);
    Page<Assemblage> findByProject(Project project, Pageable pageable);

    @Query("SELECT MAX(a.ordre) FROM Assemblage a WHERE a.project.id = :projectId")
    Optional<Integer> findMaxOrdreByProjectId(@Param("projectId") Long projectId);

    Page<Assemblage> findByProjectAndStatut(Project project, AssemblageStatut statut, Pageable pageable);

    Page<Assemblage> findByProjectAndNomContainingIgnoreCaseAndStatut(
            Project project, String nom, AssemblageStatut statut, Pageable pageable);

    Page<Assemblage> findByProjectAndStatutNot(Project project, AssemblageStatut statut, Pageable pageable);

}
