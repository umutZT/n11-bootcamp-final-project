package com.bootcamp.paymentservice.iyzico;

import com.iyzipay.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IyzicoConfig {

    private final String apiKey;
    private final String secretKey;
    private final String baseUrl;

    public IyzicoConfig(@Value("${iyzico.api-key}") String apiKey,
                        @Value("${iyzico.secret-key}") String secretKey,
                        @Value("${iyzico.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    @Bean
    public Options iyzicoOptions() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }
}
