package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;

import java.util.List;

public interface RecommendationService {
    List<Recommendation> getUserRecommendation(String userId);

    Recommendation getActivityRecommendation(String activityId);
}
