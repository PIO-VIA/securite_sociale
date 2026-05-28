package com.enspy.csi.repository;

import com.enspy.csi.entity.Remboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
}
