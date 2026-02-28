package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleEngineServiceTest {

    @Test
    void shouldPreferCandidateMatchingNumericFilters() {
        RecommendationProperties properties = new RecommendationProperties();
        RuleEngineService ruleEngineService = new RuleEngineService(properties);

        RecommendationFilters filters = new RecommendationFilters();
        filters.setPriceMax(120_000L);
        filters.setLatitude(33.450701);
        filters.setLongitude(126.570667);
        filters.setMaxDistanceKm(15.0);

        LodgingCandidateDocument good = new LodgingCandidateDocument();
        good.setMonthlyPrice(90_000L);
        good.setSafetyScore(0.9);
        good.setAverageRating(4.8);
        good.setLatitude(33.460000);
        good.setLongitude(126.560000);

        LodgingCandidateDocument bad = new LodgingCandidateDocument();
        bad.setMonthlyPrice(150_000L);
        bad.setSafetyScore(0.2);
        bad.setAverageRating(3.1);
        bad.setLatitude(37.560000);
        bad.setLongitude(126.970000);

        double goodScore = ruleEngineService.score(good, filters, "제주 장기 체류 숙소");
        double badScore = ruleEngineService.score(bad, filters, "제주 장기 체류 숙소");

        assertTrue(goodScore > badScore);
    }

    @Test
    void shouldPreferOceanAndWorkFriendlyLodgingWhenIntentExists() {
        RecommendationProperties properties = new RecommendationProperties();
        RuleEngineService ruleEngineService = new RuleEngineService(properties);
        RecommendationFilters filters = new RecommendationFilters();

        LodgingCandidateDocument oceanWork = new LodgingCandidateDocument();
        oceanWork.setWifi(true);
        oceanWork.setSummary("바다 전망이 보이는 조용한 숙소");
        oceanWork.setAmenityText("와이파이 책상 코워킹");
        oceanWork.setTagText("오션뷰 워케이션");
        oceanWork.setSafetyScore(0.6);
        oceanWork.setAverageRating(4.3);

        LodgingCandidateDocument cityHotel = new LodgingCandidateDocument();
        cityHotel.setWifi(false);
        cityHotel.setSummary("도심 비즈니스 호텔");
        cityHotel.setAmenityText("수영장 조식");
        cityHotel.setTagText("커플");
        cityHotel.setSafetyScore(0.9);
        cityHotel.setAverageRating(4.8);

        double oceanWorkScore = ruleEngineService.score(oceanWork, filters, "바다가 보이는 개발자가 일하기 좋은 숙소");
        double cityHotelScore = ruleEngineService.score(cityHotel, filters, "바다가 보이는 개발자가 일하기 좋은 숙소");

        assertTrue(oceanWorkScore > cityHotelScore);
    }
}
