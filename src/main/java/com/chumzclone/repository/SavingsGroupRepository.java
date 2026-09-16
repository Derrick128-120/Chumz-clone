package com.chumzclone.repository;

import com.chumzclone.entity.SavingsGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsGroupRepository extends JpaRepository<SavingsGroup, Long> {
    Optional<SavingsGroup> findByInviteCode(String inviteCode);
}
