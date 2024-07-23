package com.soyeon.nubim.domain.post;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumRepository;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserRepository;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local") // TODO : 테스트 환경에서 돌릴 시 dev로 변경 필요
class PostControllerV1Test {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	private User testUser;
	private User testUser2;
	private Album testAlbum;
	private Long testUserId;
	private Long testAlbumId;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Mock
	private UserService userService;

	@Autowired
	private EntityManager entityManager;

	/**
	 * 게시글 생성에 필요한 더미 유저, 앨범 생성
	 */
	@BeforeEach
	void setup() {
		// 사용자 더미 데이터 생성 및 저장
		testUser = User.builder()
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
		userRepository.save(testUser);
		testUserId = testUser.getUserId();

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

		// 앨범 더미 데이터 생성 및 저장
		testAlbum = Album.builder()
			.user(testUser)
			.description("User One's Album")
			.photoUrls("[\"https://example.com/photo1.jpg\", \"https://example.com/photo2.jpg\"]")
			.build();
		albumRepository.save(testAlbum);
		testAlbumId = testAlbum.getAlbumId();

		String accessToken = jwtTokenProvider.generateAccessToken(testUser.getEmail(), testUser.getRole().name());

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.defaultRequest(get("/")
				.header("Authorization", "Bearer " + accessToken))
			.build();
	}

    /*
    게시글 생성
     */

