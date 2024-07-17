package com.soyeon.nubim.common.util.aws;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3PresignedUrlGenerator {

	public static final int PRESIGNED_URL_DURATION_TIME = 10;
	private final S3Presigner s3Presigner;
	private final String bucketName;

	public S3PresignedUrlGenerator(
		@Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
		@Value("${spring.cloud.aws.credentials.secret-key}") String secretKey,
		@Value("${spring.cloud.aws.region.static}") String region,
		@Value("${spring.cloud.aws.s3.bucket}") String bucketName) {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		this.s3Presigner = S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
			.build();
		this.bucketName = bucketName;
	}

	/**
	 * S3 에 객체를 업로드하기 위한 presigned url을 contentTypes의 크기만큼 생성하여 반환한다
	 * @param contentTypes 업로드할 객체의 MIME 타입을 담은 List
	 * @return S3에 업로드할 수 있는 presigned url들을 담은 List
	 */
	public List<String> generatePresignedUrls(List<String> contentTypes) {
		List<String> presignedUrls = new ArrayList<>(contentTypes.size());

		String uploadDirectory = getUploadDirectory();

		for (int i = 0; i < contentTypes.size(); i++) {
			String s3UploadPath = uploadDirectory + getFileName(i);

			String presignedUrl = preparePresignedUploadUrl(contentTypes.get(i), s3UploadPath);
			presignedUrls.add(presignedUrl);
		}
		return presignedUrls;
	}

	/**
	 * S3 에 객체를 업로드하기 위한 presigned url 생성
	 * @param contentType 업로드할 객체의 MIME 타입
	 * @param s3UploadPath S3 버킷 내 업로드되는 경로와 파일명
	 * @return S3에 업로드할 수 있는 presigned url
	 */
	private String preparePresignedUploadUrl(String contentType, String s3UploadPath) {
		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(s3UploadPath)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(PRESIGNED_URL_DURATION_TIME)) //presigned url 유효 기간
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
		return presignedRequest.url().toString();
	}

	/**
	 * 사진 업로드를 위한 고유한 디렉토리 경로 생성
	 * 현재 날짜와 UUID 8자리를 이용하여 충돌 가능성이 낮은 경로를 지정한다
	 * @return 디렉토리 경로 반환
	 */
	private static String getUploadDirectory() {
		return LocalDate.now() + "/" + UUID.randomUUID().toString().substring(0, 8) + "/";
	}

	private static String getFileName(int number) {
		return "img" + String.format("%02d", number);
	}
}
