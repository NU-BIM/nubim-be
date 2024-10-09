package com.soyeon.nubim.domain.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.soyeon.nubim.common.enums.Provider;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.comment.CommentRepository;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.postlike.PostLike;
import com.soyeon.nubim.domain.postlike.PostLikeRepository;
import com.soyeon.nubim.domain.userfollow.UserFollow;
import com.soyeon.nubim.domain.userfollow.UserFollowRepository;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class DeleteAccountTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PostLikeRepository postLikeRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserFollowRepository userFollowRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Autowired
	private EntityManager em;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private User user;
	private String accessToken;
	private String refreshToken;
	private Post post;
	private User anotherUser;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testUser")
			.nickname("testNickname")
			.email("test@email.com")
			.role(Role.USER)
			.provider(Provider.GOOGLE)
			.build();
		userRepository.save(user);

		accessToken =
			jwtTokenProvider.generateAccessToken(user.getUserId().toString(), user.getEmail(), user.getRole().name());
		refreshToken =
			jwtTokenProvider.generateRefreshToken(user.getUserId().toString(), user.getEmail(), user.getRole().name());

		refreshTokenService.upsertRefreshTokenEntity(refreshToken, user.getEmail());

		// 테스트 데이터 생성
		Post post = new Post();
		post.setUser(user);
		post.setPostTitle("test post title");
		this.post = postRepository.save(post);

		Comment comment = new Comment();
		comment.setPost(post);
		comment.setUser(user);
		comment.setCommentContent("test comment content");
		commentRepository.save(comment);

		PostLike postLike = new PostLike();
		postLike.setPost(post);
		postLike.setUser(user);
		postLikeRepository.save(postLike);

		User anotherUser = User.builder()
			.username("anotherUser")
			.nickname("anotherNickname")
			.email("another@email.com")
			.role(Role.USER)
			.provider(Provider.GOOGLE)
			.build();
		this.anotherUser = userRepository.save(anotherUser);

		UserFollow userFollow = new UserFollow();
		userFollow.setFollower(user);
		userFollow.setFollowee(anotherUser);
		userFollowRepository.save(userFollow);
	}

	private void performAccountDeletion() throws Exception {
		String refreshTokenJson = "{\"refreshToken\": \"" + refreshToken + "\"}";

		mockMvc.perform(delete("/v1/users/account")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(refreshTokenJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.message").value("your account deleted"));

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("계정 삭제 전후 User 엔티티 테스트 - 성공 case")
	void testUserDeletion() throws Exception {
		// 삭제 전: User가 조회되어야 함
		Optional<User> beforeDelete = userRepository.findById(user.getUserId());
		assertTrue(beforeDelete.isPresent());

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후: JPA로 User 조회 불가능
		Optional<User> afterDelete = userRepository.findById(user.getUserId());
		assertFalse(afterDelete.isPresent());

		// 삭제 후: JDBC로 User 조회 가능 (is_deleted = true)
		String sql = "SELECT * FROM users WHERE user_id = ? AND is_deleted = true";
		Map<String, Object> deletedUserMap = jdbcTemplate.queryForMap(sql, user.getUserId());
		assertEquals(user.getUserId(), deletedUserMap.get("user_id"));
		assertTrue((boolean)deletedUserMap.get("is_deleted"));
	}

	@Test
	@DisplayName("계정 삭제 전후 Post 엔티티 테스트 - 성공 case")
	void testPostDeletion() throws Exception {
		// 삭제 전: Post가 조회되어야 함
		List<Post> beforeDelete = postRepository.findByUserUserId(user.getUserId());
		assertFalse(beforeDelete.isEmpty());

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후: JPA로 Post 조회 불가능
		List<Post> afterDelete = postRepository.findByUserUserId(user.getUserId());
		assertTrue(afterDelete.isEmpty());

		// 삭제 후: JDBC로 Post 조회 가능 (is_deleted = true)
		String sql = "SELECT * FROM post WHERE user_id = ? AND is_deleted = true";
		List<Map<String, Object>> deletedPosts = jdbcTemplate.queryForList(sql, user.getUserId());
		assertFalse(deletedPosts.isEmpty());
		assertTrue((boolean)deletedPosts.get(0).get("is_deleted"));
	}

	@Test
	@DisplayName("계정 삭제 전후 Comment 엔티티 테스트 - 성공 case")
	void testCommentDeletion() throws Exception {
		// 삭제 전: Comment가 조회되어야 함
		Page<Comment> beforeDelete = commentRepository.findByPostPostId(post.getPostId(), PageRequest.of(0, 10));
		assertFalse(beforeDelete.isEmpty());

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후: JPA로 Comment 조회 불가능
		Page<Comment> afterDelete = commentRepository.findByPostPostId(post.getPostId(), PageRequest.of(0, 10));
		assertTrue(afterDelete.isEmpty());

		// 삭제 후: JDBC로 Comment 조회 가능 (is_deleted = true)
		String sql = "SELECT * FROM comment WHERE user_id = ? AND is_deleted = true";
		List<Map<String, Object>> deletedComments = jdbcTemplate.queryForList(sql, user.getUserId());
		assertFalse(deletedComments.isEmpty());
		assertTrue((boolean)deletedComments.get(0).get("is_deleted"));
	}

	@Test
	@DisplayName("계정 삭제 전후 PostLike 엔티티 테스트 - 성공 case")
	void testPostLikeDeletion() throws Exception {
		// 삭제 전: PostLike가 존재해야 함
		assertTrue(postLikeRepository.existsPostLikeByPostAndUser(post.getPostId(), user.getUserId()));

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후: PostLike가 완전히 삭제되어야 함 (hard delete)
		assertFalse(postLikeRepository.existsPostLikeByPostAndUser(post.getPostId(), user.getUserId()));

		// JDBC로도 조회되지 않아야 함
		String sql = "SELECT COUNT(*) FROM post_like WHERE user_id = ?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, user.getUserId());
		assertEquals(0, count);
	}

	@Test
	@DisplayName("계정 삭제 전후 UserFollow 엔티티 테스트 - 성공 case")
	void testUserFollowDeletion() throws Exception {
		// 삭제 전: UserFollow가 조회되어야 함
		Optional<UserFollow> beforeDelete = userFollowRepository.findByFollowerAndFollowee(user, anotherUser);
		assertTrue(beforeDelete.isPresent());

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후: JPA로 UserFollow 조회 불가능
		Optional<UserFollow> afterDelete = userFollowRepository.findByFollowerAndFollowee(user, anotherUser);
		assertFalse(afterDelete.isPresent());

		// 삭제 후: JDBC로 조회 가능 (is_deleted = true)
		String sql = "SELECT * FROM user_follow WHERE follower_id = ? AND is_deleted = true";
		List<Map<String, Object>> deletedFollows = jdbcTemplate.queryForList(sql, user.getUserId());
		assertFalse(deletedFollows.isEmpty());
		assertTrue((boolean)deletedFollows.get(0).get("is_deleted"));
	}

	@Test
	@DisplayName("계정 삭제 전후 RefreshToken 엔티티 테스트 - 성공 case")
	void testRefreshTokenDeletion() throws Exception {
		// 삭제 전 : Refresh Token가 조회되어야 함
		assertTrue(refreshTokenService.isRefreshTokenExist(refreshToken));

		// 계정 삭제 수행
		performAccountDeletion();

		// 삭제 후 : JPA로 조회 불가능 (hard delete)
		assertFalse(refreshTokenService.isRefreshTokenExist(refreshToken));

		// 삭제 후 : JDBC로도 조회 불가능
		String sql = "SELECT COUNT(*) FROM refresh_token WHERE email = ?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, user.getEmail());
		assertEquals(0, count);
	}

	@Test
	@DisplayName("회원 탈퇴 테스트 - 실패 case: 인증되지 않은 사용자")
	void deleteAccount_Unauthorized() throws Exception {
		mockMvc.perform(delete("/v1/users/account"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("회원 탈퇴 테스트 - 실패 case: 이미 삭제된 계정, 멱등성 보장")
	void deleteAccount_AlreadyDeleted() throws Exception {
		// 먼저 계정 삭제
		performAccountDeletion();

		// 다시 삭제 시도
		String refreshTokenJson = "{\"refreshToken\": \"" + refreshToken + "\"}";

		mockMvc.perform(delete("/v1/users/account")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(refreshTokenJson)
			)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("User not found with id " + user.getUserId()));
	}

	@Test
	@DisplayName("회원 탈퇴 테스트 - 실패 case: 존재하지 않는 사용자")
	void deleteAccount_UserNotFound() throws Exception {
		// 존재하지 않는 사용자 ID로 토큰 생성
		String userId = "999999";
		String userEmail = "nonexistent@email.com";
		String role = "USER";
		String nonExistUserAccessToken = jwtTokenProvider.generateAccessToken(userId, userEmail, role);
		String nonExistUserRefreshToken = jwtTokenProvider.generateRefreshToken(userId, userEmail, role);

		String refreshTokenJson = "{\"refreshToken\": \"" + nonExistUserRefreshToken + "\"}";

		mockMvc.perform(delete("/v1/users/account")
				.header("Authorization", "Bearer " + nonExistUserAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(refreshTokenJson)
			)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("User not found with id " + userId));
	}

}