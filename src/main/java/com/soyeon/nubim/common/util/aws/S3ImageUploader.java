package com.soyeon.nubim.common.util.aws;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageUploader {

	private final S3Client s3Client;
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${spring.cloud.aws.region.static}")
	private String region;

	public String uploadImage(String uploadPath, MultipartFile image) {
		try {
			PutObjectRequest uploadRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(uploadPath)
				.build();

			s3Client.putObject(uploadRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
			log.debug("Successfully uploaded image to {}", uploadPath);
			return getFullS3Path(uploadPath);
		} catch (S3Exception | IOException e) {
			log.debug("Failed to upload image, {}", e.getMessage());
			return "upload fail";
		}
	}

	public String getFullS3Path(String uploadPath) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uploadPath);
	}

}
