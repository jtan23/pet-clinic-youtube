package com.bw.visit.repository;

import com.bw.visit.domain.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    List<Visit> findByPetId(int petId);

}
