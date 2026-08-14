package com.glowgraph.controller;

import com.glowgraph.dto.ApiResponse;
import com.glowgraph.dto.ConflictReasonInfo;
import com.glowgraph.service.RoutineService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/conflicts")
@CrossOrigin(origins = "*")
public class ConflictController {

    private final RoutineService routineService;

    public ConflictController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/reason")
    public ResponseEntity<?> getConflictReason(

            @RequestParam
            @NotBlank(message = "Ingredient1 is required")
            String ingredient1,

            @RequestParam
            @NotBlank(message = "Ingredient2 is required")
            String ingredient2
    ) {

        List<ConflictReasonInfo> result =
                routineService.findConflictReason(
                        ingredient1.trim(),
                        ingredient2.trim()
                );

        if (result.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "No conflict found between "
                            + ingredient1
                            + " and "
                            + ingredient2
                    )
            );
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getConflictDetails(
            @RequestParam
            @NotBlank(message = "Ingredient name is required")
            String ingredient
    ) {
        return ResponseEntity.ok(routineService.findConflictDetails(ingredient));
    }
}