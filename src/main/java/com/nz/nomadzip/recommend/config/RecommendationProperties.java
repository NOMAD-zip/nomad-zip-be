package com.nz.nomadzip.recommend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "recommendation")
public class RecommendationProperties {

    private int topK = 100;
    private int topN = 10;
    private long queryTimeoutMs = 3000L;
    private Qdrant qdrant = new Qdrant();
    private Cache cache = new Cache();
    private Weights weights = new Weights();
    private RuleWeights ruleWeights = new RuleWeights();
    private Reranker reranker = new Reranker();
    private RateLimit rateLimit = new RateLimit();
    private Dataset dataset = new Dataset();
    private Accuracy accuracy = new Accuracy();

    @Getter
    @Setter
    public static class Qdrant {
        private String baseUrl = "http://localhost:6333";
        private String collection = "lodging";
        private int timeoutMs = 2500;
    }

    @Getter
    @Setter
    public static class Cache {
        private int ttlSeconds = 180;
        private long maxSize = 1000;
    }

    @Getter
    @Setter
    public static class Weights {
        private double semantic = 0.45;
        private double relevance = 0.35;
        private double rule = 0.20;
    }

    @Getter
    @Setter
    public static class RuleWeights {
        private double price = 0.35;
        private double safety = 0.30;
        private double distance = 0.15;
        private double rating = 0.20;
    }

    @Getter
    @Setter
    public static class Reranker {
        private boolean enabled = false;
        private String baseUrl = "http://localhost:8000";
        private int timeoutMs = 3000;
    }

    @Getter
    @Setter
    public static class RateLimit {
        private int requestsPerMinute = 60;
    }

    @Getter
    @Setter
    public static class Dataset {
        private String manualLabelPath = "recommendation/manual_labels_1200.csv";
    }

    @Getter
    @Setter
    public static class Accuracy {
        private double baselineNdcg10 = 0.600;
        private double baselinePrecision5 = 0.700;
        private double targetNdcg10 = 0.660;
        private double targetPrecision5 = 0.750;
    }
}
