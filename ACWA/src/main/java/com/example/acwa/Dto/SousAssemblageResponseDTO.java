package com.example.acwa.Dto;

import com.example.acwa.entities.StatutSousAssemblage;

import java.time.LocalDateTime;

public class SousAssemblageResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private Integer ordre;
    private LocalDateTime dateCreation;
    private String createurUsername;
    private StatutSousAssemblage statut;
    private Long assemblageId;

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrdre() {
        return ordre;
    }
    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getCreateurUsername() {
        return createurUsername;
    }
    public void setCreateurUsername(String createurUsername) {
        this.createurUsername = createurUsername;
    }

    public StatutSousAssemblage getStatut() {
        return statut;
    }
    public void setStatut(StatutSousAssemblage statut) {
        this.statut = statut;
    }

    public Long getAssemblageId() {
        return assemblageId;
    }
    public void setAssemblageId(Long assemblageId) {
        this.assemblageId = assemblageId;
    }
}
