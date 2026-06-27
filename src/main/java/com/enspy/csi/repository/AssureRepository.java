package com.enspy.csi.repository;

import com.enspy.csi.entity.Assure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssureRepository extends JpaRepository<Assure, Long> {
    Optional<Assure> findByIdAssure(String idAssure);
    List<Assure> findByMedecinTraitantId(Long generalisteId);
    boolean existsByIdAssure(String idAssure);
    Optional<Assure> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.assure FROM PrescriptionConsultation pc JOIN pc.consultation c WHERE pc.matriculeMedecin = :matricule")
    List<Assure> findAssuresBySpecialisteMatricule(@org.springframework.data.repository.query.Param("matricule") String matricule);
}
