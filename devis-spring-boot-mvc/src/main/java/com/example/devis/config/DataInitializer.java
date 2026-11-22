package com.example.devis.config;

import com.example.devis.model.Document;
import com.example.devis.model.StatutDocument;
import com.example.devis.model.TypeDocument;
import com.example.devis.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;

    @Override
    public void run(String... args) {
        if (documentRepository.count() == 0) {
            log.info("Initialisation de données de démonstration dans la base SQLite locale...");

            List<Document> initialDocs = List.of(
                    // Devis
                    Document.builder()
                            .reference("DEV-2026-001")
                            .type(TypeDocument.DEVIS)
                            .nomClient("Acme Corporation")
                            .emailClient("contact@acme.com")
                            .telephoneClient("01 23 45 67 89")
                            .adresseClient("12 Rue de l'Industrie, 75001 Paris")
                            .date(LocalDate.now().minusDays(10))
                            .dateEcheance(LocalDate.now().plusDays(20))
                            .montantTotal(new BigDecimal("3500.00"))
                            .statut(StatutDocument.ENVOYE)
                            .description("Refonte du site vitrine institutionnel et optimisation SEO.")
                            .build(),

                    Document.builder()
                            .reference("DEV-2026-002")
                            .type(TypeDocument.DEVIS)
                            .nomClient("TechNova Solutions")
                            .emailClient("direction@technova.fr")
                            .telephoneClient("04 78 12 34 56")
                            .adresseClient("45 Boulevard Vivier Merle, 69003 Lyon")
                            .date(LocalDate.now().minusDays(5))
                            .dateEcheance(LocalDate.now().plusDays(25))
                            .montantTotal(new BigDecimal("8200.50"))
                            .statut(StatutDocument.ACCEPTE)
                            .description("Développement d'une API REST Spring Boot & architecture cloud.")
                            .build(),

                    Document.builder()
                            .reference("DEV-2026-003")
                            .type(TypeDocument.DEVIS)
                            .nomClient("Boulangerie Saint-Honoré")
                            .emailClient("pain@saint-honore.fr")
                            .telephoneClient("02 40 55 66 77")
                            .adresseClient("8 Place Royale, 44000 Nantes")
                            .date(LocalDate.now().minusDays(1))
                            .dateEcheance(LocalDate.now().plusDays(14))
                            .montantTotal(new BigDecimal("1200.00"))
                            .statut(StatutDocument.BROUILLON)
                            .description("Mise en place d'un système de commande en ligne pour click & collect.")
                            .build(),

                    // Factures
                    Document.builder()
                            .reference("FAC-2026-001")
                            .type(TypeDocument.FACTURE)
                            .nomClient("Global Logistics SAS")
                            .emailClient("compta@globallogistics.eu")
                            .telephoneClient("03 20 88 99 00")
                            .adresseClient("100 Avenue de Flandre, 59000 Lille")
                            .date(LocalDate.now().minusDays(20))
                            .dateEcheance(LocalDate.now().minusDays(5))
                            .montantTotal(new BigDecimal("5400.00"))
                            .statut(StatutDocument.PAYEE)
                            .description("Maintenance trimestrielle et support infrastructure applicative.")
                            .build(),

                    Document.builder()
                            .reference("FAC-2026-002")
                            .type(TypeDocument.FACTURE)
                            .nomClient("Cabinet Médical Dr. Dupont")
                            .emailClient("secretariat@dupont-med.fr")
                            .telephoneClient("05 56 44 33 22")
                            .adresseClient("22 Cours de l'Intendance, 33000 Bordeaux")
                            .date(LocalDate.now().minusDays(7))
                            .dateEcheance(LocalDate.now().plusDays(23))
                            .montantTotal(new BigDecimal("2150.00"))
                            .statut(StatutDocument.EN_ATTENTE)
                            .description("Configuration logicielle et sauvegarde sécurisée des données patients.")
                            .build(),

                    Document.builder()
                            .reference("FAC-2026-003")
                            .type(TypeDocument.FACTURE)
                            .nomClient("BioGreen Jardins")
                            .emailClient("admin@biogreen.fr")
                            .telephoneClient("04 93 11 22 33")
                            .adresseClient("15 Promenade des Anglais, 06000 Nice")
                            .date(LocalDate.now().minusDays(45))
                            .dateEcheance(LocalDate.now().minusDays(15))
                            .montantTotal(new BigDecimal("1800.00"))
                            .statut(StatutDocument.EN_RETARD)
                            .description("Campagne publicitaire ciblée et gestion des réseaux sociaux.")
                            .build()
            );

            documentRepository.saveAll(initialDocs);
            log.info("Données initiales insérées avec succès : {} documents créés.", initialDocs.size());
        }
    }
}
