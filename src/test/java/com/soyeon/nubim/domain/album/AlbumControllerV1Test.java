package com.soyeon.nubim.domain.album;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumUpdateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.LocationReadResponseDto;
import com.soyeon.nubim.domain.album.dto.LocationUpdateRequestDto;
import com.soyeon.nubim.domain.album.exception.AlbumNotFoundException;
import com.soyeon.nubim.domain.user.exception.UserNotFoundException;
import com.soyeon.nubim.security.SecurityConfig;
import com.soyeon.nubim.security.blacklist_accesstoken.AccessTokenBlacklistRepository;
import com.soyeon.nubim.security.blacklist_accesstoken.AccessTokenBlacklistService;
import com.soyeon.nubim.security.jwt.JwtAuthenticationFilter;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.oauth.OAuthLoginCommons;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

@WebMvcTest(controllers = AlbumControllerV1.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
@TestPropertySource(properties = {
	"jwt.secret=testsecretkeytestsecretkeytestsecretkeytestsecretkey",
	"logging.level.com.soyeon.nubim=DEBUG",
})
class AlbumControllerV1Test {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private AlbumService albumService;
	@MockBean
	private AlbumPhotoUploadUrlService albumPhotoUploadUrlService;
	@MockBean
	private RefreshTokenService refreshTokenService;
	@MockBean
	private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	@MockBean
	private OAuthLoginCommons oAuthLoginCommons;
	@MockBean
	private AccessTokenBlacklistService accessTokenBlacklistService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	private AlbumCreateRequestDto createRequestDto;
	private AlbumCreateResponseDto createResponseDto;
	private AlbumReadResponseDto readResponseDto;
	private AlbumUpdateRequestDto updateRequestDto;

	private String accessToken;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.addFilter(jwtAuthenticationFilter)
			.alwaysDo(print())
			.build();

		accessToken = jwtTokenProvider.generateAccessToken("1L", "testUser@email.com", "USER");

		Long albumId = 1L;
		Long userId = 1L;
		String description = "create album test";
		Map<Integer, String> photoUrls = Map.of(1, "https://test01.jpg",
			2, "https://test02.jpg",
			3, "https://test03.jpg",
			4, "https://test04.jpg");

		List<LocationCreateRequestDto> locationsRequest = List.of(
			LocationCreateRequestDto.builder()
				.latitude(37.5665)
				.longitude(126.9780)
				.visitedAt(LocalDateTime.now())
				.placeName("서울")
				.build(),
			LocationCreateRequestDto.builder()
				.latitude(35.1796)
				.longitude(129.0756)
				.visitedAt(LocalDateTime.now().minusDays(1))
				.placeName("서울")
				.build(),
			LocationCreateRequestDto.builder()
				.latitude(33.4996)
				.longitude(126.5312)
				.visitedAt(LocalDateTime.now().minusDays(2))
				.placeName("서울")
				.build()
		);

		List<LocationCreateResponseDto> locationsResponse = List.of(
			LocationCreateResponseDto.builder()
				.locationId(1L)
				.albumId(1L)
				.latitude(37.5665)
				.longitude(126.9780)
				.visitedAt(LocalDateTime.now())
				.placeName("서울")
				.build(),
			LocationCreateResponseDto.builder()
				.locationId(2L)
				.albumId(1L)
				.latitude(35.1796)
				.longitude(129.0756)
				.visitedAt(LocalDateTime.now().minusDays(1))
				.placeName("서울")
				.build(),
			LocationCreateResponseDto.builder()
				.locationId(3L)
				.albumId(1L)
				.latitude(33.4996)
				.longitude(126.5312)
				.visitedAt(LocalDateTime.now().minusDays(2))
				.placeName("서울")
				.build()
		);

		List<List<Double>> path = List.of(
			List.of(0.0, 0.0),
			List.of(1.0, 1.0),
			List.of(2.0, 2.0),
			List.of(3.0, 3.0),
			List.of(4.0, 4.0),
			List.of(5.0, 5.0),
			List.of(6.0, 6.0),
			List.of(7.0, 7.0),
			List.of(8.0, 8.0),
			List.of(9.0, 9.0)
		);

		createRequestDto = AlbumCreateRequestDto.builder()
			.description(description)
			.photoUrls(photoUrls)
			.locations(locationsRequest)
			.path(path)
			.build();

		createResponseDto = AlbumCreateResponseDto.builder()
			.albumId(albumId)
			.userId(userId)
			.description(description)
			.photoUrls(photoUrls)
			.locations(locationsResponse)
			.path(path)
			.build();

		readResponseDto = AlbumReadResponseDto.builder()
			.albumId(albumId)
			.userId(1L)
			.description("read album test")
			.photoUrls(Map.of(1, "https://test01.jpg", 2, "https://test02.jpg"))
			.locations(List.of(
				LocationReadResponseDto.builder()
					.locationId(1L)
					.albumId(albumId)
					.latitude(37.5665)
					.longitude(126.9780)
					.visitedAt(LocalDateTime.now())
					.placeName("서울")
					.build()
			))
			.path(path)
			.build();

		updateRequestDto = AlbumUpdateRequestDto.builder()
			.description("update album test")
			.photoUrls(Map.of(1, "https://change01.jpg", 2, "https://change01.jpg"))
			.locations(List.of(
				LocationUpdateRequestDto.builder()
					.latitude(1.0)
					.longitude(2.0)
					.visitedAt(LocalDateTime.now())
					.placeName("경기도")
					.build())
			)
			.path(path)
			.build();
	}

	@Test
	@DisplayName("앨범 생성 - 성공 case")
	void createAlbum() throws Exception {
		when(albumService.createAlbum(any(AlbumCreateRequestDto.class))).thenReturn(createResponseDto);

		MvcResult result = mockMvc.perform(post("/v1/albums")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.albumId").value(createResponseDto.getAlbumId()))
			.andExpect(jsonPath("$.description").value(createResponseDto.getDescription()))
			.andExpect(jsonPath("$.photoUrls").isNotEmpty())
			.andExpect(jsonPath("$.locations").isNotEmpty())
			.andExpect(jsonPath("$.path").isNotEmpty())
			.andReturn();

		String content = result.getResponse().getContentAsString();
		AlbumCreateResponseDto actualCreateResponseDto = objectMapper.readValue(content, AlbumCreateResponseDto.class);

		assertEquals(createResponseDto.getPhotoUrls().size(), actualCreateResponseDto.getPhotoUrls().size());
		for (Map.Entry<Integer, String> photoUrl : createResponseDto.getPhotoUrls().entrySet()) {
			assertEquals(photoUrl.getValue(), actualCreateResponseDto.getPhotoUrls().get(photoUrl.getKey()));
		}

		assertEquals(createResponseDto.getLocations().size(), actualCreateResponseDto.getLocations().size());
		for (int i = 0; i < createResponseDto.getLocations().size(); i++) {
			LocationCreateResponseDto expectedLocation = createResponseDto.getLocations().get(i);
			LocationCreateResponseDto actualLocation = actualCreateResponseDto.getLocations().get(i);

			assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude());
			assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude());
			assertEquals(expectedLocation.getVisitedAt(), actualLocation.getVisitedAt());
			assertEquals(expectedLocation.getPlaceName(), actualLocation.getPlaceName());
		}
	}

	@Test
	@DisplayName("앨범 생성 - 실패 case : access token 이 없는 경우")
	void createAlbum_Unauthorized() throws Exception {
		when(albumService.createAlbum(any(AlbumCreateRequestDto.class))).thenReturn(createResponseDto);

		mockMvc.perform(post("/v1/albums")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequestDto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("앨범 검색 - 성공 case")
	void getAlbumWithLocations() throws Exception {
		Long albumId = 1L;

		when(albumService.findByIdWithLocations(albumId)).thenReturn(readResponseDto);

		mockMvc.perform(get("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(readResponseDto.getUserId()))
			.andExpect(jsonPath("$.albumId").value(albumId))
			.andExpect(jsonPath("$.description").value(readResponseDto.getDescription()))
			.andExpect(jsonPath("$.photoUrls").isNotEmpty())
			.andExpect(jsonPath("$.locations").isNotEmpty())
			.andExpect(
				jsonPath("$.locations[0].placeName").value(readResponseDto.getLocations().get(0).getPlaceName()))
			.andExpect(jsonPath("$.path").isNotEmpty());

		verify(albumService).findByIdWithLocations(albumId);
	}

	@Test
	@DisplayName("앨범 검색 - 실패 case : 존재 하지 않는 앨범 ID 검색")
	void getAlbumWithLocations_NotFound() throws Exception {
		Long albumId = 999L;

		when(albumService.findByIdWithLocations(albumId)).thenThrow(new AlbumNotFoundException(albumId));

		mockMvc.perform(get("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound());

		verify(albumService).findByIdWithLocations(albumId);
	}

	@Test
	@DisplayName("닉네임으로 앨범 검색 - 성공 case")
	void getUserAlbums() throws Exception {
		String nickname = "testUser";

		when(albumService.findAlbumsByUserNickname(nickname)).thenReturn(List.of(readResponseDto));

		MvcResult result = mockMvc.perform(get("/v1/albums/user/{nickname}", nickname)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn();

		String content = result.getResponse().getContentAsString();
		List<AlbumReadResponseDto> actualAlbums
			= objectMapper.readValue(content, new TypeReference<>() {
		});

		assertEquals(1, actualAlbums.size());
		assertEquals(readResponseDto.getAlbumId(), actualAlbums.get(0).getAlbumId());
		assertEquals(readResponseDto.getDescription(), actualAlbums.get(0).getDescription());
		assertEquals(readResponseDto.getPhotoUrls().size(), actualAlbums.get(0).getPhotoUrls().size());
		for (Map.Entry<Integer, String> photoUrl : readResponseDto.getPhotoUrls().entrySet()) {
			assertEquals(photoUrl.getValue(), actualAlbums.get(0).getPhotoUrls().get(photoUrl.getKey()));
		}

		assertEquals(readResponseDto.getLocations().size(), actualAlbums.get(0).getLocations().size());
		for (int i = 0; i < readResponseDto.getLocations().size(); i++) {
			LocationReadResponseDto expectedLocation = readResponseDto.getLocations().get(i);
			LocationReadResponseDto actualLocation = actualAlbums.get(0).getLocations().get(i);
			assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude());
			assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude());
			assertEquals(expectedLocation.getVisitedAt(), actualLocation.getVisitedAt());
			assertEquals(expectedLocation.getPlaceName(), actualLocation.getPlaceName());
		}

		assertEquals(readResponseDto.getPath().size(), actualAlbums.get(0).getPath().size());
		for (int i = 0; i < readResponseDto.getPath().size(); i++) {
			List<Double> expectedPath = readResponseDto.getPath().get(i);
			List<Double> actualPath = actualAlbums.get(0).getPath().get(i);
			assertEquals(expectedPath.get(0), actualPath.get(0));
			assertEquals(expectedPath.get(1), actualPath.get(1));
		}
	}

	@Test
	@DisplayName("닉네임으로 앨범 검색 - 실패 case : 존재하지 않는 닉네임")
	void getUserAlbums_NotFound() throws Exception {
		String nickname = "testUser";

		when(albumService.findAlbumsByUserNickname(nickname)).thenThrow(
			new UserNotFoundException(nickname, "nickname"));

		mockMvc.perform(get("/v1/albums/user/{nickname}", nickname)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("앨범 업데이트 - 성공 case")
	void updateAlbum() throws Exception {
		Long albumId = 1L;

		List<LocationUpdateRequestDto> locationUpdateList = updateRequestDto.getLocations();
		List<LocationReadResponseDto> locationReadList = new ArrayList<>(locationUpdateList.size());
		for (int i = 0; i < locationUpdateList.size(); i++) {
			LocationUpdateRequestDto locationUpdateRequest = locationUpdateList.get(i);
			locationReadList.add(LocationReadResponseDto.builder()
				.locationId((long)(i + 1))
				.albumId(albumId)
				.longitude(locationUpdateRequest.getLongitude())
				.latitude(locationUpdateRequest.getLatitude())
				.visitedAt(locationUpdateRequest.getVisitedAt())
				.placeName(locationUpdateRequest.getPlaceName())
				.build());
		}

		AlbumReadResponseDto updateResponseDto = AlbumReadResponseDto.builder()
			.albumId(readResponseDto.getAlbumId())
			.userId(readResponseDto.getUserId())
			.description(updateRequestDto.getDescription())
			.photoUrls(updateRequestDto.getPhotoUrls())
			.locations(locationReadList)
			.path(readResponseDto.getPath())
			.build();

		when(albumService.updateAlbum(any(Long.class), any(AlbumUpdateRequestDto.class))).thenReturn(updateResponseDto);

		MvcResult result = mockMvc.perform(put("/v1/albums/{albumId}", albumId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.albumId").value(albumId))
			.andExpect(jsonPath("$.userId").value(readResponseDto.getUserId()))
			.andExpect(jsonPath("$.description").value(updateRequestDto.getDescription()))
			.andExpect(jsonPath("$.photoUrls").isNotEmpty())
			.andExpect(jsonPath("$.locations").isNotEmpty())
			.andExpect(jsonPath("$.path").isNotEmpty())
			.andReturn();

		String content = result.getResponse().getContentAsString();
		AlbumReadResponseDto actualReadResponseDto = objectMapper.readValue(content, AlbumReadResponseDto.class);

		for (Map.Entry<Integer, String> photoUrl : updateRequestDto.getPhotoUrls().entrySet()) {
			assertEquals(photoUrl.getValue(), actualReadResponseDto.getPhotoUrls().get(photoUrl.getKey()));
		}

		assertEquals(updateRequestDto.getLocations().size(), actualReadResponseDto.getLocations().size());
		for (int i = 0; i < updateRequestDto.getLocations().size(); i++) {
			LocationUpdateRequestDto expectedLocation = updateRequestDto.getLocations().get(i);
			LocationReadResponseDto actualLocation = actualReadResponseDto.getLocations().get(i);

			assertEquals(expectedLocation.getLatitude(), actualLocation.getLatitude());
			assertEquals(expectedLocation.getLongitude(), actualLocation.getLongitude());
			assertEquals(expectedLocation.getVisitedAt(), actualLocation.getVisitedAt());
			assertEquals(expectedLocation.getPlaceName(), actualLocation.getPlaceName());
		}
	}

}