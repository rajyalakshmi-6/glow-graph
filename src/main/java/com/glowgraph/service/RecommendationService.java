package com.glowgraph.service;

import com.glowgraph.dto.RecommendationInfo;
import com.glowgraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final GraphQueryRepository graphQueryRepository;

    public RecommendationService(GraphQueryRepository graphQueryRepository) {
        this.graphQueryRepository = graphQueryRepository;
    }

    public List<RecommendationInfo> findProductsForConcern(String concern) {
        return graphQueryRepository.findProductsForConcern(concern);
    }
}