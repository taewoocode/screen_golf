package com.example.screen_golf.jwts;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.screen_golf.user.domain.UserStatus;
import com.example.screen_golf.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		com.example.screen_golf.user.domain.User user = userRepository.findById(Long.parseLong(username))
			.orElseThrow(() -> new UsernameNotFoundException("해당 ID의 유저를 찾을 수 없습니다.: " + username));

		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new UsernameNotFoundException("유저가 활동중이지 않습니다.");
		}

		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities);
	}

}
