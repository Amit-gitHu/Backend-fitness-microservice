package com.fitness.activityservice.service.Impl;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.service.ActivityService;
import com.fitness.activityservice.service.UserValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidateService userValidateService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Override
    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        log.info("Processing activity tracking request for user: {}", activityRequest.getUserId());

        // Validate user
        boolean isValidUser = userValidateService.validateUser(activityRequest.getUserId());
        if (!isValidUser) {
            log.error("Invalid user attempted to track activity: {}", activityRequest.getUserId());
            throw new RuntimeException("Invalid User : " + activityRequest.getUserId());
        }

        // Build activity entity
        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getType())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();

        // Save activity to database
        Activity savedActivity = activityRepository.save(activity);
        log.info("Activity saved successfully with ID: {}", savedActivity.getActivityId());

        // Publish to RabbitMQ for AI processing
        publishToRabbitMQ(savedActivity);

        return mapToResponse(savedActivity);
    }

    private void publishToRabbitMQ(Activity activity) {
        try {
            log.info("Publishing activity to RabbitMQ - Exchange: {}, RoutingKey: {}, ActivityId: {}",
                    exchange, routingKey, activity.getActivityId());

            rabbitTemplate.convertAndSend(exchange, routingKey, activity);

            log.info("✅ Successfully published activity {} to RabbitMQ", activity.getActivityId());

        } catch (AmqpException e) {
            log.error("❌ AMQP Exception - Failed to publish activity {} to RabbitMQ: {}",
                    activity.getActivityId(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Unexpected error - Failed to publish activity {} to RabbitMQ: {}",
                    activity.getActivityId(), e.getMessage(), e);
        }
    }

    @Override
    public List<ActivityResponse> getUserActivities(String userId) {
        log.info("Fetching activities for user: {}", userId);
        List<Activity> activities = activityRepository.findByUserId(userId);
        log.info("Found {} activities for user: {}", activities.size(), userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityResponse getActivity(String activityId) {
        log.info("Fetching activity with ID: {}", activityId);
        return activityRepository.findById(activityId)
                .map(activity -> {
                    log.info("Activity found: {}", activityId);
                    return mapToResponse(activity);
                })
                .orElseThrow(() -> {
                    log.error("Activity not found with ID: {}", activityId);
                    return new RuntimeException("Activity not found with ID : " + activityId);
                });
    }

    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse response = new ActivityResponse();
        response.setActivityId(savedActivity.getActivityId());
        response.setUserId(savedActivity.getUserId());
        response.setType(savedActivity.getType());
        response.setDuration(savedActivity.getDuration());
        response.setCaloriesBurned(savedActivity.getCaloriesBurned());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());
        return response;
    }
}