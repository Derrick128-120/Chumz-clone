package com.chumzclone.controller;

import com.chumzclone.dto.DarajaCallback;
import com.chumzclone.entity.SavingsGoal;
import com.chumzclone.entity.SavingsGroup;
import com.chumzclone.entity.Transaction;
import com.chumzclone.entity.User;
import com.chumzclone.repository.SavingsGoalRepository;
import com.chumzclone.repository.SavingsGroupRepository;
import com.chumzclone.service.SavingsGoalService;
import com.chumzclone.service.SavingsGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Endpoint Safaricom posts to after an STK push resolves (success or fail).
 * Must be a public, internet-reachable URL in application.properties
 * (daraja.callback-url) - use ngrok while developing locally.
 */
@Slf4j
@RestController
@RequestMapping("/api/mpesa")
@RequiredArgsConstructor
public class MpesaCallbackController {

    private final SavingsGoalRepository goalRepository;
    private final SavingsGroupRepository groupRepository;
    private final SavingsGoalService goalService;
    private final SavingsGroupService groupService;

    @PostMapping("/callback")
    public Map<String, Object> handleCallback(@RequestBody DarajaCallback callback) {
        DarajaCallback.StkCallback stk = callback.getBody() != null ? callback.getBody().getStkCallback() : null;
        if (stk == null) {
            log.warn("Received malformed Daraja callback");
            return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
        }

        log.info("Daraja callback: CheckoutRequestID={} ResultCode={} ResultDesc={}",
                stk.getCheckoutRequestID(), stk.getResultCode(), stk.getResultDesc());

        if (stk.getResultCode() != 0) {
            log.info("Payment not completed (user cancelled or timed out): {}", stk.getResultDesc());
            return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
        }

        // Successful payment - pull amount, receipt number and account ref from metadata
        List<DarajaCallback.CallbackItem> items = stk.getCallbackMetadata() != null
                ? stk.getCallbackMetadata().getItem() : List.of();

        BigDecimal amount = extract(items, "Amount")
                .map(v -> new BigDecimal(v.toString()))
                .orElse(BigDecimal.ZERO);
        String receipt = extract(items, "MpesaReceiptNumber").map(Object::toString).orElse(null);
        String phone = extract(items, "PhoneNumber").map(Object::toString).orElse(null);

        // NOTE: AccountReference (GOAL-{id} / GRP-{id}) isn't echoed back in the
        // standard sandbox metadata, so a production build should persist the
        // pending Transaction keyed by CheckoutRequestID at STK-push time and
        // look it up here rather than re-parsing. Left as a documented TODO
        // for the diploma write-up.
        log.info("Confirmed M-Pesa payment: amount={} receipt={} phone={}", amount, receipt, phone);

        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }

    private java.util.Optional<Object> extract(List<DarajaCallback.CallbackItem> items, String name) {
        return items.stream()
                .filter(i -> name.equals(i.getName()))
                .map(DarajaCallback.CallbackItem::getValue)
                .findFirst();
    }
}
