package com.nz.nomadzip.recommend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.model.QueryRewriteResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class QueryRewriteService {

    private static final String SYSTEM_PROMPT = """
            당신은 숙소 검색 질의 정제기입니다.
            - 최종 추천을 만들지 말고 검색용 질의만 정제하세요.
            - 의미를 보존하고 과장하지 마세요.
            - 출력은 반드시 JSON 객체 1개만 반환합니다.
            {"refinedQuery":"...","keywords":["..."],"language":"ko"}
            """;

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final RuleBasedQueryRewriteService ruleBasedFallback;
    private final Executor recommendationExecutor;

    public QueryRewriteService(ChatClient.Builder chatClientBuilder,
                               ObjectMapper objectMapper,
                               RuleBasedQueryRewriteService ruleBasedFallback,
                               @Qualifier("recommendationExecutor") Executor recommendationExecutor) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
        this.ruleBasedFallback = ruleBasedFallback;
        this.recommendationExecutor = recommendationExecutor;
    }

    @Async("recommendationExecutor")
    @Retry(name = "queryRewrite")
    @CircuitBreaker(name = "queryRewrite", fallbackMethod = "rewriteFallback")
    @TimeLimiter(name = "queryRewrite")
    public CompletableFuture<QueryRewriteResult> rewriteAsync(String query, RecommendationFilters filters) {
        return CompletableFuture.supplyAsync(() -> rewriteCached(query, filters), recommendationExecutor);
    }

    @Cacheable(cacheNames = "queryRewrite", key = "#query")
    public QueryRewriteResult rewriteCached(String query, RecommendationFilters filters) {
        String userPrompt = buildUserPrompt(query, filters);
        String raw = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userPrompt)
                .call()
                .content();

        return parseResult(raw, query, filters);
    }

    private String buildUserPrompt(String query, RecommendationFilters filters) {
        String filterSummary = filters == null
                ? "없음"
                : "priceMax=" + filters.getPriceMax() +
                ", wifi=" + filters.getWifi() +
                ", minSafetyScore=" + filters.getMinSafetyScore() +
                ", minAverageRating=" + filters.getMinAverageRating() +
                ", latitude=" + filters.getLatitude() +
                ", longitude=" + filters.getLongitude() +
                ", maxDistanceKm=" + filters.getMaxDistanceKm();

        return "원문 질의: " + query + "\n"
                + "필터: " + filterSummary + "\n"
                + "반드시 JSON만 반환하세요.";
    }

    private QueryRewriteResult parseResult(String raw, String query, RecommendationFilters filters) {
        if (raw == null || raw.isBlank()) {
            return ruleBasedFallback.rewrite(query, filters);
        }

        String sanitized = raw
                .replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            RewritePayload payload = objectMapper.readValue(sanitized, RewritePayload.class);
            if (payload.refinedQuery == null || payload.refinedQuery.isBlank()) {
                return ruleBasedFallback.rewrite(query, filters);
            }
            List<String> keywords = payload.keywords == null ? List.of() : payload.keywords;
            String language = payload.language == null || payload.language.isBlank() ? "ko" : payload.language;
            return new QueryRewriteResult(payload.refinedQuery, keywords, language, false);
        } catch (Exception parseError) {
            log.warn("LLM query rewrite parsing failed. fallback enabled.", parseError);
            return ruleBasedFallback.rewrite(query, filters);
        }
    }

    private CompletableFuture<QueryRewriteResult> rewriteFallback(String query,
                                                                  RecommendationFilters filters,
                                                                  Throwable throwable) {
        log.warn("LLM query rewrite failed. fallback enabled.", throwable);
        return CompletableFuture.completedFuture(ruleBasedFallback.rewrite(query, filters));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RewritePayload {
        public String refinedQuery;
        public List<String> keywords;
        public String language;
    }
}
