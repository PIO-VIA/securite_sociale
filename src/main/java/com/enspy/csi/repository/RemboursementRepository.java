package com.enspy.csi.repository;

import com.enspy.csi.entity.Remboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    @Query("SELECT r FROM Remboursement r JOIN r.feuillesMaladie f WHERE f.id = :feuilleMaladieId")
    Optional<Remboursement> findByFeuilleMaladieId(@Param("feuilleMaladieId") Long feuilleMaladieId);
    List<Remboursement> findByModePaiement(String modePaiement);
    List<Remboursement> findByStatut(String statut);

    @Query("SELECT SUM(r.montant) FROM Remboursement r WHERE r.statut = 'EFFECTUE'")
    Double sumTotalRemboursements();

    @Query("SELECT SUM(r.montant) FROM Remboursement r WHERE r.dateRemboursement BETWEEN :debut AND :fin")
    Double sumRemboursementsBetween(
        @Param("debut") LocalDate debut,
        @Param("fin") LocalDate fin
    );
}
