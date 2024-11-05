package com.soyeon.nubim.domain.user_block;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.user_block.dto.UserBlockCreateResponse;
import com.soyeon.nubim.domain.user_block.dto.UserBlockDeleteResponse;
import com.soyeon.nubim.domain.user_block.dto.UserBlockReadResponse;
import com.soyeon.nubim.domain.user_block.dto.UserBlockRequest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user-block")
@RequiredArgsConstructor
public class UserBlockControllerV1 {

	private final UserBlockService userBlockService;

	@Operation(description = "사용자가 다른 사용자를 차단한다")
	@PostMapping
	public ResponseEntity<UserBlockCreateResponse> blockUser(@RequestBody UserBlockRequest userBlockRequest) {
		UserBlockCreateResponse blockCreateResponse = userBlockService.blockUser(userBlockRequest);

		return ResponseEntity.ok().body(blockCreateResponse);
	}

	@Operation(description = "사용자가 차단한 사용자들의 리스트를 검색한다")
	@GetMapping
	public ResponseEntity<List<UserBlockReadResponse>> getBlockedUsers() {
		List<UserBlockReadResponse> userBlockReadResponses = userBlockService.getBlockedUsers();

		return ResponseEntity.ok().body(userBlockReadResponses);
	}

	@Operation(description = "사용자가 다른 사용자에 대한 차단을 해제한다")
	@DeleteMapping
	public ResponseEntity<UserBlockDeleteResponse> unblockUser(@RequestBody UserBlockRequest userBlockRequest) {
		UserBlockDeleteResponse blockDeleteResponse = userBlockService.unblockUser(userBlockRequest);

		return ResponseEntity.ok().body(blockDeleteResponse);
	}
}
