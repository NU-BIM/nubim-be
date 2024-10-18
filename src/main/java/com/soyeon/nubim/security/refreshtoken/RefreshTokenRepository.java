package com.soyeon.nubim.security.refreshtoken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByEmail(String email);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.email = :email")
	int deleteByEmail(String email);

	boolean existsByToken(String token);

}
