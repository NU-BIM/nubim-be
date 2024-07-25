package com.soyeon.nubim.security.refreshtoken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByEmail(String email);

	Optional<RefreshToken> deleteByToken(String token);

	boolean existsByToken(String token);

}
