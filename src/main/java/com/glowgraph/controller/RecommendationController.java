package com.glowgraph.controller;

import com.glowgraph.dto.ApiResponse;
import com.glowgraph.dto.RecommendationInfo;
import com.glowgraph.service.RoutineService;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
@Validated
public class RecommendationController {

    private final RoutineService routineService;

    public RecommendationController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping
    public ResponseEntity<?> getRecommendations(
            @RequestParam
            @NotBlank(message = "Concern is required")
            String concern
    ) {

        List<RecommendationInfo> result =
                routineService.findProductsForConcern(concern.trim());

        if (result.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "No products found for concern: " + concern
                    )
            );
        }

        return ResponseEntity.ok(result);
    }
}