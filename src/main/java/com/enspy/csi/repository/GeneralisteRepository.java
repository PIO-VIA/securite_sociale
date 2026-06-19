package com.enspy.csi.repository;

import com.enspy.csi.entity.Generaliste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralisteRepository extends JpaRepository<Generaliste, Long> {
    List<Generaliste> findByEstAssure(Boolean estAssure);
    Optional<Generaliste> findByMatricule(String matricule);
    Optional<Generaliste> findByEmail(String email);
}
