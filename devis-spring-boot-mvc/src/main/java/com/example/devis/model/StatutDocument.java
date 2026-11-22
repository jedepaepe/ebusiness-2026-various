package com.example.devis.model;

public enum StatutDocument {
    // Statuts Devis
    BROUILLON("Brouillon", "bg-secondary"),
    ENVOYE("Envoyé", "bg-info text-dark"),
    ACCEPTE("Accepté", "bg-success"),
    REFUSE("Refusé", "bg-danger"),

    // Statuts Factures
    EN_ATTENTE("En attente", "bg-warning text-dark"),
    PAYEE("Payée", "bg-success"),
    EN_RETARD("En retard", "bg-danger"),
    ANNULEE("Annulée", "bg-dark");

    private final String libelle;
    private final String badgeClass;

    StatutDocument(String libelle, String badgeClass) {
        this.libelle = libelle;
        this.badgeClass = badgeClass;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
