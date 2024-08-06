package com.soyeon.nubim.domain.userfollow;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.common.enums.Gender;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserRepository;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
class UserFollowControllerV1Test {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserFollowRepository userFollowRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private EntityManager entityManager;

	private User testUser1;
	private User testUser2;

	@BeforeEach
	void setup() {
		// 사용자 더미 데이터 생성 및 저장
		testUser1 = User.builder()
			.username("user1")
			.nickname("User One")
			.email("user1@example.com")
			.profileImageUrl("https://example.com/user1.jpg")
			.profileIntroduction("Hello, I'm user one!")
			.phoneNumber("123-456-7890")
			.birthDate(LocalDateTime.of(1990, 1, 1, 0, 0))
			.gender(Gender.MALE)
			.role(Role.USER)
			.build();
		userRepository.save(testUser1);

		testUser2 = User.builder()
			.username("user2")
			.nickname("User Two")
			.email("user2@example.com")
			.profileImageUrl("https://example.com/user2.jpg")
			.profileIntroduction("Hello, I'm user two!")
			.phoneNumber("123-456-7891")
			.birthDate(LocalDateTime.of(1991, 1, 1, 0, 0))
			.gender(Gender.FEMALE)
			.role(Role.USER)
			.build();
		userRepository.save(testUser2);

		// jwt 인증
		String accessToken = jwtTokenProvider.generateAccessToken(
			testUser1.getUserId().toString(),
			testUser1.getEmail(),
			testUser1.getRole().name());

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.defaultRequest(get("/")
				.header("Authorization", "Bearer " + accessToken))
			.build();
	}

	/*
	-----------------------------------------------------------------------------------------------
	유저 팔로우 API
	-----------------------------------------------------------------------------------------------
	 */

	/**
	 * 한 유저가 다른 유저를 정상적으로 팔로우하는 테스트
	 * 201 Created
	 */
	@DisplayName("정상 유저 팔로우 테스트")
	@Test
	void followUser_Success() throws Exception {
		// given
		String requestPath = "/v1/follows/" + testUser2.getNickname();

		// when
		ResultActions resultActions = mockMvc.perform(
			post(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isCreated());
	}

	/**
	 * 자기 자신을 팔로우 시 에러 테스트
	 * 400 Bad Request
	 */
	@DisplayName("자기 자신 팔로우 에러 테스트")
	@Test
	void followUser_FollowMyself_Error() throws Exception {
		// given
		String requestPath = "/v1/follows/" + testUser1.getNickname();

		// when
		ResultActions resultActions = mockMvc.perform(
			post(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isBadRequest());
	}

	/**
	 * 존재하지 않는 유저 팔로우 에러 테스트
	 * 404 Not Found
	 */
	@DisplayName("존재하지 않는 유저 팔로우 에러 테스트")
	@Test
	void followUser_NotFoundUser_Error() throws Exception {
		// given
		Long notFoundUserId = Long.MAX_VALUE;
		String requestPath = "/v1/follows/" + notFoundUserId;

		// when
		ResultActions resultActions = mockMvc.perform(
			post(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isNotFound());
	}

	/**
	 * 이미 팔로우한 유저 팔로우 시도 에러 테스트
	 * 400 Bad Request
	 */
	@DisplayName("이미 팔로우한 유저 팔로우 시도 에러 테스트")
	@Test
	void followUser_FollowRepetition_Error() throws Exception {
		// given
		String requestPath = "/v1/follows/" + testUser2.getNickname();
		UserFollow userFollow = UserFollow.builder()
			.follower(testUser1)
			.followee(testUser2)
			.build();
		userFollowRepository.save(userFollow);
		entityManager.flush();
		entityManager.clear();

		// when
		ResultActions resultActions = mockMvc.perform(
			post(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("You are already following " + testUser2.getNickname()));
	}

	/*
	-----------------------------------------------------------------------------------------------
	유저 언팔로우 API
	-----------------------------------------------------------------------------------------------
	 */

	/**
	 * 팔로우 정상 취소 테스트
	 * 200 OK
	 */
	@DisplayName("팔로우 정상 취소 테스트")
	@Test
	void unfollowUser_Success() throws Exception {
		// given
		String requestPath = "/v1/follows/" + testUser2.getNickname();
		UserFollow userFollow = UserFollow.builder()
			.follower(testUser1)
			.followee(testUser2)
			.build();
		userFollowRepository.save(userFollow);
		entityManager.flush();
		entityManager.clear();

		// when
		ResultActions resultActions = mockMvc.perform(
			delete(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Successfully unfollowed"));

	}

	/**
	 * 존재하지 않는 userId 취소 테스트
	 * 404 Not Found
	 */
	@DisplayName("존재하지 않는 유저 팔로우 취소 에러 테스트")
	@Test
	void unfollowUser_InvalidUserId_Error() throws Exception {
		// given
		Long notFoundUserId = Long.MAX_VALUE;
		String requestPath = "/v1/follows/" + notFoundUserId;

		// when
		ResultActions resultActions = mockMvc.perform(
			delete(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isNotFound());
	}

	/**
	 * 팔로우 하지 않은 유저 팔로우 취소 테스트
	 * 400 Bad Request
	 */
	@DisplayName("팔로우 하지 않은 유저 팔로우 취소 테스트")
	@Test
	void unfollowUser_UserNotFollowed_Error() throws Exception {
		// given
		String requestPath = "/v1/follows/" + testUser2.getNickname();

		// when
		ResultActions resultActions = mockMvc.perform(
			delete(requestPath)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("You are NOT following " + testUser2.getNickname()));
	}


	/*
	-----------------------------------------------------------------------------------------------
	팔로워 조회 API
	-----------------------------------------------------------------------------------------------
	 */

	@DisplayName("팔로워가 한명일 때 조회 테스트")
	@Test
	void getFollowers_OneUser_Success() throws Exception {
		// given
		UserFollow dummyUserFollow = UserFollow.builder()
			.follower(testUser2)
			.followee(testUser1)
			.build();
		userFollowRepository.save(dummyUserFollow);
		entityManager.flush();
		entityManager.clear();

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].username").value(testUser2.getUsername()));
	}

	@DisplayName("팔로워가 없을 때 조회 테스트")
	@Test
	void getFollowers_NoUser_Success() throws Exception {
		// given

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(0)));
	}

	/**
	 * 10명의 테스트 팔로워 생성 (testUser1을 팔로우함)
	 * 3개의 페이지 사이즈로 2번째 페이지 조회
	 * 배열 뒤에서부터 조회하여 6,5,4 유저 반환(9,8,7 건너 뜀)
	 */
	@DisplayName("페이징된 여러 팔로워 시간 내림차순 결과 반환")
	@Test
	void getFollowers_PagedUsers_Success() throws Exception {
		// given
		List<User> testFollowers = generateTestFollowers(testUser1, 10);

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followers")
				.queryParam("page", "1")
				.queryParam("pageSize", "3")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(3)))
			.andExpect(jsonPath("$.content[0].username").value(testFollowers.get(6).getUsername()))
			.andExpect(jsonPath("$.content[1].username").value(testFollowers.get(5).getUsername()))
			.andExpect(jsonPath("$.content[2].username").value(testFollowers.get(4).getUsername()));
	}

	private List<User> generateTestFollowers(User followee, int length) {
		List<User> testFollowers = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User user = User.builder()
				.username("follower" + i)
				.nickname("follower" + i)
				.email("follower" + i + "@example.com")
				.profileImageUrl("https://example.com/follower" + i + ".jpg")
				.profileIntroduction("Hello, I'm follower " + i + "!")
				.phoneNumber("123-456-7890")
				.birthDate(LocalDateTime.of(1990, 1, 1, 0, 0))
				.gender(Gender.MALE)
				.role(Role.USER)
				.build();
			userRepository.save(user);
			UserFollow userFollow = UserFollow.builder()
				.follower(user)
				.followee(followee)
				.build();
			testFollowers.add(user);
			userFollowRepository.save(userFollow);
		}
		entityManager.flush();
		entityManager.clear();

		return testFollowers;
	}


	/*
	-----------------------------------------------------------------------------------------------
	팔로이 조회 API
	-----------------------------------------------------------------------------------------------
	 */

	@DisplayName("팔로이가 한명일 때 조회 테스트")
	@Test
	void getFollowees_OneUser_Success() throws Exception {
		// given
		UserFollow dummyUserFollow = UserFollow.builder()
			.follower(testUser1)
			.followee(testUser2)
			.build();
		userFollowRepository.save(dummyUserFollow);
		entityManager.flush();
		entityManager.clear();

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].username").value(testUser2.getUsername()));
	}

