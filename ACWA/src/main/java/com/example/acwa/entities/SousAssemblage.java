package com.example.acwa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SousAssemblage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1500)
    private String description;

    private Integer ordre;

    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User createur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSousAssemblage statut = StatutSousAssemblage.BROUILLON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assemblage_id", nullable = false)
    private Assemblage assemblage;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_avant_archive")
    private StatutSousAssemblage statutAvantArchive;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public User getCreateur() { return createur; }
    public void setCreateur(User createur) { this.createur = createur; }

    public StatutSousAssemblage getStatut() { return statut; }
    public void setStatut(StatutSousAssemblage statut) { this.statut = statut; }

    public Assemblage getAssemblage() { return assemblage; }
    public void setAssemblage(Assemblage assemblage) { this.assemblage = assemblage; }

    public StatutSousAssemblage getStatutAvantArchive() { return statutAvantArchive; }
    public void setStatutAvantArchive(StatutSousAssemblage statutAvantArchive) { this.statutAvantArchive = statutAvantArchive; }
}
