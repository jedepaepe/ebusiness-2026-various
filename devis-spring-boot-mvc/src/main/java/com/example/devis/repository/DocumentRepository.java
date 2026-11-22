package com.example.devis.repository;

import com.example.devis.model.Document;
import com.example.devis.model.StatutDocument;
import com.example.devis.model.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByTypeOrderByDateDesc(TypeDocument type);

    List<Document> findByTypeAndStatutOrderByDateDesc(TypeDocument type, StatutDocument statut);

    @Query("SELECT d FROM Document d WHERE d.type = :type AND " +
           "(LOWER(d.nomClient) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.reference) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY d.date DESC")
    List<Document> searchByTypeAndKeyword(@Param("type") TypeDocument type, @Param("search") String search);

    @Query("SELECT d FROM Document d WHERE d.type = :type AND d.statut = :statut AND " +
           "(LOWER(d.nomClient) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.reference) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY d.date DESC")
    List<Document> searchByTypeAndStatutAndKeyword(@Param("type") TypeDocument type,
                                                  @Param("statut") StatutDocument statut,
                                                  @Param("search") String search);

    long countByType(TypeDocument type);

    long countByTypeAndStatut(TypeDocument type, StatutDocument statut);

    @Query("SELECT COALESCE(SUM(d.montantTotal), 0) FROM Document d WHERE d.type = :type")
    BigDecimal sumMontantTotalByType(@Param("type") TypeDocument type);

    @Query("SELECT COALESCE(SUM(d.montantTotal), 0) FROM Document d WHERE d.type = :type AND d.statut = :statut")
    BigDecimal sumMontantTotalByTypeAndStatut(@Param("type") TypeDocument type, @Param("statut") StatutDocument statut);

    List<Document> findTop5ByOrderByDateCreationDesc();

    boolean existsByReference(String reference);
}
