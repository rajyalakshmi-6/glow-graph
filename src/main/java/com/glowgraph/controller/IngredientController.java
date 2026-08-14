package com.glowgraph.controller;

import com.glowgraph.dto.IngredientConflictRanking;
import com.glowgraph.service.RoutineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "*")
public class IngredientController {

    private final RoutineService routineService;

    public IngredientController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/problematic")
    public List<IngredientConflictRanking> getMostProblematicIngredients() {
        return routineService.findMostProblematicIngredients();
    }
}