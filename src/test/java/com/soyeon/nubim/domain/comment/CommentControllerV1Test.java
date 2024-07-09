package com.soyeon.nubim.domain.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.common.Gender;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumRepository;
import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserRepository;
import jakarta.transaction.Transactional;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
class CommentControllerV1Test {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlbumRepository albumRepository;


    private User testUser;
    private Post testPost;
    private Album testAlbum;

    /**
     * 더미 게시글 및 유저 생성
     */
    @BeforeEach
    void setUp() {
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
                .build();
        userRepository.save(testUser);

        testAlbum = Album.builder()
                .user(testUser)
                .description("Test User's Album")
                .photoUrls("[\"https://example.com/photo1.jpg\", \"https://example.com/photo2.jpg\"]")
                .coordinate(null)
                .coordinateTime(LocalDateTime.now())
                .build();
        albumRepository.save(testAlbum);

        testPost = Post.builder()
                .user(testUser)
                .album(testAlbum)
                .postTitle("Test Post")
                .postContent("This is the content of test post")
                .build();

        postRepository.save(testPost);
    }

    @DisplayName("댓글 정상 생성 테스트")
    @Test
    void createComment_Success() throws Exception {
        //given
        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                .userId(testUser.getUserId())
                .postId(testPost.getPostId())
                .content("Test Comment")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequestDto)));

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.postId").value(testPost.getPostId()))
                .andExpect(jsonPath("$.content").value("Test Comment"));
    }

    @DisplayName("유효하지 않은 userId로 댓글 생성 시 404 에러 테스트")
    @Test
    void createComment_UserNotFound() throws Exception {
        //given
        Long invalidUserId = -1L; // 존재하지 않는 userId
        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                .userId(invalidUserId)
                .postId(testPost.getPostId())
                .content("Test Comment")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequestDto)));

        //then
        resultActions
                .andExpect(status().isNotFound());
    }

    @DisplayName("유효하지 않은 postId로 댓글 생성 시 404 에러 테스트")
    @Test
    void createComment_PostNotFound() throws Exception {
        //given
        Long invalidPostId = -1L; // 존재하지 않는 postId
        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                .userId(testUser.getUserId())
                .postId(invalidPostId) // 유효하지 않은 postId 사용
                .content("Test Comment")
                .build();

        //when
        ResultActions resultActions = mockMvc.perform(post("/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequestDto)));

        //then
        resultActions
                .andExpect(status().isNotFound());
    }

}