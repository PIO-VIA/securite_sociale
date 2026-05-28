package com.enspy.csi.repository;

import com.enspy.csi.entity.Generaliste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralisteRepository extends JpaRepository<Generaliste, Long> {
}
