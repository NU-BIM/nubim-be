package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.common.util.aws.S3PresignedUrlGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final S3PresignedUrlGenerator s3PresignedUrlGenerator;

	public String handlePhotoUploadUrlGeneration(String uploadPath, String contentType) {
		return s3PresignedUrlGenerator.generatePresignedUrl(uploadPath, contentType);
	}

	public List<String> handlePhotoUploadUrlsGeneration(String contentType, int fileCnt) {
		return s3PresignedUrlGenerator.generatePresignedUrls(contentType, fileCnt);
	}

}
