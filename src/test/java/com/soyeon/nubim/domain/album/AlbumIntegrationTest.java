package com.soyeon.nubim.domain.album;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumUpdateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationUpdateRequestDto;
import com.soyeon.nubim.domain.album.dto.PhotoInitialUploadRequestDto;
import com.soyeon.nubim.domain.album.dto.PhotoUpdateUploadRequestDto;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserRepository;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class AlbumIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${spring.cloud.aws.region.static}")
	private String region;
	@Value("${CDN_URL}")
	private String cdnUrl;

	private String s3BucketUrlPrefix;

	private User user;
	private String accessToken;
	private String anotherAccessToken;
	private AlbumCreateRequestDto createRequestDto;
	private AlbumUpdateRequestDto updateRequestDto;
	private final Long nonExistentAlbumId = 9999999999L;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testUser")
			.nickname("testNickname")
			.email("testEmail@email.com")
			.role(Role.USER)
			.build();
		userRepository.save(user);

		User anotherUser = User.builder()
			.username("anotherUser")
			.nickname("anotherNickname")
			.email("another@email.com")
			.role(Role.USER)
			.build();
		userRepository.save(anotherUser);

		accessToken = jwtTokenProvider.generateAccessToken(user.getUserId().toString(), user.getEmail(),
			user.getRole().name());
		anotherAccessToken = jwtTokenProvider.generateAccessToken(anotherUser.getUserId().toString(),
			anotherUser.getEmail(), anotherUser.getRole().name());

		s3BucketUrlPrefix = String.format("https://%s.s3.%s.amazonaws.com", bucketName, region);

		LocationCreateRequestDto location1 = LocationCreateRequestDto.builder()
			.latitude(1.0)
			.longitude(2.0)
			.visitedAt(LocalDateTime.now())
			.placeName("서울")
			.build();

		LocationCreateRequestDto location2 = LocationCreateRequestDto.builder()
			.latitude(5.0)
			.longitude(6.0)
			.visitedAt(LocalDateTime.now())
			.placeName("제주")
			.build();

		createRequestDto = AlbumCreateRequestDto.builder()
			.description("create test album")
			.photoUrls(Map.of(1, s3BucketUrlPrefix + "/test01.jpg", 2, s3BucketUrlPrefix + "/test02.jpg"))
			.locations(List.of(location1, location2))
			.build();

		LocationUpdateRequestDto newLocation = LocationUpdateRequestDto.builder()
			.latitude(1.0)
			.longitude(2.0)
			.visitedAt(LocalDateTime.now())
			.placeName("new place")
			.build();

		updateRequestDto = AlbumUpdateRequestDto.builder()
			.description("updated description")
			.photoUrls(Map.of(1, s3BucketUrlPrefix + "/updated01.jpg", 2, cdnUrl + "/updated02.jpg"))
			.locations(List.of(newLocation))
			.build();
	}

	@Test
	@DisplayName("앨범 생성 테스트 - 성공 case")
	void createAlbum() throws Exception {
		mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.albumId").exists())
			.andExpect(jsonPath("$.description").value("create test album"))
			.andExpect(jsonPath("$.photoUrls").exists())
			.andExpect(jsonPath("$.locations").exists())
			.andExpect(jsonPath("$.userId").value(user.getUserId()));
	}

	@Test
	@DisplayName("앨범 조회 테스트 - 성공 case")
	void getAlbumWithLocations() throws Exception {
		MvcResult result = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		AlbumCreateResponseDto createResponse =
			objectMapper.readValue(result.getResponse().getContentAsString(), AlbumCreateResponseDto.class);
		Long albumId = createResponse.getAlbumId();

		mockMvc.perform(get("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.albumId").value(albumId))
			.andExpect(jsonPath("$.description").value("create test album"))
			.andExpect(jsonPath("$.photoUrls").exists())
			.andExpect(jsonPath("$.photoUrls.*", everyItem(startsWith(cdnUrl))))
			.andExpect(jsonPath("$.locations").exists())
			.andExpect(jsonPath("$.userId").value(user.getUserId()));
	}

	@Test
	@DisplayName("사용자의 모든 앨범 조회 테스트 - 성공 case")
	void getUserAlbums() throws Exception {
		mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk());

		mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/v1/albums/user/{nickname}", user.getNickname())
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].description").value("create test album"))
			.andExpect(jsonPath("$[1].description").value("create test album"));
	}

	@Test
	@DisplayName("내 앨범 조회 테스트 - 성공 case")
	void getMyAlbums() throws Exception {
		mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/v1/albums/my-albums")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].description").value("create test album"));
	}

	@Test
	@DisplayName("앨범 업데이트 테스트 - 성공 case")
	void updateAlbum() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		AlbumCreateResponseDto createResponse =
			objectMapper.readValue(createResult.getResponse().getContentAsString(), AlbumCreateResponseDto.class);
		Long albumId = createResponse.getAlbumId();

		mockMvc.perform(put("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.albumId").value(albumId))
			.andExpect(jsonPath("$.description").value("updated description"))
			.andExpect(jsonPath("$.photoUrls").exists())
			.andExpect(jsonPath("$.photoUrls.*", everyItem(startsWith(cdnUrl))))
			.andExpect(jsonPath("$.locations").value(hasSize(1)));
	}

	@Test
	@DisplayName("앨범 업데이트 테스트 - 실패 case : 존재하지 않는 앨범")
	void updateNonExistentAlbum() throws Exception {
		mockMvc.perform(put("/v1/albums/{albumId}", nonExistentAlbumId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequestDto)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(containsString("Album not found")));
	}

	@Test
	@DisplayName("앨범 업데이트 테스트 - 실패 case : 권한 없음")
	void updateAlbumWithoutPermission() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		AlbumCreateResponseDto createResponse =
			objectMapper.readValue(createResult.getResponse().getContentAsString(), AlbumCreateResponseDto.class);
		Long albumId = createResponse.getAlbumId();

		mockMvc.perform(put("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + anotherAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequestDto)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("앨범 삭제 테스트 - 성공 case")
	void deleteAlbum() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		AlbumCreateResponseDto createResponse =
			objectMapper.readValue(createResult.getResponse().getContentAsString(), AlbumCreateResponseDto.class);
		Long albumId = createResponse.getAlbumId();

		mockMvc.perform(delete("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());

		mockMvc.perform(get("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("앨범 삭제 테스트 - 실패 case : 권한 없음")
	void deleteAlbumWithoutPermission() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andReturn();

		AlbumCreateResponseDto createResponse =
			objectMapper.readValue(createResult.getResponse().getContentAsString(), AlbumCreateResponseDto.class);
		Long albumId = createResponse.getAlbumId();

		mockMvc.perform(delete("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + anotherAccessToken))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("앨범 조회 테스트 - 실패 case : 존재하지 않는 앨범")
	void getAlbumWithNonExistentId() throws Exception {
		mockMvc.perform(get("/v1/albums/{albumId}", nonExistentAlbumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(containsString("Album not found")));
	}

	@Test
	@DisplayName("초기 사진 업로드 URL 생성 테스트 - 성공 case")
	void getInitialPhotoUploadUrls() throws Exception {
		PhotoInitialUploadRequestDto requestDto = new PhotoInitialUploadRequestDto();
		requestDto.setContentTypes(List.of("image/png", "image/png"));

		mockMvc.perform(post("/v1/albums/photos/upload-urls")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0]").isString())
			.andExpect(jsonPath("$[1]").isString())
			.andExpect(jsonPath("$.*", everyItem(startsWith(s3BucketUrlPrefix))));
	}

	@Test
	@DisplayName("초기 사진 업로드 URL 생성 테스트 - 실패 case : 잘못된 컨텐츠 타입")
	void getInitialPhotoUploadUrlsWithInvalidContentType() throws Exception {
		PhotoInitialUploadRequestDto requestDto = new PhotoInitialUploadRequestDto();
		requestDto.setContentTypes(List.of("invalid/type", "image/png"));

		mockMvc.perform(post("/v1/albums/photos/upload-urls")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(jsonPath("$.message").value(containsString("Invalid content type")));
	}

	@Test
	@DisplayName("사진 업데이트 업로드 URL 생성 테스트 - 성공 case")
	void getUpdatePhotoUploadUrls() throws Exception {
		PhotoUpdateUploadRequestDto requestDto = new PhotoUpdateUploadRequestDto();
		requestDto.setContentTypes(List.of("image/png", "image/png"));
		requestDto.setUploadPath("custom/path");

		mockMvc.perform(post("/v1/albums/photos/update-upload-urls")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0]").isString())
			.andExpect(jsonPath("$[1]").isString())
			.andExpect(jsonPath("$.*", everyItem(startsWith(s3BucketUrlPrefix))));
	}

	@Test
	@DisplayName("사진 업데이트 업로드 URL 생성 테스트 - 실패 case: 잘못된 컨텐츠 타입")
	void getUpdatePhotoUploadUrlsWithInvalidContentType() throws Exception {
		PhotoUpdateUploadRequestDto requestDto = new PhotoUpdateUploadRequestDto();
		requestDto.setContentTypes(List.of("invalid/type", "image/png"));
		requestDto.setUploadPath("custom/path");

		mockMvc.perform(post("/v1/albums/photos/update-upload-urls")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(jsonPath("$.message").value(containsString("Invalid content type")));
	}
}
