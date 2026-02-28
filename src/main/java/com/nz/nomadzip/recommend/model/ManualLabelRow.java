package com.nz.nomadzip.recommend.model;

public record ManualLabelRow(
        long pairId,
        String query,
        long lodgingId,
        int relevance
) {
}
