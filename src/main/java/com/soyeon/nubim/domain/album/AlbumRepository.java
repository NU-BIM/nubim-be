package com.soyeon.nubim.domain.album;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

	@Query("SELECT a FROM Album a LEFT JOIN FETCH a.locations WHERE a.albumId = :albumId")
	Optional<Album> findByIdWithLocations(@Param("albumId") Long albumId);

}
