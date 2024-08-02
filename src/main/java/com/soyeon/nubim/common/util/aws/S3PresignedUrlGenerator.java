package com.soyeon.nubim.common.util.aws;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3PresignedUrlGenerator {

	private static final int PRESIGNED_URL_DURATION_TIME = 10;
	private final S3Presigner s3Presigner;
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	/**
	 * S3 에 객체를 업로드하기 위한 presigned url을 contentTypes의 크기만큼 생성하여 반환한다
	 * 업로드할 경로는 자동으로 생성된다
	 * @param contentTypes 업로드할 객체의 MIME 타입을 담은 List
	 * @return S3에 업로드할 수 있는 presigned url들을 담은 List
	 */
	public List<String> generatePresignedUrls(List<String> contentTypes) {
		List<String> presignedUrls = new ArrayList<>(contentTypes.size());

		String uploadDirectory = getUploadDirectory();

		for (String contentType : contentTypes) {
			String s3UploadPath = uploadDirectory + getFileName();

			String presignedUrl = preparePresignedUploadUrl(contentType, s3UploadPath);
			presignedUrls.add(presignedUrl);
		}
		return presignedUrls;
	}

	/**
	 * S3 에 객체를 업로드하기 위한 presigned url을 contentTypes의 크기만큼 생성하여 반환한다.
	 * 업로드할 경로는 사용자가 직접 지정해야 한다
	 * @param contentTypes 업로드할 객체의 MIME 타입을 담은 List
	 * @param uploadPath 업로드할 경로를 사용자가 직접 지정한다
	 * @return S3에 업로드할 수 있는 presigned url들을 담은 List
	 */
	public List<String> generatePresignedUrls(List<String> contentTypes, String uploadPath) {
		List<String> presignedUrls = new ArrayList<>(contentTypes.size());

		for (String contentType : contentTypes) {
			String s3UploadPath = uploadPath + getFileName();

			String presignedUrl = preparePresignedUploadUrl(contentType, s3UploadPath);
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
	 * 현재 날짜와 UUID 16자리를 이용하여 충돌 가능성이 낮은 경로를 지정한다
	 * @return 디렉토리 경로 반환
	 */
	private static String getUploadDirectory() {
		return LocalDate.now() + "/"
			+ UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16) + "/";
	}

	/**
	 * 사진 업로드 경로 생성
	 * UUID 10자리를 이용해 충돌 가능성이 낮은 경로를 생성한다
	 * @return 사진 업로드 경로 반환
	 */
	private static String getFileName() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
	}
}
