package com.chumzclone.repository;

import com.chumzclone.entity.Transaction;
import com.chumzclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByCreatedAtDesc(User user);
    Optional<Transaction> findByCheckoutRequestId(String checkoutRequestId);
    List<Transaction> findTop10ByUserOrderByCreatedAtDesc(User user);
}
