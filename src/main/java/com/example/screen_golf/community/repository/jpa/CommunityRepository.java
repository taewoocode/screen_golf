package com.example.screen_golf.community.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.screen_golf.community.domain.Community;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
	int countByParentReplyNumber(Integer postNumber);
}
