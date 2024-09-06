package com.soyeon.nubim.common.util.aws;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class S3AndCdnUrlConverterTest {

	private S3AndCdnUrlConverter s3AndCdnUrlConverter;

	@BeforeEach
	void setup() {
		String bucketName = "my-s3-bucket";
		String region = "ap-northeast-2";
		String cdnUrl = "https://cdn.example.com";

		s3AndCdnUrlConverter = new S3AndCdnUrlConverter(bucketName, region, cdnUrl);
	}

	@Test
	void testConvertS3UrlToPath_ValidS3Url() {
		// given
		String s3Url = "https://my-s3-bucket.s3.ap-northeast-2.amazonaws.com/path/example";
		String expectedPath = "/path/example";

		// when
		String result = s3AndCdnUrlConverter.convertS3UrlToPath(s3Url);

		// then
		assertEquals(expectedPath, result);
	}

	@Test
	void testConvertS3UrlToPath_InvalidS3Url() {
		// given
		String invalidS3Url = "https://other-bucket.s3.ap-northeast-2.amazonaws.com/path/example";

		// when & then
		assertThrows(InvalidS3UrlException.class, () -> {
			s3AndCdnUrlConverter.convertS3UrlToPath(invalidS3Url);
		});
	}

	@Test
	void testConvertPathToCdnUrl() {
		// given
		String path = "/path/example";
		String expectedCdnUrl = "https://cdn.example.com/path/example";

		// when
		String result = s3AndCdnUrlConverter.convertPathToCdnUrl(path);

		// then
		assertEquals(expectedCdnUrl, result);
	}
}