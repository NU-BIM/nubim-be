package com.soyeon.nubim.domain.album;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.soyeon.nubim.common.util.aws.S3ImageDeleter;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumUpdateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationUpdateRequestDto;
import com.soyeon.nubim.domain.album.exception.AlbumNotFoundException;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.album.mapper.LocationMapper;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

class AlbumServiceTest {

	@Mock
	private AlbumValidator albumValidator;
	@Mock
	private AlbumRepository albumRepository;
	@Mock
	private AlbumMapper albumMapper;
	@Mock
	private UserService userService;
	@Mock
	private LocationMapper locationMapper;
	@Mock
	private S3ImageDeleter s3ImageDeleter;
	@Mock
	private PostRepository postRepository;

	@InjectMocks
	private AlbumService albumService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("앨범 id로 검색 - 성공 case")
	void findById() {
		Long albumId = 1L;
		Album expectedAlbum = new Album();
		when(albumRepository.findById(albumId)).thenReturn(Optional.of(expectedAlbum));

		Album result = albumService.findById(albumId);

		assertNotNull(result);
		assertEquals(expectedAlbum, result);
	}

	@Test
	@DisplayName("앨범 id로 검색 - 실패 case : 앨범이 없는 경우")
	void findById_shouldThrowException_whenAlbumNotFound() {
		Long albumId = 1L;
		when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

		assertThrows(AlbumNotFoundException.class, () -> albumService.findById(albumId));
	}

	@Test
	@DisplayName("앨범 생성 - 성공 case")
	void createAlbum() {
		AlbumCreateRequestDto requestDto = AlbumCreateRequestDto.builder().build();
		User currentUser = new User();
		Album album = new Album();
		Album savedAlbum = new Album();
		AlbumCreateResponseDto expectedResponseDto = AlbumCreateResponseDto.builder().build();

		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(albumMapper.toEntity(requestDto, currentUser)).thenReturn(album);
		when(albumRepository.save(album)).thenReturn(savedAlbum);
		when(albumMapper.toAlbumCreateResponseDto(savedAlbum)).thenReturn(expectedResponseDto);

		AlbumCreateResponseDto result = albumService.createAlbum(requestDto);

		assertNotNull(result);
		assertEquals(expectedResponseDto, result);
		verify(albumRepository).save(album);
	}

	@Test
	void findByIdWithLocations_shouldReturnAlbumWithLocations_whenAlbumExists() {
		// Given
		Long albumId = 1L;
		Album album = new Album();
		AlbumReadResponseDto expectedResponseDto = AlbumReadResponseDto.builder().build();

		when(albumRepository.findByIdWithLocations(albumId)).thenReturn(Optional.of(album));
		when(albumMapper.toAlbumReadResponseDto(album)).thenReturn(expectedResponseDto);

		// When
		AlbumReadResponseDto result = albumService.findByIdWithLocations(albumId);

		// Then
		assertNotNull(result);
		assertEquals(expectedResponseDto, result);
	}

	@Test
	void updateAlbum_shouldUpdateAndReturnAlbum() {
		// Given
		Long albumId = 1L;
		Long userId = 1L;
		AlbumUpdateRequestDto updateRequestDto = AlbumUpdateRequestDto.builder()
			.description("New description")
			.photoUrls(Map.of())
			.locations(List.of(LocationUpdateRequestDto.builder().build()))
		.build();

		Album existingAlbum = new Album();
		existingAlbum.setPhotoUrls(new HashMap<>());

		when(userService.getCurrentUserId()).thenReturn(userId);
		when(albumRepository.findByIdWithLocations(albumId)).thenReturn(Optional.of(existingAlbum));
		when(albumRepository.save(existingAlbum)).thenReturn(existingAlbum);
		when(albumMapper.toAlbumReadResponseDto(existingAlbum)).thenReturn(AlbumReadResponseDto.builder().build());

		// When
		AlbumReadResponseDto result = albumService.updateAlbum(albumId, updateRequestDto);

		// Then
		assertNotNull(result);
		verify(albumValidator).validateAlbumOwner(albumId, userId);
		verify(albumRepository).deleteLocationsByAlbumId(albumId);
		verify(albumRepository).save(existingAlbum);
	}

	@Test
	void deleteAlbum_shouldDeleteAlbumAndRelatedData() {
		// Given
		Long albumId = 1L;
		Long userId = 1L;
		Album album = new Album();

		when(userService.getCurrentUserId()).thenReturn(userId);
		when(albumRepository.findByIdWithLocations(albumId)).thenReturn(Optional.of(album));

		// When
		albumService.deleteAlbum(albumId);

		// Then
		verify(albumValidator).validateAlbumOwner(albumId, userId);
		verify(albumRepository).deleteLocationsByAlbumId(albumId);
		verify(albumRepository).deleteByAlbumId(albumId);
		verify(postRepository).deletePostByDeletedAlbumId(albumId);
	}
}