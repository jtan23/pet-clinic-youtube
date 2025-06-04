package com.bw.owner.repository;

import com.bw.owner.domain.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    Page<Owner> findByLastName(Pageable pageable, String lastName);

}
