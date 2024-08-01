package com.soyeon.nubim.common.util.aws;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageDeleter {

	private final S3Client s3Client;
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	public void deleteImage(String objectKey) {
		try{
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(objectKey)
				.build();
			s3Client.deleteObject(deleteObjectRequest);
			log.debug("Successfully deleted image: {}", objectKey);
		} catch (S3Exception e) {
			log.debug("Failed to deleted image: {}", objectKey);
		}
	}

	public void deleteImages(List<String> objectKeys) {
		for (String objectKey : objectKeys) {
			deleteImage(objectKey);
		}
	}
}
