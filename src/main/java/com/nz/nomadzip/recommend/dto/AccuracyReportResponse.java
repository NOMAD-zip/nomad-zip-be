package com.nz.nomadzip.recommend.dto;

import lombok.Builder;

@Builder
public record AccuracyReportResponse(
        int queryCount,
        int pairCount,
        double baselineNdcgAt10,
        double rerankedNdcgAt10,
        double targetNdcgAt10,
        boolean ndcgTargetAchieved,
        double baselinePrecisionAt5,
        double rerankedPrecisionAt5,
        double targetPrecisionAt5,
        boolean precisionTargetAchieved
) {
}
