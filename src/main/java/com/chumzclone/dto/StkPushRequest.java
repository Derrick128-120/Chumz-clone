package com.chumzclone.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StkPushRequest {
    private String phoneNumber; // 2547XXXXXXXX
    private BigDecimal amount;
    private Long goalId;   // optional - deposit into a specific goal
    private Long groupId;  // optional - contribute into a group
}
