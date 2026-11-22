package com.example.devis.controller;

import com.example.devis.model.Document;
import com.example.devis.model.StatutDocument;
import com.example.devis.model.TypeDocument;
import com.example.devis.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long nbDevis = documentService.countByType(TypeDocument.DEVIS);
        long nbFactures = documentService.countByType(TypeDocument.FACTURE);
        BigDecimal totalDevis = documentService.sumMontantTotalByType(TypeDocument.DEVIS);
        BigDecimal totalFactures = documentService.sumMontantTotalByType(TypeDocument.FACTURE);
        BigDecimal totalPaye = documentService.sumMontantTotalByTypeAndStatut(TypeDocument.FACTURE, StatutDocument.PAYEE);
        BigDecimal totalEnAttente = documentService.sumMontantTotalByTypeAndStatut(TypeDocument.FACTURE, StatutDocument.EN_ATTENTE);
        long facturesEnAttente = documentService.countByTypeAndStatut(TypeDocument.FACTURE, StatutDocument.EN_ATTENTE);
        long devisEnAttente = documentService.countByTypeAndStatut(TypeDocument.DEVIS, StatutDocument.ENVOYE);

        model.addAttribute("nbDevis", nbDevis);
        model.addAttribute("nbFactures", nbFactures);
        model.addAttribute("totalDevis", totalDevis);
        model.addAttribute("totalFactures", totalFactures);
        model.addAttribute("totalPaye", totalPaye);
        model.addAttribute("totalEnAttente", totalEnAttente);
        model.addAttribute("facturesEnAttente", facturesEnAttente);
        model.addAttribute("devisEnAttente", devisEnAttente);
        model.addAttribute("recentDocuments", documentService.getRecentDocuments());

        return "dashboard";
    }

    @GetMapping("/devis")
    public String listDevis(@RequestParam(required = false) StatutDocument statut,
                            @RequestParam(required = false) String search,
                            Model model) {
        List<Document> devisList = documentService.getDocuments(TypeDocument.DEVIS, statut, search);
        model.addAttribute("documents", devisList);
        model.addAttribute("currentType", TypeDocument.DEVIS);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statutsDisponibles", List.of(
                StatutDocument.BROUILLON,
                StatutDocument.ENVOYE,
                StatutDocument.ACCEPTE,
                StatutDocument.REFUSE
        ));
        return "documents/list";
    }

    @GetMapping("/factures")
    public String listFactures(@RequestParam(required = false) StatutDocument statut,
                               @RequestParam(required = false) String search,
                               Model model) {
        List<Document> facturesList = documentService.getDocuments(TypeDocument.FACTURE, statut, search);
        model.addAttribute("documents", facturesList);
        model.addAttribute("currentType", TypeDocument.FACTURE);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statutsDisponibles", List.of(
                StatutDocument.EN_ATTENTE,
                StatutDocument.PAYEE,
                StatutDocument.EN_RETARD,
                StatutDocument.ANNULEE
        ));
        return "documents/list";
    }

    @GetMapping("/documents/nouveau")
    public String createForm(@RequestParam(defaultValue = "DEVIS") TypeDocument type, Model model) {
        Document doc = Document.builder()
                .type(type)
                .reference(documentService.generateReference(type))
                .date(LocalDate.now())
                .dateEcheance(type == TypeDocument.FACTURE ? LocalDate.now().plusDays(30) : LocalDate.now().plusDays(15))
                .statut(type == TypeDocument.DEVIS ? StatutDocument.BROUILLON : StatutDocument.EN_ATTENTE)
                .montantTotal(BigDecimal.ZERO)
                .build();

        model.addAttribute("document", doc);
        model.addAttribute("isNew", true);
        model.addAttribute("statuts", type == TypeDocument.DEVIS ?
                List.of(StatutDocument.BROUILLON, StatutDocument.ENVOYE, StatutDocument.ACCEPTE, StatutDocument.REFUSE) :
                List.of(StatutDocument.EN_ATTENTE, StatutDocument.PAYEE, StatutDocument.EN_RETARD, StatutDocument.ANNULEE));
        return "documents/form";
    }

    @GetMapping("/documents/modifier/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return documentService.findById(id).map(doc -> {
            model.addAttribute("document", doc);
            model.addAttribute("isNew", false);
            model.addAttribute("statuts", doc.isDevis() ?
                    List.of(StatutDocument.BROUILLON, StatutDocument.ENVOYE, StatutDocument.ACCEPTE, StatutDocument.REFUSE) :
                    List.of(StatutDocument.EN_ATTENTE, StatutDocument.PAYEE, StatutDocument.EN_RETARD, StatutDocument.ANNULEE));
            return "documents/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Document introuvable");
            return "redirect:/dashboard";
        });
    }

    @PostMapping("/documents/enregistrer")
    public String saveDocument(@ModelAttribute Document document, RedirectAttributes redirectAttributes) {
        boolean isNew = (document.getId() == null);
        Document saved = documentService.save(document);

        redirectAttributes.addFlashAttribute("successMessage",
                (saved.isDevis() ? "Le devis " : "La facture ") + saved.getReference() +
                (isNew ? " a été créé avec succès !" : " a été mis à jour avec succès !"));

        return saved.isDevis() ? "redirect:/devis" : "redirect:/factures";
    }

    @GetMapping("/documents/details/{id}")
    public String viewDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return documentService.findById(id).map(doc -> {
            model.addAttribute("document", doc);
            return "documents/detail";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Document introuvable");
            return "redirect:/dashboard";
        });
    }

    @PostMapping("/documents/supprimer/{id}")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return documentService.findById(id).map(doc -> {
            boolean wasDevis = doc.isDevis();
            String ref = doc.getReference();
            documentService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    (wasDevis ? "Le devis " : "La facture ") + ref + " a été supprimé.");
            return wasDevis ? "redirect:/devis" : "redirect:/factures";
        }).orElse("redirect:/dashboard");
    }

    @PostMapping("/documents/{id}/changer-statut")
    public String changeStatut(@PathVariable Long id,
                               @RequestParam StatutDocument statut,
                               @RequestParam(defaultValue = "list") String source,
                               RedirectAttributes redirectAttributes) {
        Document doc = documentService.updateStatut(id, statut);
        redirectAttributes.addFlashAttribute("successMessage",
                "Statut de " + doc.getReference() + " mis à jour vers " + statut.getLibelle());

        if ("details".equals(source)) {
            return "redirect:/documents/details/" + id;
        }
        return doc.isDevis() ? "redirect:/devis" : "redirect:/factures";
    }

    @PostMapping("/documents/{id}/convertir")
    public String convertToFacture(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Document facture = documentService.convertDevisToFacture(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Le devis a été converti en facture avec succès ! Nouvelle facture : " + facture.getReference());
            return "redirect:/documents/details/" + facture.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la conversion : " + e.getMessage());
            return "redirect:/devis";
        }
    }
}
