package com.nz.nomadzip.recommend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class RecommendationWebClientConfig {

    private final RecommendationProperties recommendationProperties;

    @Bean
    @Qualifier("qdrantWebClient")
    public WebClient qdrantWebClient(WebClient.Builder builder) {
        return builder.baseUrl(recommendationProperties.getQdrant().getBaseUrl()).build();
    }

    @Bean
    @Qualifier("rerankerWebClient")
    public WebClient rerankerWebClient(WebClient.Builder builder) {
        return builder.baseUrl(recommendationProperties.getReranker().getBaseUrl()).build();
    }
}
