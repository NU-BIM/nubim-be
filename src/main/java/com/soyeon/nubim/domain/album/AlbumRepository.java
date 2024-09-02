package com.soyeon.nubim.domain.album;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

	@Query("SELECT a.user.userId FROM Album a WHERE a.albumId = :albumId")
	Optional<Long> findAlbumOwnerIdByAlbumId(Long albumId);

	@Query("SELECT a FROM Album a LEFT JOIN FETCH a.locations WHERE a.albumId = :albumId")
	Optional<Album> findByIdWithLocations(@Param("albumId") Long albumId);

	@Query("SELECT a FROM Album a LEFT JOIN FETCH a.locations WHERE a.user = :user")
	List<Album> findByUser(@Param("user") User user);

	@Query("SELECT a FROM Album a LEFT JOIN FETCH a.locations WHERE a.user.email = :email")
	List<Album> findAlbumsByEmail(@Param("email") String email);

	@Query("SELECT a FROM Album a LEFT JOIN FETCH a.locations WHERE a.user.email = :email and a.postLinked = false")
	List<Album> findUnlinkedAlbumsByEmail(@Param("email") String email);

	@Modifying
	@Query("DELETE FROM Location l WHERE l.album.albumId = :albumId")
	void deleteLocationsByAlbumId(@Param("albumId") Long albumId);

	@Modifying
	@Query("UPDATE Album a SET a.isDeleted = true WHERE a.albumId = :albumId")
	void deleteByAlbumId(@Param("albumId") Long albumId);

}
