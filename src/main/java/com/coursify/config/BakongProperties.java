package com.coursify.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bakong")
@Getter
@Setter
public class BakongProperties {

    // Your Bakong merchant token (from Bakong developer portal)
    private String token;

    // Base URL: https://api-bakong.nbc.gov.kh  (production)
    //           https://api-sandbox.bakong.nbc.gov.kh  (sandbox)
    private String baseUrl;

    // Your merchant ID registered with Bakong
    private String merchantId;

    // Your store/terminal name shown on the customer's app
    private String merchantName;

    // City shown on the KHQR code
    private String city;

    // Webhook secret for verifying incoming webhook calls from Bakong
    private String webhookSecret;
}