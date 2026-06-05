package com.enspy.csi.repository;

import com.enspy.csi.entity.Specialiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialisteRepository extends JpaRepository<Specialiste, Long> {
    List<Specialiste> findByDomaineSpecialisation(String domaine);
    Optional<Specialiste> findByMatricule(String matricule);
}
