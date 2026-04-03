package com.coursify.client;

import com.coursify.config.BakongProperties;
import com.coursify.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Low-level HTTP client for the Bakong KHQR API.
 *
 * Bakong API docs: https://bakong.nbc.gov.kh/download/KHQR_Merchant_API.pdf
 *
 * Two steps:
 *   1. createQr()  → backend calls Bakong to generate a KHQR code
 *   2. checkStatus() → backend polls Bakong to check if payment was made
 *      (or Bakong calls your webhook — use whichever your plan supports)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BakongApiClient {

    private final BakongProperties props;
    private final RestTemplate restTemplate;

    // ─── Create QR ────────────────────────────────────────────────────────────

    /**
     * Calls Bakong to create a KHQR payment code.
     *
     * Returns the raw KHQR string (e.g. "00020101021229...") which your frontend
     * renders as a QR code image using a KHQR library.
     *
     * @param orderRef  your unique reference for this order
     * @param amount    payment amount
     * @param currency  "USD" or "KHR"
     * @param memo      short description shown to the payer
     * @return          raw KHQR string
     */
    @SuppressWarnings("unchecked")
    public BakongQrResult createQr(String orderRef, BigDecimal amount,
                                   String currency, String memo) {
        String url = props.getBaseUrl() + "/v1/merchant/qr/create";

        Map<String, Object> body = Map.of(
                "merchantId",   props.getMerchantId(),
                "merchantName", props.getMerchantName(),
                "city",         props.getCity(),
                "amount",       amount,
                "currency",     currencyCode(currency),
                "billNumber",   orderRef,
                "mobileNumber", "",     // optional
                "storeLabel",   props.getMerchantName(),
                "terminalLabel", orderRef,
                "purposeOfTransaction", memo
        );

        HttpHeaders headers = authHeaders();
        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST,
                        new HttpEntity<>(body, headers), Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !"SUCCESS".equals(responseBody.get("responseCode"))) {
            log.error("Bakong QR creation failed: {}", responseBody);
            throw new BadRequestException("Failed to create Bakong payment QR. Please try again.");
        }

        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        String qr       = (String) data.get("qr");
        String deepLink  = (String) data.getOrDefault("deepLink", null);

        return new BakongQrResult(qr, deepLink);
    }

    // ─── Check payment status (polling fallback) ───────────────────────────────

    /**
     * Polls Bakong to check if a KHQR has been paid.
     * Use this as a fallback if webhooks are not available on your Bakong plan.
     *
     * @param md5  MD5 hash of the QR string (returned by Bakong in the create response)
     * @return     true if Bakong confirms the payment is completed
     */
    @SuppressWarnings("unchecked")
    public boolean isPaymentCompleted(String md5) {
        String url = props.getBaseUrl() + "/v1/merchant/check-transaction-by-md5";

        Map<String, Object> body = Map.of("md5", md5);

        try {
            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.POST,
                            new HttpEntity<>(body, authHeaders()), Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) return false;

            // Bakong returns responseCode "00" when paid
            return "00".equals(responseBody.get("responseCode"));
        } catch (Exception e) {
            log.warn("Bakong status check failed for md5={}: {}", md5, e.getMessage());
            return false;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getToken());
        return headers;
    }

    /**
     * Bakong uses numeric ISO 4217 currency codes.
     * USD = 840, KHR = 116
     */
    private int currencyCode(String currency) {
        return "KHR".equalsIgnoreCase(currency) ? 116 : 840;
    }

    // ─── Inner result record ───────────────────────────────────────────────────

    public record BakongQrResult(String qrCode, String deepLink) {}
}