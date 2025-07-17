package com.example.acwa.mappers;

import com.example.acwa.Dto.ProjectRequestDTO;
import com.example.acwa.Dto.ProjectResponseDTO;
import com.example.acwa.entities.Project;

import java.util.stream.Collectors;

public class ProjectMapper {

    public static Project toEntity(ProjectRequestDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setArchived(dto.isArchived());
        return project;
    }

    public static ProjectResponseDTO toDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setCreatorUsername(project.getCreator().getUsername());
        dto.setCollaboratorsUsernames(
                project.getCollaborators().stream()
                        .map(user -> user.getUsername())
                        .collect(Collectors.toSet())
        );
        dto.setArchived(project.isArchived());
        return dto;
    }
}
