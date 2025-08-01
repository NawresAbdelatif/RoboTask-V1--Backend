package com.example.acwa.Dto;

import com.example.acwa.entities.StatutSousAssemblage;

public class SousAssemblageRequestDTO {
    private String reference;
    private String nom;
    private String description;
    private Integer ordre;
    private StatutSousAssemblage statut;
    private Long assemblageId;

    // Getters & Setters
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public StatutSousAssemblage getStatut() { return statut; }
    public void setStatut(StatutSousAssemblage statut) { this.statut = statut; }

    public Long getAssemblageId() { return assemblageId; }
    public void setAssemblageId(Long assemblageId) { this.assemblageId = assemblageId; }
}
