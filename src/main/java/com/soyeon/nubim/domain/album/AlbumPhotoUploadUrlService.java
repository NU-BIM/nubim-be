package com.soyeon.nubim.domain.album;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.common.util.aws.S3PresignedUrlGenerator;
import com.soyeon.nubim.domain.album.exception.InvalidContentTypeException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumPhotoUploadUrlService {

	public static final String MIME_TYPE_IMAGE = "image";
	private final S3PresignedUrlGenerator s3PresignedUrlGenerator;

	public List<String> generatePhotoUploadUrlsWithRandomPath(List<String> contentTypes) {
		validateContentTypes(contentTypes);
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes);
	}

	public List<String> generatePhotoUploadUrlsWithCustomPath(List<String> contentTypes, String uploadPath) {
		validateContentTypes(contentTypes);
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes, uploadPath);
	}

	private void validateContentTypes(List<String> contentTypes) {
		for (String contentType : contentTypes) {
			if (!contentType.startsWith(MIME_TYPE_IMAGE)) {
				throw new InvalidContentTypeException("Invalid content type: " + contentType);
			}
		}
	}
}