	@DisplayName("팔로이가 없을 때 조회 테스트")
	@Test
	void getFollowees_NoUser_Success() throws Exception {
		// given

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followees")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(0)));
	}

	/**
	 * 10명의 테스트 팔로이 생성 (testUser1가 팔로우함)
	 * 3개의 페이지 사이즈로 2번째 페이지 조회
	 * 배열 뒤에서부터 조회하여 6,5,4 유저 반환(9,8,7 건너 뜀)
	 */
	@DisplayName("페이징된 여러 팔로이 시간 내림차순 결과 반환")
	@Test
	void getFollowees_PagedUsers_Success() throws Exception {
		// given
		List<User> testFollowees = generateTestFollowees(testUser1, 10);

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/followees")
				.queryParam("page", "1")
				.queryParam("pageSize", "3")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(3)))
			.andExpect(jsonPath("$.content[0].username").value(testFollowees.get(6).getUsername()))
			.andExpect(jsonPath("$.content[1].username").value(testFollowees.get(5).getUsername()))
			.andExpect(jsonPath("$.content[2].username").value(testFollowees.get(4).getUsername()));
	}

	private List<User> generateTestFollowees(User follower, int length) {
		List<User> testFollowees = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			User user = User.builder()
				.username("followee" + i)
				.nickname("followee" + i)
				.email("followee" + i + "@example.com")
				.profileImageUrl("https://example.com/followee" + i + ".jpg")
				.profileIntroduction("Hello, I'm followee " + i + "!")
				.phoneNumber("123-456-7890")
				.birthDate(LocalDateTime.of(1990, 1, 1, 0, 0))
				.gender(Gender.MALE)
				.role(Role.USER)
				.build();
			userRepository.save(user);
			UserFollow userFollow = UserFollow.builder()
				.follower(follower)
				.followee(user)
				.build();
			testFollowees.add(user);
			userFollowRepository.save(userFollow);
		}
		entityManager.flush();
		entityManager.clear();

		return testFollowees;
	}
}