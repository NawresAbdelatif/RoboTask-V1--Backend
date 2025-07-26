package com.example.acwa.repositories;

import com.example.acwa.entities.Assemblage;
import com.example.acwa.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblageRepository extends JpaRepository<Assemblage, Long> {
    Page<Assemblage> findByProjectAndNomContainingIgnoreCase(Project project, String nom, Pageable pageable);
    Page<Assemblage> findByProject(Project project, Pageable pageable);
}
