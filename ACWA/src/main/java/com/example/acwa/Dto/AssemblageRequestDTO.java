package com.example.acwa.Dto;

import com.example.acwa.entities.AssemblageStatut;

public class AssemblageRequestDTO {
    private String nom;
    private String description;
   private Long parentId;
    private String reference;
    private AssemblageStatut statut;


    // --- Getters & Setters ---
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public AssemblageStatut getStatut() { return statut; }
    public void setStatut(AssemblageStatut statut) { this.statut = statut; }
}
