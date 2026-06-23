package com.enspy.csi.repository;

import com.enspy.csi.entity.Prescription;
import com.enspy.csi.entity.PrescriptionConsultation;
import com.enspy.csi.entity.PrescriptionMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByConsultationId(Long consultationId);

    @Query("SELECT p FROM PrescriptionMedicament p WHERE p.consultation.id = :consultationId")
    List<PrescriptionMedicament> findMedicamentsByConsultationId(@Param("consultationId") Long consultationId);

    @Query("SELECT p FROM PrescriptionConsultation p WHERE p.consultation.id = :consultationId")
    List<PrescriptionConsultation> findConsultationsByConsultationId(@Param("consultationId") Long consultationId);

    @Query("SELECT p FROM PrescriptionConsultation p WHERE p.matriculeMedecin = :matricule")
    List<PrescriptionConsultation> findConsultationsByMatriculeMedecin(@Param("matricule") String matricule);
}
