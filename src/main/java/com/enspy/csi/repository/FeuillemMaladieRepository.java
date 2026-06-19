package com.enspy.csi.repository;

import com.enspy.csi.entity.FeuillemMaladie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeuillemMaladieRepository extends JpaRepository<FeuillemMaladie, Long> {
    Optional<FeuillemMaladie> findByIdFeuille(String idFeuille);
    Optional<FeuillemMaladie> findByConsultationId(Long consultationId);
    boolean existsByConsultationId(Long consultationId);
    List<FeuillemMaladie> findByEstRembourse(Boolean estRembourse);
    List<FeuillemMaladie> findByConsultationAssureId(Long assureId);
    List<FeuillemMaladie> findByConsultationGeneralisteId(Long generalisteId);
    List<FeuillemMaladie> findByConsultationGeneralisteEmail(String email);
}
