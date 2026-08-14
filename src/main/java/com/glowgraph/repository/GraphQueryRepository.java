package com.glowgraph.repository;

import com.glowgraph.dto.ConflictInfo;
import com.glowgraph.dto.ConflictReasonInfo;
import com.glowgraph.dto.IndirectRiskInfo;
import com.glowgraph.dto.IngredientConflictRanking;
import com.glowgraph.dto.RecommendationInfo;
import com.glowgraph.dto.SynergyInfo;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class GraphQueryRepository {

    private final Neo4jClient neo4jClient;

    public GraphQueryRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<ConflictInfo> checkRoutineConflicts(List<String> productNames) {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (p1:Product)-[:CONTAINS]->(i1:Ingredient), " +
                "      (p2:Product)-[:CONTAINS]->(i2:Ingredient) " +
                "MATCH (i1)-[c:CONFLICTS_WITH]-(i2) " +
                "WHERE p1.name IN $products " +
                "AND p2.name IN $products " +
                "AND p1.name < p2.name " +
                "RETURN p1.name AS product1, " +
                "i1.name AS ingredient1, " +
                "i2.name AS ingredient2, " +
                "p2.name AS product2, " +
                "c.reason AS reason, " +
                "c.severity AS severity " +
                "ORDER BY CASE c.severity " +
                "WHEN 'high' THEN 1 " +
                "WHEN 'medium' THEN 2 " +
                "WHEN 'low' THEN 3 " +
                "ELSE 4 END, " +
                "product1 ASC, product2 ASC"
        )
        .bind(productNames)
        .to("products")
        .fetch()
        .all();

        List<ConflictInfo> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            result.add(
                    new ConflictInfo(
                            (String) row.get("product1"),
                            (String) row.get("ingredient1"),
                            (String) row.get("ingredient2"),
                            (String) row.get("product2"),
                            (String) row.get("reason"),
                            (String) row.get("severity")
                    )
            );
        }

        return result;
    }
    public List<RecommendationInfo> findProductsForConcern(String concern) {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (p:Product)-[:CONTAINS]->(i:Ingredient)" +
                "-[:TARGETS]->(c:Concern) " +
                "WHERE c.name = $concern " +
                "RETURN p.name AS productName, " +
                "p.brand AS brand, " +
                "p.category AS category, " +
                "p.price AS price, " +
                "p.imageUrl AS imageUrl, " +
                "collect(DISTINCT i.name) AS ingredients, " +
                "c.name AS concern " +
                "ORDER BY p.name"
        )
        .bind(concern)
        .to("concern")
        .fetch()
        .all();

        List<RecommendationInfo> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            Double price = row.get("price") != null
                    ? ((Number) row.get("price")).doubleValue()
                    : null;

            @SuppressWarnings("unchecked")
            List<String> ingredients =
                    (List<String>) row.get("ingredients");

            String ingredient = ingredients != null
                    ? String.join(", ", ingredients)
                    : null;

            result.add(
                    new RecommendationInfo(
                            (String) row.get("productName"),
                            (String) row.get("brand"),
                            (String) row.get("category"),
                            price,
                            (String) row.get("imageUrl"),
                            ingredient,
                            (String) row.get("concern")
                    )
            );
        }

        return result;
    }
    
    public List<ConflictReasonInfo> findConflictReason(
            String ingredient1,
            String ingredient2
    ) {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (i1:Ingredient {name: $ingredient1})" +
                "-[c:CONFLICTS_WITH]->" +
                "(i2:Ingredient {name: $ingredient2}) " +
                "RETURN i1.name AS ingredient1, " +
                "i2.name AS ingredient2, " +
                "c.reason AS reason, " +
                "c.severity AS severity"
        )
        .bind(ingredient1)
        .to("ingredient1")
        .bind(ingredient2)
        .to("ingredient2")
        .fetch()
        .all();

        List<ConflictReasonInfo> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            result.add(
                    new ConflictReasonInfo(
                            (String) row.get("ingredient1"),
                            (String) row.get("ingredient2"),
                            (String) row.get("reason"),
                            (String) row.get("severity")
                    )
            );
        }

        return result;
    }
    
    public List<IngredientConflictRanking> findMostProblematicIngredients() {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (i:Ingredient) " +
                "OPTIONAL MATCH (i)-[:CONFLICTS_WITH]-(conflict:Ingredient) " +
                "RETURN i.name AS ingredient, " +
                "count(conflict) AS conflictCount " +
                "ORDER BY conflictCount DESC, ingredient ASC"
        )
        .fetch()
        .all();

        List<IngredientConflictRanking> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            Long conflictCount = row.get("conflictCount") != null
                    ? ((Number) row.get("conflictCount")).longValue()
                    : 0L;

            result.add(
                    new IngredientConflictRanking(
                            (String) row.get("ingredient"),
                            conflictCount
                    )
            );
        }

        return result;
    }
    public List<IndirectRiskInfo> findIndirectRisks() {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH p=(i1:Ingredient)-[:CONFLICTS_WITH*2..3]->(i2:Ingredient) " +
                "WHERE i1 <> i2 " +
                "RETURN i1.name AS sourceIngredient, " +
                "i2.name AS targetIngredient, " +
                "length(p) AS pathLength, " +
                "[node IN nodes(p) | node.name] AS conflictPath " +
                "ORDER BY pathLength ASC, sourceIngredient ASC"
        )
        .fetch()
        .all();

        List<IndirectRiskInfo> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            Long pathLength = row.get("pathLength") != null
                    ? ((Number) row.get("pathLength")).longValue()
                    : 0L;

            @SuppressWarnings("unchecked")
            List<String> conflictPath =
                    (List<String>) row.get("conflictPath");

            result.add(
                    new IndirectRiskInfo(
                            (String) row.get("sourceIngredient"),
                            (String) row.get("targetIngredient"),
                            pathLength,
                            conflictPath
                    )
            );
        }

        return result;
    }
    
    
    public List<SynergyInfo> findSynergies(
            String ingredient1,
            String ingredient2
    ) {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (i1:Ingredient {name: $ingredient1})" +
                "-[s:PAIRS_WELL_WITH]->" +
                "(i2:Ingredient {name: $ingredient2}) " +
                "RETURN i1.name AS ingredient1, " +
                "i2.name AS ingredient2, " +
                "s.benefit AS benefit"
        )
        .bind(ingredient1)
        .to("ingredient1")
        .bind(ingredient2)
        .to("ingredient2")
        .fetch()
        .all();

        List<SynergyInfo> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {

            result.add(
                    new SynergyInfo(
                            (String) row.get("ingredient1"),
                            (String) row.get("ingredient2"),
                            (String) row.get("benefit")
                    )
            );
        }

        return result;
    }

    public List<Map<String, Object>> findConflictDetails(String ingredientName) {
        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (i1:Ingredient {name: $ingredient})-[c:CONFLICTS_WITH]-(i2:Ingredient) " +
                "OPTIONAL MATCH (p:Product)-[:CONTAINS]->(i2) " +
                "RETURN i2.name AS conflictingIngredient, " +
                "c.reason AS reason, " +
                "c.severity AS severity, " +
                "collect(DISTINCT p.name) AS products"
        )
        .bind(ingredientName)
        .to("ingredient")
        .fetch()
        .all();

        return new ArrayList<>(rows);
    }
}