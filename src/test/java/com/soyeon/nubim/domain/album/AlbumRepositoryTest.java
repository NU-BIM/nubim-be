package com.soyeon.nubim.domain.album;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.album.exception.AlbumNotFoundException;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class AlbumRepositoryTest {

	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager entityManager;

	private Album album;
	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setRole(Role.USER);
		user.setUsername("testUser");
		user.setNickname("testNickname");
		user.setEmail("test@email.com");

		album = new Album();
		album.setPhotoUrls(Map.of());

		Location location1 = Location.builder()
			.latitude(0.0)
			.longitude(0.0)
			.visitedAt(LocalDateTime.now())
			.build();
		Location location2 = Location.builder()
			.latitude(0.0)
			.longitude(0.0)
			.visitedAt(LocalDateTime.now())
			.build();

		album.setLocations(List.of(location1, location2));
		album.bindLocations();

		album.setUser(user);

		userRepository.save(user);
		albumRepository.save(album);
	}

	@Test
	@DisplayName("앨범을 생성한 사용자의 ID 검색 - 성공 case")
	void findAlbumOwnerIdByAlbumId() {
		Optional<Long> ownerId = albumRepository.findAlbumOwnerIdByAlbumId(album.getAlbumId());

		assertTrue(ownerId.isPresent());
		assertEquals(user.getUserId(), ownerId.get());
	}

	@Test
	@DisplayName("앨범과 연관된 장소를 한번에 검색 - 성공 case")
	void findByIdWithLocations() {
		Optional<Album> foundAlbum = albumRepository.findByIdWithLocations(album.getAlbumId());
		assertTrue(foundAlbum.isPresent());
		assertEquals(2, foundAlbum.get().getLocations().size());
	}

	@Test
	@DisplayName("사용자로 앨범 검색 - 성공 case")
	void findByUser() {
		List<Album> albums = albumRepository.findByUser(user);
		assertFalse(albums.isEmpty());
		assertEquals(album.getAlbumId(), albums.get(0).getAlbumId());
	}

	@Test
	@DisplayName("사용자의 이메일로 앨범 검색 - 성공 case")
	void findAlbumsByEmail() {
		List<Album> albums = albumRepository.findAlbumsByEmail(user.getEmail());
		assertFalse(albums.isEmpty());
		assertEquals(album.getAlbumId(), albums.get(0).getAlbumId());
	}

	@Test
	@DisplayName("게시물과 연결되지 않은 앨범 검색 - 성공 case")
	void findUnlinkedAlbumsByEmail() {
		List<Album> unlinkedAlbums = albumRepository.findUnlinkedAlbumsByEmail(user.getEmail());
		assertFalse(unlinkedAlbums.isEmpty());
		assertEquals(album.getAlbumId(), unlinkedAlbums.get(0).getAlbumId());

		album.setPostLinked(true);
		unlinkedAlbums = albumRepository.findUnlinkedAlbumsByEmail(user.getEmail());
		assertTrue(unlinkedAlbums.isEmpty());
	}

	@Test
	@DisplayName("앨범과 연관된 경로 삭제 - 성공 case")
	void deleteLocationsByAlbumId() {
		albumRepository.deleteLocationsByAlbumId(album.getAlbumId());
		entityManager.clear();

		Album refreshedAlbum = albumRepository.findById(album.getAlbumId()).orElseThrow();
		assertTrue(refreshedAlbum.getLocations().isEmpty());
	}

	@Test
	@DisplayName("앨범 삭제 테스트 - 성공 case : 삭제된 앨범은 조회 시 NotFoundException을 발생시킨다")
	void deleteByAlbumId() {
		albumRepository.deleteByAlbumId(album.getAlbumId());

		assertThrows(AlbumNotFoundException.class, () ->
			albumRepository.findByIdWithLocations(album.getAlbumId()).orElseThrow(
				()-> new AlbumNotFoundException(album.getAlbumId())
			));
	}
}