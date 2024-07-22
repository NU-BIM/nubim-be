package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumControllerV1 {

	private final AlbumService albumService;

	@Operation(description = "앨범 생성")
	@PostMapping
	public ResponseEntity<AlbumCreateResponseDto> createAlbum(
		@RequestBody AlbumCreateRequestDto albumCreateRequestDto) {

		AlbumCreateResponseDto album = albumService.createAlbum(albumCreateRequestDto);
		return ResponseEntity.ok(album);
	}

	@Operation(description = "앨범 id로 앨범, 연관된 경로를 검색한다")
	@GetMapping("/{albumId}")
	public ResponseEntity<AlbumReadResponseDto> getAlbumWithLocations(@PathVariable Long albumId) {
		AlbumReadResponseDto album = albumService.findByIdWithLocations(albumId);
		return ResponseEntity.ok(album);
	}

	@Operation(description = "사용자 id로 해당 사용자의 모든 앨범, 연관된 경로를 검색한다.")
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<AlbumReadResponseDto>> getUserAlbums(@PathVariable Long userId) {
		List<AlbumReadResponseDto> albums = albumService.findAlbumsByUserId(userId);
		return ResponseEntity.ok(albums);
	}

	@Operation(description = "앨범 id로 앨범을 삭제한다")
	@DeleteMapping("/{albumId}")
	public void deleteAlbum(@PathVariable Long albumId) {
		albumService.deleteAlbum(albumId);
	}

	@PostMapping("/photos/upload-urls")
	public ResponseEntity<List<String>> getPhotoUploadUrls(
		@RequestParam List<String> contentTypes) {

		List<String> presignedUrls = albumService.handlePhotoUploadUrlsGeneration(contentTypes);
		return ResponseEntity.ok(presignedUrls);
	}
}
