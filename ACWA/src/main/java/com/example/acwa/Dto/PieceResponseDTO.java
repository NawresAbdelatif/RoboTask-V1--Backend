package com.example.acwa.Dto;

public class PieceResponseDTO {

    private Long id;
    private String reference;
    private String designation;
//    private int quantite;
    private String imageUrl;
    private String observation;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

//    public int getQuantite() {
//        return quantite;
//    }
//
//    public void setQuantite(int quantite) {
//        this.quantite = quantite;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
