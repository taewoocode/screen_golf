package com.example.screen_golf.jwts;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		com.example.screen_golf.user.domain.User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new CustomUserDetails(user.getEmail(), user.getPassword(), authorities);
	}
}
