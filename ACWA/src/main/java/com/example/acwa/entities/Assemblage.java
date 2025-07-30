package com.example.acwa.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Assemblage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1500)
    private String description;

    @Column(name = "ordre")
    private Integer ordre;

    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssemblageStatut statut = AssemblageStatut.BROUILLON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Assemblage> sousAssemblages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Assemblage parent;



    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }


    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Set<Assemblage> getSousAssemblages() { return sousAssemblages; }
    public void setSousAssemblages(Set<Assemblage> sousAssemblages) { this.sousAssemblages = sousAssemblages; }

    public Assemblage getParent() { return parent; }
    public void setParent(Assemblage parent) { this.parent = parent; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public AssemblageStatut getStatut() { return statut; }
    public void setStatut(AssemblageStatut statut) { this.statut = statut; }

}
