package com.enspy.csi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy.csi.entity.FeuillemMaladie;

@Repository
public interface FeuillemMaladieRepository extends JpaRepository<FeuillemMaladie, Long> {

    Optional<FeuillemMaladie> findByIdFeuille(String idFeuille);

    Optional<FeuillemMaladie> findByConsultationId(Long consultationId);

    boolean existsByConsultationId(Long consultationId);

    List<FeuillemMaladie> findByEstRembourse(Boolean estRembourse);
}