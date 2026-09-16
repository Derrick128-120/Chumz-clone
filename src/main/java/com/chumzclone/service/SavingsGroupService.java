package com.chumzclone.service;

import com.chumzclone.entity.*;
import com.chumzclone.repository.GroupMemberRepository;
import com.chumzclone.repository.SavingsGroupRepository;
import com.chumzclone.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGroupService {

    private final SavingsGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final TransactionRepository transactionRepository;
    private final StreakService streakService;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public SavingsGroup createGroup(User creator, String name, String description, BigDecimal targetAmount) {
        SavingsGroup group = SavingsGroup.builder()
                .name(name)
                .description(description)
                .inviteCode(generateInviteCode())
                .createdBy(creator)
                .groupTargetAmount(targetAmount)
                .build();
        group = groupRepository.save(group);

        GroupMember admin = GroupMember.builder()
                .group(group)
                .user(creator)
                .role(GroupMember.MemberRole.ADMIN)
                .build();
        memberRepository.save(admin);

        return group;
    }

    public SavingsGroup joinGroup(User user, String inviteCode) {
        SavingsGroup group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        memberRepository.findByGroupAndUser(group, user).ifPresent(m -> {
            throw new IllegalStateException("Already a member of this group");
        });

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupMember.MemberRole.MEMBER)
                .build();
        memberRepository.save(member);
        return group;
    }

    @Transactional
    public Transaction contribute(User user, SavingsGroup group, BigDecimal amount) {
        GroupMember member = memberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new IllegalStateException("Not a member of this group"));

        member.setTotalContributed(member.getTotalContributed().add(amount));
        memberRepository.save(member);

        group.setGroupCurrentAmount(group.getGroupCurrentAmount().add(amount));
        groupRepository.save(group);

        Transaction txn = Transaction.builder()
                .user(user)
                .group(group)
                .amount(amount)
                .type(Transaction.TransactionType.GROUP_CONTRIBUTION)
                .status(Transaction.TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(txn);

        streakService.recordSaveEvent(user);
        return txn;
    }

    public List<GroupMember> getMembers(SavingsGroup group) {
        return memberRepository.findByGroup(group);
    }

    public List<GroupMember> getGroupsForUser(User user) {
        return memberRepository.findByUser(user);
    }

    public SavingsGroup getById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("CHM-");
        for (int i = 0; i < 6; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
