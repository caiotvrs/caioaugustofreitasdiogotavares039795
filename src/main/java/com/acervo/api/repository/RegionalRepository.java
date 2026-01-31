package com.acervo.api.repository;

import com.acervo.api.domain.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    Optional<Regional> findByExternalIdAndAtivoTrue(Long externalId);

    List<Regional> findAllByAtivoTrue();

    @Modifying
    @Query("UPDATE Regional r SET r.ativo = false WHERE r.externalId NOT IN :externalIds AND r.ativo = true")
    int inactivateNotInExternalIds(@Param("externalIds") List<Long> externalIds);

    @Modifying
    @Query("UPDATE Regional r SET r.ativo = false WHERE r.externalId = :externalId AND r.ativo = true")
    int inactivateByExternalId(@Param("externalId") Long externalId);
}
