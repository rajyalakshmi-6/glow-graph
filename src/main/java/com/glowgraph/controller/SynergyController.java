package com.glowgraph.controller;

import com.glowgraph.dto.ApiResponse;
import com.glowgraph.dto.SynergyInfo;
import com.glowgraph.service.RoutineService;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/synergies")
@CrossOrigin(origins = "*")
public class SynergyController {

    private final RoutineService routineService;

    public SynergyController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/test")
    public String test() {
        return "Synergy Controller is working";
    }

    @GetMapping
    public ResponseEntity<?> getSynergy(

            @RequestParam
            @NotBlank(message = "Ingredient1 is required")
            String ingredient1,

            @RequestParam
            @NotBlank(message = "Ingredient2 is required")
            String ingredient2
    ) {

        List<SynergyInfo> result =
                routineService.findSynergies(
                        ingredient1.trim(),
                        ingredient2.trim()
                );

        if (result.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "No synergy found between "
                            + ingredient1
                            + " and "
                            + ingredient2
                    )
            );
        }

        return ResponseEntity.ok(result);
    }
}