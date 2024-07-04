package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	@PostMapping("/photos/upload-url")
	public ResponseEntity<String> getPhotoUploadUrl(
		@RequestParam String uploadPath,
		@RequestParam String contentType) {

		String presignedUrl = albumService.handlePhotoUploadUrlGeneration(uploadPath, contentType);
		return ResponseEntity.ok(presignedUrl);
	}

	@PostMapping("/photos/upload-urls")
	public ResponseEntity<List<String>> getPhotoUploadUrls(
		@RequestParam String contentType,
		@RequestParam int fileCnt) {

		List<String> presignedUrls = albumService.handlePhotoUploadUrlsGeneration(contentType, fileCnt);
		return ResponseEntity.ok(presignedUrls);
	}
}
