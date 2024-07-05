package com.soyeon.nubim.domain.album;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.common.util.aws.S3PresignedUrlGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final S3PresignedUrlGenerator s3PresignedUrlGenerator;
	private final AlbumRepository albumRepository;

	public Optional<Album> findById(Long id) {
		return albumRepository.findById(id);
	}

	public List<String> handlePhotoUploadUrlsGeneration(List<String> contentTypes) {
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes);
	}

}
