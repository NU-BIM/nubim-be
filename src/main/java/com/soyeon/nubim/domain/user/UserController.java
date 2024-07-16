package com.soyeon.nubim.domain.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(name = "refresh_token") String token) {
		ResponseCookie responseCookie = userService.logout(token);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, responseCookie.toString())
			.build();
	}

}
