package com.enspy.csi.repository;

import com.enspy.csi.entity.Specialiste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialisteRepository extends JpaRepository<Specialiste, Long> {
}
