package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.common.util.aws.S3PresignedUrlGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumPhotoUploadUrlService {

	private final S3PresignedUrlGenerator s3PresignedUrlGenerator;

	public List<String> generatePhotoUploadUrlsWithRandomPath(List<String> contentTypes) {
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes);
	}

	public List<String> generatePhotoUploadUrlsWithCustomPath(List<String> contentTypes, String uploadPath) {
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes, uploadPath);
	}
}
