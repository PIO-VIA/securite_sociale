package com.enspy.csi.repository;

import com.enspy.csi.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByAssureId(Long assureId);
    List<Consultation> findByGeneralisteId(Long generalisteId);
    List<Consultation> findByAssureIdAndGeneralisteId(Long assureId, Long generalisteId);
    Long countByAssureId(Long assureId);
}
