package com.example.acwa.Dto;

import com.example.acwa.entities.ProjectStatus;

import java.time.LocalDate;

public class ProjectRequestDTO {

    private String reference;
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean archived;



    // Getters & Setters

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

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
