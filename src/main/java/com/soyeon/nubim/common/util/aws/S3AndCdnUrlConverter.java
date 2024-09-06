package com.soyeon.nubim.common.util.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class S3AndCdnUrlConverter {
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Value("${CDN_URL}")
	private String cdnUrl;

	public String convertS3UrlToPath(String s3Url) {
		String s3BucketUrlPrefix = String.format("https://%s.s3.%s.amazonaws.com", bucketName, region);

		// S3 URL이 유효한지 확인
		if (s3Url.startsWith(s3BucketUrlPrefix)) {
			return s3Url.replace(s3BucketUrlPrefix, "");
		} else {
			throw new InvalidS3UrlException();
		}
	}

	public String convertPathToCdnUrl(String path) {
		return cdnUrl + path;
	}
}
