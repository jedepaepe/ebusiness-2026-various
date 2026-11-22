package com.example.devis.service;

import com.example.devis.model.Document;
import com.example.devis.model.StatutDocument;
import com.example.devis.model.TypeDocument;
import com.example.devis.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional(readOnly = true)
    public List<Document> getDocuments(TypeDocument type, StatutDocument statut, String search) {
        if (search != null && !search.trim().isEmpty()) {
            if (statut != null) {
                return documentRepository.searchByTypeAndStatutAndKeyword(type, statut, search.trim());
            } else {
                return documentRepository.searchByTypeAndKeyword(type, search.trim());
            }
        } else if (statut != null) {
            return documentRepository.findByTypeAndStatutOrderByDateDesc(type, statut);
        } else {
            return documentRepository.findByTypeOrderByDateDesc(type);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public Document save(Document document) {
        if (document.getReference() == null || document.getReference().trim().isEmpty()) {
            document.setReference(generateReference(document.getType()));
        }
        if (document.getDate() == null) {
            document.setDate(LocalDate.now());
        }
        if (document.getStatut() == null) {
            document.setStatut(document.isDevis() ? StatutDocument.BROUILLON : StatutDocument.EN_ATTENTE);
        }
        return documentRepository.save(document);
    }

    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    public Document updateStatut(Long id, StatutDocument statut) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document non trouvé avec l'id: " + id));
        doc.setStatut(statut);
        return documentRepository.save(doc);
    }

    public Document convertDevisToFacture(Long devisId) {
        Document devis = documentRepository.findById(devisId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé avec l'id: " + devisId));

        if (!devis.isDevis()) {
            throw new IllegalStateException("Le document sélectionné n'est pas un devis");
        }

        // Marquer le devis comme accepté
        devis.setStatut(StatutDocument.ACCEPTE);
        documentRepository.save(devis);

        // Créer la facture associée
        Document facture = Document.builder()
                .reference(generateReference(TypeDocument.FACTURE))
                .type(TypeDocument.FACTURE)
                .nomClient(devis.getNomClient())
                .emailClient(devis.getEmailClient())
                .telephoneClient(devis.getTelephoneClient())
                .adresseClient(devis.getAdresseClient())
                .date(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .montantTotal(devis.getMontantTotal())
                .statut(StatutDocument.EN_ATTENTE)
                .description("Facturation issue du devis " + devis.getReference() + 
                             (devis.getDescription() != null ? "\n" + devis.getDescription() : ""))
                .build();

        return documentRepository.save(facture);
    }

    @Transactional(readOnly = true)
    public long countByType(TypeDocument type) {
        return documentRepository.countByType(type);
    }

    @Transactional(readOnly = true)
    public long countByTypeAndStatut(TypeDocument type, StatutDocument statut) {
        return documentRepository.countByTypeAndStatut(type, statut);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumMontantTotalByType(TypeDocument type) {
        return documentRepository.sumMontantTotalByType(type);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumMontantTotalByTypeAndStatut(TypeDocument type, StatutDocument statut) {
        return documentRepository.sumMontantTotalByTypeAndStatut(type, statut);
    }

    @Transactional(readOnly = true)
    public List<Document> getRecentDocuments() {
        return documentRepository.findTop5ByOrderByDateCreationDesc();
    }

    public String generateReference(TypeDocument type) {
        String prefix = type == TypeDocument.DEVIS ? "DEV" : "FAC";
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = documentRepository.countByType(type) + 1;
        String ref = String.format("%s-%s-%03d", prefix, yearMonth, count);
        
        // S'assurer de l'unicité
        int seq = 1;
        while (documentRepository.existsByReference(ref)) {
            ref = String.format("%s-%s-%03d", prefix, yearMonth, count + seq);
            seq++;
        }
        return ref;
    }
}
