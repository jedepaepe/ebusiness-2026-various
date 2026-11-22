package com.example.devis.model;

public enum TypeDocument {
    DEVIS("Devis"),
    FACTURE("Facture");

    private final String libelle;

    TypeDocument(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
