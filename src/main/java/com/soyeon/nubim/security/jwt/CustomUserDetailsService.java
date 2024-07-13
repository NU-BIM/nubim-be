package com.soyeon.nubim.security.jwt;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email);
		return org.springframework.security.core.userdetails.User.builder()
			.username(user.getEmail())
			.password("")
			.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
			.build();
	}
}