	/**
	 * 정상 처리
	 * 201 Created
	 */
	@DisplayName("게시글 정상 생성 테스트")
	@Test
	void createPost_Success() throws Exception {
		//given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.albumId(testAlbumId)
			.postTitle("Test Title")
			.postContent("Test Content")
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/v1/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(postCreateRequestDto))
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId", notNullValue()))
			.andDo(print());
	}

	/**
	 * 앨범 없음 에러
	 * 404 Not Found
	 */
	@DisplayName("유효하지 않은 albumId로 게시글 생성 시 404 에러 테스트")
	@Test
	void createPost_AlbumNotFound_Error() throws Exception {
		//given
		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.albumId(-1L) // 존재하지 않는 albumId
			.postTitle("Test Title")
			.postContent("Test Content")
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/v1/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(postCreateRequestDto))
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isNotFound())
			.andDo(print());
	}

	String generateRandomString(int length) {
		Random random = new Random();
		// 사용할 문자들 정의 (알파벳 대소문자)
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(random.nextInt(characters.length())));
		}

		return sb.toString();
	}

	/**
	 * 게시글 제목 글자 수 제한 에러
	 * 400 Bad Request
	 */
	@DisplayName("게시글 제목 101자 작성 에러 테스트")
	@Test
	void createPost_Over101CharatersInTitle_Error() throws Exception {
		//given
		final String TEST_TITLE = generateRandomString(101);

		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.albumId(testAlbumId)
			.postTitle(TEST_TITLE)
			.postContent("Test Content")
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/v1/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(postCreateRequestDto))
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	/**
	 * 게시글 제목 글자 수 제한 에러
	 * 400 Bad Request
	 */
	@DisplayName("게시글 본문 2201자 작성 에러 테스트")
	@Test
	void createPost_Over2201CharatersInContent_Error() throws Exception {
		//given
		final String TEST_CONTENT = generateRandomString(2201);

		PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
			.albumId(testAlbumId)
			.postTitle("Test Title")
			.postContent(TEST_CONTENT)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/v1/posts")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(postCreateRequestDto))
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andDo(print());
	}



    /*
    게시글 조회
     */

	/**
	 * 게시글 상세 조회
	 * 200 OK
	 */
	@DisplayName("게시글 정상 상세 조회 테스트")
	@Test
	void getPostDetail_WithNullType_Success() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/{postId}", post.getPostId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postId").value(post.getPostId()))
			.andExpect(jsonPath("$.postTitle").value("Test Title"))
			.andExpect(jsonPath("$.postContent").value("Test Content"))
			.andDo(print());
	}

	/**
	 * 게시글 미리보기
	 * 200 OK
	 */
	@DisplayName("게시글 정상 미리보기 테스트")
	@Test
	void getPostDetail_WithSimpleType_Success() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/{postId}?type=simple", post.getPostId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postId").value(post.getPostId()))
			.andExpect(jsonPath("$.postTitle").value("Test Title"))
			.andExpect(jsonPath("$.postContent").value("Test Content"))
			.andExpect(jsonPath("$.numberOfComments").value(0))
			.andDo(print());
	}

	/**
	 * 비정상적인 type
	 * 400 Bad Request
	 */
	@DisplayName("유효하지 않은 'type' 파라미터로 조회 시 400 에러 테스트")
	@Test
	void getPostDetail_WithInvalidType_BadRequest() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/{postId}?type=invalid", post.getPostId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

    /*
    method : getPostsByUserId
    endpoint : /v1/posts/user/{userId}
    userId 기반 게시글 목록 조회
     */

	@DisplayName("게시글 2개 시간 내림차순 정상 조회 테스트")
	@Test
	void getPostsByUserId_Desc_Success() throws Exception {
		//given
		Post post1 = Post.builder()
			.postTitle("First Post")
			.postContent("First Post Content")
			.user(this.testUser)
			.album(this.testAlbum)
			.build();
		post1.setCreatedAt(LocalDateTime.now().minusDays(1));
		postRepository.save(post1);

		Post post2 = Post.builder()
			.postTitle("Second Post")
			.postContent("Second Post Content")
			.user(this.testUser)
			.album(this.testAlbum)
			.build();
		post2.setCreatedAt(LocalDateTime.now().minusDays(2));
		postRepository.save(post2);

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/user/" + this.testUser.getUserId()));

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].postId").value(post2.getPostId()))
			.andExpect(jsonPath("$.content[0].postTitle").value(post2.getPostTitle()))
			.andExpect(jsonPath("$.content[0].postContent").value(post2.getPostContent()))
			.andExpect(jsonPath("$.content[1].postId").value(post1.getPostId()))
			.andExpect(jsonPath("$.content[1].postTitle").value(post1.getPostTitle()))
			.andExpect(jsonPath("$.content[1].postContent").value(post1.getPostContent()));

	}

	@DisplayName("게시글 2개 시간 오름차순 정상 조회 테스트")
	@Test
	void getPostsByUserId_Asc_Success() throws Exception {
		//given
		Post post1 = Post.builder()
			.postTitle("First Post")
			.postContent("First Post Content")
			.user(this.testUser)
			.album(this.testAlbum)
			.build();
		post1.setCreatedAt(LocalDateTime.now().minusDays(1));
		postRepository.save(post1);

		Post post2 = Post.builder()
			.postTitle("Second Post")
			.postContent("Second Post Content")
			.user(this.testUser)
			.album(this.testAlbum)
			.build();
		post2.setCreatedAt(LocalDateTime.now().minusDays(2));
		postRepository.save(post2);

		//when
		ResultActions resultActions = mockMvc.perform(
			get("/v1/posts/user/" + this.testUser.getUserId())
				.param("sort", "asc")
		);

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].postId").value(post1.getPostId()))
			.andExpect(jsonPath("$.content[0].postTitle").value(post1.getPostTitle()))
			.andExpect(jsonPath("$.content[0].postContent").value(post1.getPostContent()))
			.andExpect(jsonPath("$.content[1].postId").value(post2.getPostId()))
			.andExpect(jsonPath("$.content[1].postTitle").value(post2.getPostTitle()))
			.andExpect(jsonPath("$.content[1].postContent").value(post2.getPostContent()));

	}

	@DisplayName("게시글이 없는 user 조회 시 빈 배열 조회 테스트")
	@Test
	void getPostsByUserId_NoPosts_ReturnsEmptyList() throws Exception {
		//given

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/user/" + this.testUser.getUserId()));

		//then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(0)));
	}

	@DisplayName("존재하지 않는 userId로 조회 시 404 에러 테스트")
	@Test
	void getPostsByUserId_InvalidUserId_ThrowsUserNotFoundException() throws Exception {
		//given

		//when
		ResultActions resultActions = mockMvc.perform(get("/v1/posts/user/" + "-1"));

		//then
		resultActions
			.andExpect(status().isNotFound());
	}


    /*
    게시글 삭제
     */

	/**
	 * 정상적인 게시글 삭제
	 * 200 OK
	 */
	@DisplayName("게시글 정상 삭제 테스트")
	@Test
	void deletePost_Success() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		entityManager.flush();
		entityManager.clear();

		//when
		ResultActions resultActions = mockMvc.perform(delete("/v1/posts/{postId}", post.getPostId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isOk())
			.andDo(print());
	}

	/**
	 * 존재하지 않는 Id로 삭제
	 * 404 Not Found
	 */
	@DisplayName("유효하지 않은 postId로 게시글 삭제 시 404 에러 테스트")
	@Test
	void deletePost_PostNotFound_Error() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		//when
		ResultActions resultActions = mockMvc.perform(delete("/v1/posts/{postId}", -1)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isNotFound())
			.andDo(print());
	}

	/**
	 * 소유하지 않은 post를 삭제
	 * 403 Forbidden
	 */
	@DisplayName("본인의 게시글이 아닌 다른 유저의 게시글을 삭제 시 403 Forbidden")
	@Test
	void deletePost_Forbidden_Error() throws Exception {
		//given
		Post post = Post.builder()
			.album(testAlbum)
			.user(testUser2)
			.postTitle("Test Title")
			.postContent("Test Content")
			.comments(List.of())
			.build();
		postRepository.save(post);

		entityManager.flush();
		entityManager.clear();

		//when
		ResultActions resultActions = mockMvc.perform(delete("/v1/posts/{postId}", post.getPostId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
		);

		//then
		resultActions
			.andExpect(status().isForbidden())
			.andDo(print());
	}
}
