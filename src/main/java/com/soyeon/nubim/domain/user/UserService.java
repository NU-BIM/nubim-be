package com.soyeon.nubim.domain.user;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.soyeon.nubim.common.enums.Gender;
import com.soyeon.nubim.common.util.aws.S3ImageUploader;
import com.soyeon.nubim.domain.comment.CommentRepository;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.postlike.PostLikeRepository;
import com.soyeon.nubim.domain.user.dto.ProfileImageUpdateResponse;
import com.soyeon.nubim.domain.user.dto.ProfileUpdateRequest;
import com.soyeon.nubim.domain.user.dto.ProfileUpdateResponse;
import com.soyeon.nubim.domain.user.dto.TermsAgreementStatusResponse;
import com.soyeon.nubim.domain.user.dto.TermsAgreementUpdateRequest;
import com.soyeon.nubim.domain.user.dto.TermsAgreementUpdateResponse;
import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.domain.user.exception.DeletedUserSignupAttemptException;
import com.soyeon.nubim.domain.user.exception.InvalidNicknameFormatException;
import com.soyeon.nubim.domain.user.exception.MultipleProfileUpdateException;
import com.soyeon.nubim.domain.user.exception.MultipleUserAgreementUpdateException;
import com.soyeon.nubim.domain.user.exception.NicknameAlreadyExistsException;
import com.soyeon.nubim.domain.user.exception.NicknameNullOrEmptyException;
import com.soyeon.nubim.domain.user.exception.UnsupportedProfileImageTypeException;
import com.soyeon.nubim.domain.user.exception.UserAgreementUpdateFailException;
import com.soyeon.nubim.domain.user.exception.UserNotFoundException;
import com.soyeon.nubim.domain.user.exception.UsernameNullOrEmptyException;
import com.soyeon.nubim.domain.userfollow.UserFollowRepository;
import com.soyeon.nubim.security.blacklist_accesstoken.AccessTokenBlacklistService;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final int UPDATE_SUCCESS = 1;
	private static final int UPDATE_FAIL = 0;
	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;
	private final AccessTokenBlacklistService accessTokenBlacklistService;
	private final UserMapper userMapper;
	private final S3ImageUploader s3ImageUploader;
	private final LoggedInUserService loggedInUserService;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostRepository postRepository;
	private final UserFollowRepository userFollowRepository;

	public UserProfileResponseDto getCurrentUserProfile() {
		User currentUser = loggedInUserService.getCurrentUser();

		return userMapper.toUserProfileResponseDto(currentUser);
	}

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> UserNotFoundException.forEmail(email));
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public Map<String, String> logout(String accessToken) {
		String currentUserEmail = loggedInUserService.getCurrentUserEmail();
		refreshTokenService.deleteRefreshToken(currentUserEmail);
		accessTokenBlacklistService.addToBlacklist(accessToken);

		return Map.of("status", "success",
			"message", "your refresh token deleted");
	}

	@Transactional
	public Map<String, String> deleteAccount(String accessToken) {
		Long currentUserId = loggedInUserService.getCurrentUserId();
		validateUserExists(currentUserId);

		String anonymizedNickname = UserNicknameGenerator.generateAnonymizedNickname();
		userRepository.deleteAccount(currentUserId, anonymizedNickname);
		commentRepository.deleteCommentByUserId(currentUserId);
		// post_like 는 hard delete, 그 외 soft delete
		postLikeRepository.deletePostLikeByUserId(currentUserId);
		postRepository.deletePostByUserId(currentUserId);
		userFollowRepository.deleteFollowerByUserId(currentUserId);
		userFollowRepository.deleteFolloweeByUserId(currentUserId);

		String currentUserEmail = loggedInUserService.getCurrentUserEmail();
		refreshTokenService.deleteRefreshToken(currentUserEmail);
		accessTokenBlacklistService.addToBlacklist(accessToken);

		return Map.of("status", "success",
			"message", "your account deleted");
	}

	public void validateUserExists(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(userId);
		}
	}

	public User findUserByIdOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}

	@Transactional
	public ProfileImageUpdateResponse updateProfileImage(MultipartFile profileImage) {
		validateProfileImageContentType(profileImage.getContentType());

		String uploadPath = "users/" + loggedInUserService.getCurrentUserId() + "/profile/" + UUID.randomUUID()
			.toString()
			.substring(0, 4);
		String uploadResponse = s3ImageUploader.uploadImage(uploadPath, profileImage);

		if (uploadResponse.contains("fail")) {
			return new ProfileImageUpdateResponse("profile image update fail", null);
		}
		userRepository.updateProfileImage(uploadResponse, loggedInUserService.getCurrentUserId());
		return new ProfileImageUpdateResponse("profile image update success", uploadResponse);
	}

	@Transactional
	public ProfileUpdateResponse updateProfile(ProfileUpdateRequest updateRequest) {
		String username = updateRequest.getUsername();
		String nickname = updateRequest.getNickname();
		String profileIntroduction = updateRequest.getProfileIntroduction();
		String phoneNumber = updateRequest.getPhoneNumber();
		LocalDateTime birthDate = updateRequest.getBirthDate();
		Gender gender = updateRequest.getGender();
		Long currentUserId = loggedInUserService.getCurrentUserId();

		validateUsername(username);
		validateNickname(nickname);

		int updateResult = userRepository.updateProfile(username, nickname, profileIntroduction,
			phoneNumber, birthDate, gender, currentUserId);

		if (updateResult == UPDATE_SUCCESS) {
			return new ProfileUpdateResponse("profile update success");
		}
		if (updateResult == UPDATE_FAIL) {
			return new ProfileUpdateResponse("profile update fail");
		}
		throw new MultipleProfileUpdateException();
	}

	@Transactional
	public TermsAgreementUpdateResponse updateTermsAgreement(TermsAgreementUpdateRequest request) {
		Long userId = loggedInUserService.getCurrentUserId();
		boolean privacyAgreement = request.isPrivacyAgreement();
		boolean serviceAgreement = request.isServiceAgreement();

		int updateResult = userRepository.updateTermsAgreement(userId, privacyAgreement, serviceAgreement);

		if (updateResult == UPDATE_SUCCESS) {
			return new TermsAgreementUpdateResponse("terms agreement update success");
		}
		if (updateResult == UPDATE_FAIL) {
			throw new UserAgreementUpdateFailException();
		}
		throw new MultipleUserAgreementUpdateException(updateResult);
	}

	public TermsAgreementStatusResponse checkTermsAgreement() {
		Long currentUserId = loggedInUserService.getCurrentUserId();
		boolean termsAgreement = userRepository.isAllAgreementsAccepted(currentUserId);

		return new TermsAgreementStatusResponse(termsAgreement);
	}

	public void checkIfUserIsDeleted(String email) {
		if (userRepository.existsByEmailAndIsDeletedTrue(email)) {
			throw new DeletedUserSignupAttemptException(email);
		}
	}

	private void validateProfileImageContentType(String contentType) {
		if (contentType == null || !contentType.equals("image/png")) {
			throw new UnsupportedProfileImageTypeException(contentType);
		}
	}

	private void validateUsername(String username) {
		if (username == null || username.isEmpty()) {
			throw new UsernameNullOrEmptyException();
		}
	}

	private void validateNickname(String nickname) {
		if (nickname == null || nickname.isEmpty()) {
			throw new NicknameNullOrEmptyException();
		}
		if (nickname.length() < User.NicknamePolicy.MIN_LENGTH || nickname.length() > User.NicknamePolicy.MAX_LENGTH) {
			throw new InvalidNicknameFormatException("must be between " + User.NicknamePolicy.MIN_LENGTH +
				" and " + User.NicknamePolicy.MAX_LENGTH + " characters");
		}
		if (!Pattern.matches(User.NicknamePolicy.REGEXP, nickname)) {
			throw new InvalidNicknameFormatException("contains illegal characters");
		}
		if (!userRepository.isNicknameMatchingForUser(loggedInUserService.getCurrentUserId(), nickname)) {
			validateDuplicatedNickname(nickname);
		}
	}

	public void validateDuplicatedNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new NicknameAlreadyExistsException(nickname);
		}
	}

	public Page<User> searchUserByNickname(Pageable pageable, String query) {
		return userRepository.findByNicknameStartsWith(pageable, query);
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> UserNotFoundException.forNickname(nickname));
	}
}
