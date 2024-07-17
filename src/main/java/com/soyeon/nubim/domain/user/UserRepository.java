package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	@Query("SELECT u.userId FROM User u WHERE u.email = :email")
	Optional<Long> findUserIdByEmail(String email);
}
