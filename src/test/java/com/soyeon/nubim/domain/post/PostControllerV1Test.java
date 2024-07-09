package com.soyeon.nubim.domain.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.common.Gender;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumRepository;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
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
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    private User testUser;
    private Album testAlbum;
    private Long testUserId;
    private Long testAlbumId;

    /**
     * 게시글 생성에 필요한 더미 유저, 앨범 생성
     */
    @BeforeEach
    public void setup() {
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
        testUserId = testUser.getUserId();

        // 앨범 더미 데이터 생성 및 저장
        testAlbum = Album.builder()
                .user(testUser)
                .description("User One's Album")
                .coordinate(null)
                .photoUrls("[\"https://example.com/photo1.jpg\", \"https://example.com/photo2.jpg\"]")
                .build();
        albumRepository.save(testAlbum);
        testAlbumId = testAlbum.getAlbumId();
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
    public void createPost_Success() throws Exception {
        //given
        PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
                .userId(testUserId)
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
     * 유저 없음 에러
     * 404 Not Found
     */
    @DisplayName("유효하지 않은 userId로 게시글 생성 시 404 에러 테스트")
    @Test
    public void createPost_UserNotFound_Error() throws Exception {
        //given
        PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
                .userId(-1L) // 존재하지 않는 userId
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
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * 앨범 없음 에러
     * 404 Not Found
     */
    @DisplayName("유효하지 않은 albumId로 게시글 생성 시 404 에러 테스트")
    @Test
    public void createPost_AlbumNotFound_Error() throws Exception {
        //given
        PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
                .userId(testUserId)
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

    /*
    게시글 조회
     */

    /**
     * 게시글 상세 조회
     * 200 OK
     */
    @DisplayName("게시글 정상 상세 조회 테스트")
    @Test
    public void getPostDetail_WithNullType_Success() throws Exception {
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
    public void getPostDetail_WithSimpleType_Success() throws Exception {
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
    public void getPostDetail_WithInvalidType_BadRequest() throws Exception {
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
    게시글 삭제
     */

    /**
     * 정상적인 게시글 삭제
     * 200 OK
     */
    @DisplayName("게시글 정상 삭제 테스트")
    @Test
    public void deletePost_Success() throws Exception {
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
    public void deletePost_PostNotFound_Error() throws Exception {
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
}