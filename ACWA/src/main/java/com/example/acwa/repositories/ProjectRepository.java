package com.example.acwa.repositories;

import com.example.acwa.entities.Project;
import com.example.acwa.entities.ProjectStatus;
import com.example.acwa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("SELECT p FROM Project p WHERE p.archived = false AND (p.creator = :user OR :user MEMBER OF p.collaborators)")
    Page<Project> findActiveByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.archived = true AND (p.creator = :user OR :user MEMBER OF p.collaborators)")
    Page<Project> findArchivedByUser(@Param("user") User user, Pageable pageable);


    @Query("SELECT p FROM Project p WHERE p.archived = false AND " +
            "((LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) AND p.creator = :user) " +
            "OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) AND :user MEMBER OF p.collaborators)) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<Project> searchActiveByUser(
            @Param("user") User user,
            @Param("search") String search,
            @Param("status") ProjectStatus status,
            Pageable pageable
    );

    // Pour l’admin (tous les projets non archivés)
    Page<Project> findAllByArchivedFalse(Pageable pageable);
    // Pour l’admin (tous les projets archivés)
    Page<Project> findAllByArchivedTrue(Pageable pageable);

    // ADMIN - projets actifs seulement
    Page<Project> findAllByNameContainingIgnoreCaseAndArchivedFalse(String name, Pageable pageable);
    Page<Project> findAllByNameContainingIgnoreCaseAndStatusAndArchivedFalse(String name, ProjectStatus status, Pageable pageable);

    // Pour les users/collabs (toujours actifs seulement)
    Page<Project> findAllByNameContainingIgnoreCaseAndArchivedFalseAndCreatorOrNameContainingIgnoreCaseAndArchivedFalseAndCollaboratorsContaining(
            String name1, User creator, String name2, User collaborator, Pageable pageable
    );

    Page<Project> findAllByNameContainingIgnoreCaseAndStatusAndArchivedFalseAndCreatorOrNameContainingIgnoreCaseAndStatusAndArchivedFalseAndCollaboratorsContaining(
            String name1, ProjectStatus status1, User creator,
            String name2, ProjectStatus status2, User collaborator,
            Pageable pageable
    );

    boolean existsByReference(String reference);

    long countByArchivedFalse();

}




