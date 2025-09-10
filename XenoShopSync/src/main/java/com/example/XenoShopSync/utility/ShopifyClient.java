package com.example.XenoShopSync.utility;


import com.example.XenoShopSync.entity.Tenant;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ShopifyClient {

    private final RestTemplate restTemplate;

    public ShopifyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> get(Tenant tenant, String endpoint) {
        String url = tenant.getShopifyBaseUrl() + endpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", tenant.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }
}
