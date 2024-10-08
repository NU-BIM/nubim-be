package com.soyeon.nubim.domain.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soyeon.nubim.domain.user.dto.ProfileImageUpdateResponse;
import com.soyeon.nubim.domain.user.dto.ProfileUpdateRequest;
import com.soyeon.nubim.domain.user.dto.ProfileUpdateResponse;
import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.jwt.dto.TokenDeleteRequestDto;
import com.soyeon.nubim.security.oauth.AppleOAuthLoginService;
import com.soyeon.nubim.security.oauth.GoogleOAuthLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

	private final UserService userService;
	private final GoogleOAuthLoginService googleOAuthLoginService;
	private final AppleOAuthLoginService appleOAuthLoginService;

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getCurrentUserProfile() {
		UserProfileResponseDto currentUserProfile = userService.getCurrentUserProfile();

		return ResponseEntity.ok()
			.body(currentUserProfile);
	}

	@GetMapping("/login-google")
	public ResponseEntity<JwtTokenResponseDto> loginGoogle(@RequestHeader("Authorization") String oauthAccessToken) {
		return googleOAuthLoginService.authenticateWithGoogleToken(oauthAccessToken);
	}

	@GetMapping("/login-apple")
	public ResponseEntity<JwtTokenResponseDto> loginApple(@RequestHeader("Authorization") String idToken) {
		return appleOAuthLoginService.authenticateWithAppleToken(idToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody TokenDeleteRequestDto tokenDeleteRequestDto) {
		Map<String, String> logoutResult = userService.logout(tokenDeleteRequestDto.getRefreshToken());

		return ResponseEntity.ok().body(logoutResult);
	}

	@DeleteMapping("/account")
	public ResponseEntity<?> deleteAccount() {
		Map<String, String> deleteAccountResult = userService.deleteAccount();

		return ResponseEntity.ok().body(deleteAccountResult);
	}

	@Operation(description = "프로필 이미지 변경")
	@PostMapping(value = "/profile-image", consumes = {"multipart/form-data"})
	public ResponseEntity<ProfileImageUpdateResponse> updateProfileImage(@RequestParam MultipartFile profileImage) {
		ProfileImageUpdateResponse profileImageUpdateResponse = userService.updateProfileImage(profileImage);

		return ResponseEntity.ok().body(profileImageUpdateResponse);
	}

	@PostMapping("/profile-update")
	public ResponseEntity<ProfileUpdateResponse> updateProfile(
		@RequestBody ProfileUpdateRequest profileUpdateRequest) {
		ProfileUpdateResponse profileUpdateResponse = userService.updateProfile(profileUpdateRequest);

		return ResponseEntity.ok().body(profileUpdateResponse);
	}

	@Operation(description = "닉네임 중복 및 형식 체크, 중복 시 409, 형식 에러 시 400 반환, 문제 없을 시 200")
	@GetMapping("/nickname/duplication")
	public ResponseEntity<Void> checkNicknameDuplication(
		@RequestParam
		@Size(min = User.NicknamePolicy.MIN_LENGTH, max = User.NicknamePolicy.MAX_LENGTH)
		@Pattern(regexp = User.NicknamePolicy.REGEXP, message = User.NicknamePolicy.ERROR_MESSAGE)
		String nickname) {
		userService.validateDuplicatedNickname(nickname);
		return ResponseEntity.ok().build();
	}
}
