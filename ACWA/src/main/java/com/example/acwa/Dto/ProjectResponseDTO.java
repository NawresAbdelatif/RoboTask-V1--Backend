package com.example.acwa.Dto;

import com.example.acwa.entities.ProjectStatus;

import java.time.LocalDate;
import java.util.Set;

public class ProjectResponseDTO {
    private Long id;
    private String reference;

    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDate startDate;
//    private LocalDate endDate;
    private String creatorUsername;
    private Set<String> collaboratorsUsernames;
    private boolean archived;
    private Set<CollaboratorDTO> collaborators;
    public Set<CollaboratorDTO> getCollaborators() { return collaborators; }
    public void setCollaborators(Set<CollaboratorDTO> collaborators) { this.collaborators = collaborators; }



    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

//    public LocalDate getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(LocalDate endDate) {
//        this.endDate = endDate;
//    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public Set<String> getCollaboratorsUsernames() {
        return collaboratorsUsernames;
    }

    public void setCollaboratorsUsernames(Set<String> collaboratorsUsernames) {
        this.collaboratorsUsernames = collaboratorsUsernames;
    }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
