package com.chumzclone.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Mirrors the JSON body Safaricom's Daraja API posts to our
 * /api/mpesa/callback endpoint after an STK push attempt resolves.
 * See: https://developer.safaricom.co.ke/Documentation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DarajaCallback {
    private Body Body;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private StkCallback stkCallback;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StkCallback {
        private String MerchantRequestID;
        private String CheckoutRequestID;
        private int ResultCode;
        private String ResultDesc;
        private CallbackMetadata CallbackMetadata;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackMetadata {
        private List<CallbackItem> Item;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CallbackItem {
        private String Name;
        private Object Value;
    }
}
