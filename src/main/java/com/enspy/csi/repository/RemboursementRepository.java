package com.enspy.csi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.enspy.csi.entity.Remboursement;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {

    Optional<Remboursement> findByFeuilleMaladieId(Long feuilleMaladieId);

    List<Remboursement> findByModePaiement(String modePaiement);

    @Query("SELECT SUM(r.montant) FROM Remboursement r")
    Double sumTotalRemboursements();

    @Query("SELECT SUM(r.montant) FROM Remboursement r WHERE r.dateRemboursement BETWEEN :debut AND :fin")
    Double sumRemboursementsBetween(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );
}