package com.chumzclone.repository;

import com.chumzclone.entity.User;
import com.chumzclone.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(User user);
}
