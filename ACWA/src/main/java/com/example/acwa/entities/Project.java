package com.example.acwa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true, unique = true)
    private String reference;


    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ProjectStatus previousStatus;



    private LocalDate startDate;
//    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "project_collaborators",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> collaborators = new HashSet<>();


    @Column(nullable = false)
    private boolean archived = false;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Assemblage> assemblages = new HashSet<>();

    // ----- Getters and setters -----

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
//    public void setEndDate(LocalDate endDate) {
//        this.endDate = endDate;
//    }
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public Set<User> getCollaborators() {
        return collaborators;
    }
    public void setCollaborators(Set<User> collaborators) {
        this.collaborators = collaborators;
    }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public ProjectStatus getPreviousStatus() {
        return previousStatus;
    }
    public void setPreviousStatus(ProjectStatus previousStatus) {
        this.previousStatus = previousStatus;
    }
    public Set<Assemblage> getAssemblages() { return assemblages; }
    public void setAssemblages(Set<Assemblage> assemblages) { this.assemblages = assemblages; }
}
