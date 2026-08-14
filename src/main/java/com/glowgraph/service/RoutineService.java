package com.glowgraph.service;

import com.glowgraph.dto.ConflictInfo;
import com.glowgraph.dto.ConflictReasonInfo;
import com.glowgraph.dto.IndirectRiskInfo;
import com.glowgraph.dto.IngredientConflictRanking;
import com.glowgraph.dto.RecommendationInfo;
import com.glowgraph.dto.SynergyInfo;
import com.glowgraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineService {

    private final GraphQueryRepository graphQueryRepository;

    public RoutineService(GraphQueryRepository graphQueryRepository) {
        this.graphQueryRepository = graphQueryRepository;
    }

    public List<ConflictInfo> checkRoutineConflicts(
            List<String> productNames
    ) {
        return graphQueryRepository.checkRoutineConflicts(productNames);
    }

    public List<RecommendationInfo> findProductsForConcern(
            String concern
    ) {
        return graphQueryRepository.findProductsForConcern(concern);
    }

    public List<ConflictReasonInfo> findConflictReason(
            String ingredient1,
            String ingredient2
    ) {

        ingredient1 = ingredient1.trim();
        ingredient2 = ingredient2.trim();

        if (ingredient1.equalsIgnoreCase(ingredient2)) {
            throw new IllegalArgumentException(
                    "Ingredient1 and Ingredient2 must be different"
            );
        }

        return graphQueryRepository.findConflictReason(
                ingredient1,
                ingredient2
        );
    }

    public List<IngredientConflictRanking> findMostProblematicIngredients() {
        return graphQueryRepository.findMostProblematicIngredients();
    }

    public List<IndirectRiskInfo> findIndirectRisks() {
        return graphQueryRepository.findIndirectRisks();
    }
    
    public List<SynergyInfo> findSynergies(
            String ingredient1,
            String ingredient2
    ) {

        ingredient1 = ingredient1.trim();
        ingredient2 = ingredient2.trim();

        if (ingredient1.equalsIgnoreCase(ingredient2)) {
            throw new IllegalArgumentException(
                    "Ingredient1 and Ingredient2 must be different"
            );
        }

        return graphQueryRepository.findSynergies(
                ingredient1,
                ingredient2
        );
    }

    public List<java.util.Map<String, Object>> findConflictDetails(String ingredientName) {
        return graphQueryRepository.findConflictDetails(ingredientName.trim());
    }
    
}