package com.example.acwa.Dto;

import java.time.LocalDateTime;
import java.util.List;

public class AssemblageResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private LocalDateTime dateCreation;
    private String creatorUsername;
    private List<AssemblageResponseDTO> sousAssemblages;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
    public List<AssemblageResponseDTO> getSousAssemblages() { return sousAssemblages; }
    public void setSousAssemblages(List<AssemblageResponseDTO> sousAssemblages) { this.sousAssemblages = sousAssemblages; }
}
