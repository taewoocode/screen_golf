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
	public UserDetails loadUserByUsername(String userIdAsString) {
		Long userId = Long.parseLong(userIdAsString);
		com.example.screen_golf.user.domain.User user = userRepository.findById(userId)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities);
	}
}
