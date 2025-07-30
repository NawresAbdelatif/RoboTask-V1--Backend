package com.example.acwa.Dto;

public class OutilRequestDTO {
    private String reference;
    private String designation;
    private String specification;
//    private int quantite;
    private String imageUrl;
    private String description;

    // Getters and Setters
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

//    public int getQuantite() { return quantite; }
//    public void setQuantite(int quantite) { this.quantite = quantite; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
