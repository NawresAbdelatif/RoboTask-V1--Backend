package com.example.acwa.controllers;

import com.example.acwa.Dto.PageResult;
import com.example.acwa.Dto.ProjectRequestDTO;
import com.example.acwa.Dto.ProjectResponseDTO;
import com.example.acwa.entities.Project;
import com.example.acwa.entities.ProjectStatus;
import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import com.example.acwa.mappers.ProjectMapper;
import com.example.acwa.repositories.UserRepository;
import com.example.acwa.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequestDTO requestDTO, Authentication authentication) {
        String username = authentication.getName();
        try {
            Project projectToSave = ProjectMapper.toEntity(requestDTO);
            Project savedProject = projectService.createProject(projectToSave, username);
            ProjectResponseDTO responseDTO = ProjectMapper.toDTO(savedProject);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{projectId}/add-collaborator")
    public ResponseEntity<?> addCollaborator(
            @PathVariable Long projectId,
            @RequestParam String collaboratorUsername,
            Authentication authentication
    ) {
        String currentUsername = authentication.getName();
        try {
            projectService.addCollaborator(projectId, currentUsername, collaboratorUsername);
            return ResponseEntity.ok("Collaborateur ajouté !");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/operators")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public List<String> getOperatorUsernames() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(role ->
                                role.getName() == RoleName.ROLE_OPERATOR ||
                                        role.getName() == RoleName.ROLE_ADMIN))
                .map(User::getUsername)
                .toList();
    }


//    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
//    @GetMapping
//    public ResponseEntity<?> getAllProjects(Authentication authentication) {
//        String username = authentication.getName();
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Project> projects = projectService.getProjectsForUser(user);
//        List<ProjectResponseDTO> dtos = projects.stream()
//                .map(ProjectMapper::toDTO)
//                .toList();
//
//        return ResponseEntity.ok(dtos);
//    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping
    public ResponseEntity<PageResult<ProjectResponseDTO>> getAllProjects(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        long start = System.currentTimeMillis();

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);

        Page<Project> projectsPage = projectService.getProjectsForUser(user, pageable);
        List<ProjectResponseDTO> dtos = projectsPage.getContent().stream()
                .map(ProjectMapper::toDTO)
                .toList();

        long duration = System.currentTimeMillis() - start;
        logger.info("[PERF] /api/projects?page={} size={} -> {} ms", page, size, duration);

        PageResult<ProjectResponseDTO> response = new PageResult<>(
                dtos,
                projectsPage.getTotalElements(),
                projectsPage.getTotalPages(),
                projectsPage.getNumber()
        );

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequestDTO dto,
            Authentication authentication) {
        String username = authentication.getName();
        try {
            Project updated = projectService.updateProject(id, dto, username);
            return ResponseEntity.ok(ProjectMapper.toDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        try {
            projectService.deleteProject(id, username);
            return ResponseEntity.ok(Map.of("message", "Projet supprimé !"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping("/search")
    public ResponseEntity<PageResult<ProjectResponseDTO>> searchProjects(
            Authentication authentication,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Project> projectsPage = projectService.searchProjectsForUser(user, search, status, pageable);
        List<ProjectResponseDTO> dtos = projectsPage.getContent().stream()
                .map(ProjectMapper::toDTO)
                .toList();

        PageResult<ProjectResponseDTO> response = new PageResult<>(
                dtos,
                projectsPage.getTotalElements(),
                projectsPage.getTotalPages(),
                projectsPage.getNumber()
        );

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectService.getProjectByIdAndUser(id, user);
        ProjectResponseDTO dto = ProjectMapper.toDTO(project);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/stats/by-year")
    public ResponseEntity<Map<Integer, Long>> getProjectsCountByYear() {
        Map<Integer, Long> data = projectService.getProjectsCountByYear();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<Map<String, Long>> getProjectsCountByStatus() {
        Map<String, Long> data = projectService.getProjectsCountByStatus();
        return ResponseEntity.ok(data);
    }
}
