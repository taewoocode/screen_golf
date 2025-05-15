// package com.example.screen_golf.community.service;
//
// import java.util.List;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.screen_golf.community.domain.Community;
// import com.example.screen_golf.community.repository.jpa.CommunityRepository;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class PostSeederBatchService {
//
// 	private final CommunityRepository communityRepository;
//
// 	@Transactional
// 	public void saveBatch(List<Community> batch) {
// 		communityRepository.saveAll(batch);
// 		communityRepository.flush();
// 	}
// }
