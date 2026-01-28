package com.acervo.api.repository;

import com.acervo.api.domain.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {
}
