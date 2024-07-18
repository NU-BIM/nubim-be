package com.soyeon.nubim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.region(DefaultAwsRegionProviderChain.builder().build().getRegion())
			.credentialsProvider(DefaultCredentialsProvider.create())
			.build();
	}

}

