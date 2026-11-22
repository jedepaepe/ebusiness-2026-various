package com.example.devis.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument type;

    @Column(nullable = false)
    private String nomClient;

    private String emailClient;

    private String telephoneClient;

    @Column(length = 500)
    private String adresseClient;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate date;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEcheance;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDocument statut;

    @Column(length = 2000)
    private String description;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
        if (this.date == null) {
            this.date = LocalDate.now();
        }
        if (this.montantTotal == null) {
            this.montantTotal = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    public boolean isDevis() {
        return TypeDocument.DEVIS.equals(this.type);
    }

    public boolean isFacture() {
        return TypeDocument.FACTURE.equals(this.type);
    }
}
