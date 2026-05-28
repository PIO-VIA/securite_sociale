package com.enspy.csi.repository;

import com.enspy.csi.entity.FeuillemMaladie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeuillemMaladieRepository extends JpaRepository<FeuillemMaladie, Long> {
}
