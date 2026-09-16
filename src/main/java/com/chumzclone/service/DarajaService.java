package com.chumzclone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

/**
 * Thin wrapper around Safaricom's Daraja API (sandbox) for STK Push
 * (Lipa na M-Pesa Online). Requires consumer key/secret + passkey from
 * https://developer.safaricom.co.ke.
 *
 * Flow:
 *  1. requestAccessToken() -> OAuth bearer token (valid ~1hr)
 *  2. initiateStkPush()    -> prompts the user's phone for M-Pesa PIN
 *  3. Safaricom posts the result to daraja.callback-url, handled by
 *     MpesaCallbackController -> DarajaService.handleCallback logic lives
 *     in the controller since it needs repository access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DarajaService {

    private final WebClient.Builder webClientBuilder;

    @Value("${daraja.base-url}")
    private String baseUrl;

    @Value("${daraja.consumer-key}")
    private String consumerKey;

    @Value("${daraja.consumer-secret}")
    private String consumerSecret;

    @Value("${daraja.shortcode}")
    private String shortcode;

    @Value("${daraja.passkey}")
    private String passkey;

    @Value("${daraja.callback-url}")
    private String callbackUrl;

    private WebClient client() {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    public String requestAccessToken() {
        String credentials = Base64.getEncoder()
                .encodeToString((consumerKey + ":" + consumerSecret).getBytes());

        Map<String, Object> response = client().get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("Failed to obtain Daraja access token - check sandbox credentials");
        }
        return (String) response.get("access_token");
    }

    /**
     * Initiates an STK push prompt on the saver's phone.
     * accountRef is echoed back in the callback so we know which goal/group to credit.
     */
    public Map<String, Object> initiateStkPush(String phoneNumber, BigDecimal amount, String accountRef) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder()
                .encodeToString((shortcode + passkey + timestamp).getBytes());

        Map<String, Object> body = Map.ofEntries(
                Map.entry("BusinessShortCode", shortcode),
                Map.entry("Password", password),
                Map.entry("Timestamp", timestamp),
                Map.entry("TransactionType", "CustomerPayBillOnline"),
                Map.entry("Amount", amount.intValue()),
                Map.entry("PartyA", phoneNumber),
                Map.entry("PartyB", shortcode),
                Map.entry("PhoneNumber", phoneNumber),
                Map.entry("CallBackURL", callbackUrl),
                Map.entry("AccountReference", accountRef),
                Map.entry("TransactionDesc", "Chumz Clone savings deposit")
        );

        String token = requestAccessToken();

        return client().post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
