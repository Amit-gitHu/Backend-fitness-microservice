package com.fitness.activityservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    @JsonProperty("activityId")
    private String activityId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("type")
    private ActivityType type;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("caloriesBurned")
    private Integer caloriesBurned;

    @JsonProperty("startTime")
    private LocalDateTime startTime;

    @Field("metrics")
    @JsonProperty("additionalMetrics")
    private Map<String, Object> additionalMetrics;

    @CreatedDate
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
