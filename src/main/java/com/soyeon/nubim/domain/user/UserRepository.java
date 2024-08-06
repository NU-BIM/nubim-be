package com.soyeon.nubim.domain.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.common.enums.Gender;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	@Query("SELECT u.userId FROM User u WHERE u.email = :email")
	Optional<Long> findUserIdByEmail(String email);

	Boolean existsByNickname(String nickname);

	Page<User> findByNicknameStartsWith(Pageable pageable, String query);

	@Modifying
	@Query("UPDATE User u SET u.profileImageUrl = :newProfileImage WHERE u.userId = :userId")
	void updateProfileImage(String newProfileImage, Long userId);

	@Modifying
	@Query("UPDATE User u "
		+ "SET u.username = :username, u.nickname = :nickname, u.profileIntroduction = :profileIntroduction,"
		+ "u.phoneNumber = :phoneNumber, u.birthDate = :birthDate, u.gender = :gender "
		+ "WHERE u.userId = :userId")
	int updateProfile(String username, String nickname, String profileIntroduction,
		String phoneNumber, LocalDateTime birthDate, Gender gender, Long userId);

	@Query("SELECT CASE WHEN u.nickname = :nickname THEN true ELSE false END FROM User u WHERE u.userId = :userId")
	boolean isNicknameMatchingForUser(Long userId, String nickname);
}
