package com.glowgraph.controller;

import com.glowgraph.dto.ApiResponse;
import com.glowgraph.dto.ConflictInfo;
import com.glowgraph.service.RoutineService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/routine")
@CrossOrigin(origins = "*")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @PostMapping("/conflicts")
    public ResponseEntity<?> checkConflicts(
            @RequestBody(required = true) List<String> productNames
    ) {

        // 1. Check empty list
        if (productNames == null || productNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one product must be selected"
            );
        }

        // 2. Clean product names
        List<String> cleanedProducts = productNames.stream()
                .map(product -> product == null ? null : product.trim())
                .toList();

        // 3. Check blank product names
        for (String product : cleanedProducts) {

            if (product == null || product.isEmpty()) {
                throw new IllegalArgumentException(
                        "Product name cannot be empty"
                );
            }
        }
        
        if (cleanedProducts.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two products must be selected"
            );
        }

        // 4. Check duplicate products
        Set<String> uniqueProducts = new HashSet<>(cleanedProducts);

        if (uniqueProducts.size() != cleanedProducts.size()) {
            throw new IllegalArgumentException(
                    "Duplicate products are not allowed"
            );
        }

        // 5. Check conflicts
        List<ConflictInfo> result =
                routineService.checkRoutineConflicts(cleanedProducts);

        // 6. No conflicts found
        if (result.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse(
                            "No conflicts found between the selected products"
                    )
            );
        }

        // 7. Conflicts found
        return ResponseEntity.ok(result);
    }
}