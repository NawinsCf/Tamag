package com.tamago.feedservice.repository;

import com.tamago.feedservice.model.Tamago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TamagoRepository extends JpaRepository<Tamago, Long> {
	boolean existsByIduserAndEstVivantTrue(Long iduser);
	java.util.List<Tamago> findByIduserAndEstVivantTrue(Long iduser);

	// Use pessimistic write lock to prevent concurrent updates on the same row.
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from Tamago t where t.id = :id")
	Optional<Tamago> findByIdForUpdate(@Param("id") Long id);
}
