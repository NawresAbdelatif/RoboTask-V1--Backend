package com.example.acwa.repositories;

import com.example.acwa.entities.Project;
import com.example.acwa.entities.ProjectStatus;
import com.example.acwa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Page<Project> findAllByCreatorOrCollaboratorsContaining(User creator, User collaborator, Pageable pageable);
    Page<Project> findAllByNameContainingIgnoreCaseAndCreatorOrNameContainingIgnoreCaseAndCollaboratorsContaining(
            String name1, User creator, String name2, User collaborator, Pageable pageable
    );
    Page<Project> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Project> findAllByNameContainingIgnoreCaseAndStatus(String name, ProjectStatus status, Pageable pageable);

    Page<Project> findAllByNameContainingIgnoreCaseAndStatusAndCreatorOrNameContainingIgnoreCaseAndStatusAndCollaboratorsContaining(
            String name1, ProjectStatus status1, User creator,
            String name2, ProjectStatus status2, User collaborator,
            Pageable pageable
    );

    @Query("SELECT YEAR(p.startDate) as year, COUNT(p) as count FROM Project p GROUP BY YEAR(p.startDate) ORDER BY year")
    List<Object[]> countProjectsByYear();

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();

}




