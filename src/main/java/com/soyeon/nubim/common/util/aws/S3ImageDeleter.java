package com.soyeon.nubim.common.util.aws;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageDeleter {

	private final S3Client s3Client;
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	public void deleteImages(List<String> objectKeys) {
		try {
			List<ObjectIdentifier> keys = objectKeys.stream()
				.map(key -> ObjectIdentifier.builder().key(key).build())
				.toList();

			DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
				.bucket(bucketName)
				.delete(Delete.builder().objects(keys).build())
				.build();
			DeleteObjectsResponse deleteObjectsResponse = s3Client.deleteObjects(deleteObjectsRequest);

			List<DeletedObject> deletedObjects = deleteObjectsResponse.deleted();
			deletedObjects.forEach(object -> log.debug("Successfully deleted image: {}", object.key()));

			List<S3Error> errors = deleteObjectsResponse.errors();
			errors.forEach(error -> log.debug("Failed to delete image: {}, message: {}", error.key(), error.message()));
		} catch (S3Exception e) {
			log.debug("Failed to delete images: {}", objectKeys);
		}
	}
}
