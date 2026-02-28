package com.nz.nomadzip.recommend.model;

public record RankedCandidate(
        LodgingCandidateDocument lodging,
        double semanticScore,
        double relevanceScore,
        double ruleScore,
        double finalScore
) {
}
