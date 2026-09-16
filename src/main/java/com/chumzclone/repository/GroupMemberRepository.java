package com.chumzclone.repository;

import com.chumzclone.entity.GroupMember;
import com.chumzclone.entity.SavingsGroup;
import com.chumzclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroup(SavingsGroup group);
    List<GroupMember> findByUser(User user);
    Optional<GroupMember> findByGroupAndUser(SavingsGroup group, User user);
}
