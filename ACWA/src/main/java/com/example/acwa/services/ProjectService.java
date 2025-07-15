package com.example.acwa.services;

import com.example.acwa.Dto.ProjectRequestDTO;
import com.example.acwa.entities.Project;
import com.example.acwa.entities.ProjectStatus;
import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import com.example.acwa.repositories.ProjectRepository;
import com.example.acwa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
    @Autowired
    private NotificationService notificationService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, CacheManager cacheManager) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    public void evictProjectCache(Project project) {
        if (project.getCreator() != null) {
            System.out.println("Éviction du cache pour userId: " + project.getCreator().getId());
            cacheManager.getCache("projectsByUser").evict(project.getCreator().getId());
        }
        if (project.getCollaborators() != null) {
            project.getCollaborators().forEach(collab -> {
                System.out.println("Éviction du cache pour userId: " + collab.getId());
                cacheManager.getCache("projectsByUser").evict(collab.getId());
            });
        }
    }


    @Transactional
    public Project createProject(Project project, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Seuls les ADMIN ou OPERATOR peuvent créer un projet
        boolean isAllowed = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleName.ROLE_ADMIN) || role.getName().equals(RoleName.ROLE_CREATOR));

        if (!isAllowed) {
            throw new RuntimeException("Unauthorized: Only ADMIN or OPERATOR can create a project");
        }

        project.setCreator(user);
        return projectRepository.save(project);
    }

    public void addCollaborator(Long projectId, String currentUsername, String collaboratorUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        User collaborator = userRepository.findByUsername(collaboratorUsername)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé"));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur courant introuvable"));

        // Seuls l’admin OU le créateur peuvent ajouter des collaborateurs
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isCreator = project.getCreator().getUsername().equals(currentUsername);

        if (!isAdmin && !isCreator) {
            throw new RuntimeException("Seul l'administrateur ou le créateur du projet peut ajouter des collaborateurs !");
        }



        project.getCollaborators().add(collaborator);
        projectRepository.save(project);

        // Notification
        String msg = "Vous avez été invité au projet : " + project.getName();
        notificationService.createNotification(msg, collaborator);

        evictProjectCache(project);
    }


//    public List<Project> getProjectsForUser(User user) {
//        // Si Admin : tout
//        boolean isAdmin = user.getRoles().stream()
//                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
//
//        if (isAdmin) {
//            return projectRepository.findAll();
//        } else {
//            // Sinon, seulement ceux créés ou collaborés
//            return projectRepository.findAllByCreatorOrCollaboratorsContaining(user, user);
//        }
//    }
//
//    @Cacheable(value = "projectsByUser", key = "#user.id")
//    public List<Project> getProjectsForUser(User user) {
//        System.out.println(">>> Appel à la base de données pour l'utilisateur: " + user.getUsername());
//        boolean isAdmin = user.getRoles().stream()
//                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
//
//        if (isAdmin) {
//            return projectRepository.findAll();
//        } else {
//            return projectRepository.findAllByCreatorOrCollaboratorsContaining(user, user);
//        }
//    }

    @Cacheable(value = "projectsByUser", key = "#user.id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Project> getProjectsForUser(User user, Pageable pageable) {
        long start = System.currentTimeMillis();
         System.out.println(">>> Appel à la base de données pour l'utilisateur: " + user.getUsername());

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

        Page<Project> result;
        if (isAdmin) {
            result = projectRepository.findAll(pageable);
        } else {
            result = projectRepository.findAllByCreatorOrCollaboratorsContaining(user, user, pageable);
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("[PERF] getProjectsForUser(" + user.getUsername() + ", page=" + pageable.getPageNumber() +
                ", size=" + pageable.getPageSize() + ") a pris " + duration + " ms");

        return result;
    }

//
//    @Transactional
//    @CacheEvict(value = "projectsByUser", allEntries = true)
//    public Project updateProject(Long projectId, ProjectRequestDTO dto, String username) {
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Projet introuvable"));
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
//
//        if (!isAdmin && !project.getCreator().getUsername().equals(username)) {
//            throw new RuntimeException("Vous n'avez pas le droit de modifier ce projet !");
//        }
//        project.setName(dto.getName());
//        project.setDescription(dto.getDescription());
//        project.setStatus(dto.getStatus());
//        project.setStartDate(dto.getStartDate());
//        project.setEndDate(dto.getEndDate());
//        return projectRepository.save(project);
//    }

    @Transactional
    @CacheEvict(value = "projectsByUser", allEntries = true)
    public Project updateProject(Long projectId, ProjectRequestDTO dto, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));

        if (!isAdmin && !project.getCreator().getUsername().equals(username)) {
            throw new RuntimeException("Vous n'avez pas le droit de modifier ce projet !");
        }
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        Project updatedProject = projectRepository.save(project);

        evictProjectCache(updatedProject);

        return updatedProject;
    }

    @Transactional
    @CacheEvict(value = "projectsByUser", allEntries = true)
    public void deleteProject(Long projectId, String username) {
        System.out.println("Tentative suppression projet " + projectId + " par " + username);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));

        System.out.println("User roles: " + user.getRoles());
        System.out.println("Project creator: " + project.getCreator().getUsername());
        System.out.println("isAdmin: " + isAdmin);

        if (!isAdmin && !project.getCreator().getUsername().equals(username)) {
            throw new RuntimeException("Vous n'avez pas le droit de supprimer ce projet !");
        }

        evictProjectCache(project);

        projectRepository.delete(project);
        System.out.println("Projet " + projectId + " supprimé par " + username);
    }

    @Cacheable(value = "projectsByUser", key = "#user.id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #search + '-' + #status")
    public Page<Project> searchProjectsForUser(User user, String search, ProjectStatus status, Pageable pageable) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

        if (isAdmin) {
            if (status != null)
                return projectRepository.findAllByNameContainingIgnoreCaseAndStatus(search, status, pageable);
            else
                return projectRepository.findAllByNameContainingIgnoreCase(search, pageable);
        } else {
            if (status != null)
                return projectRepository.findAllByNameContainingIgnoreCaseAndStatusAndCreatorOrNameContainingIgnoreCaseAndStatusAndCollaboratorsContaining(
                        search, status, user, search, status, user, pageable);
            else
                return projectRepository.findAllByNameContainingIgnoreCaseAndCreatorOrNameContainingIgnoreCaseAndCollaboratorsContaining(
                        search, user, search, user, pageable);
        }
    }


    public Project getProjectByIdAndUser(Long projectId, User user) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projet introuvable"));
        if (isAdmin ||
                project.getCreator().getId().equals(user.getId()) ||
                project.getCollaborators().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            return project;
        }
        throw new RuntimeException("Vous n'avez pas accès à ce projet !");
    }

    public Map<Integer, Long> getProjectsCountByYear() {
        List<Object[]> result = projectRepository.countProjectsByYear();
        Map<Integer, Long> map = new LinkedHashMap<>();
        for (Object[] row : result) {
            Integer year = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            map.put(year, count);
        }
        return map;
    }


    public Map<String, Long> getProjectsCountByStatus() {
        List<Object[]> result = projectRepository.countProjectsByStatus();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : result) {
            String status = row[0].toString(); // Ex: PLANNED
            Long count = ((Number) row[1]).longValue();
            map.put(status, count);
        }
        return map;
    }
}

