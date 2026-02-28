package com.nz.nomadzip.recommend.model;

import java.util.List;

public record QueryRewriteResult(
        String refinedQuery,
        List<String> keywords,
        String language,
        boolean fallbackApplied
) {

    public static QueryRewriteResult fallback(String refinedQuery, List<String> keywords) {
        return new QueryRewriteResult(refinedQuery, keywords, "ko", true);
    }
}
