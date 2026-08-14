package com.glowgraph.repository;

import com.glowgraph.model.Ingredient;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final Neo4jClient neo4jClient;

    public IngredientRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Ingredient save(String name, String function, Integer rating) {

        Map<String, Object> row = neo4jClient.query(
                "MERGE (i:Ingredient {name: $name}) " +
                "SET i.function = $function, " +
                "i.comedogenicRating = $rating " +
                "RETURN i.name AS name, " +
                "i.function AS function, " +
                "i.comedogenicRating AS comedogenicRating"
        )
        .bind(name)
        .to("name")
        .bind(function)
        .to("function")
        .bind(rating)
        .to("rating")
        .fetch()
        .one()
        .orElseThrow();

        return mapToIngredient(row);
    }

    public List<Ingredient> findAll() {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (i:Ingredient) " +
                "RETURN i.name AS name, " +
                "i.function AS function, " +
                "i.comedogenicRating AS comedogenicRating"
        )
        .fetch()
        .all();

        List<Ingredient> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            result.add(mapToIngredient(row));
        }

        return result;
    }

    public Optional<Ingredient> findByName(String name) {

        return neo4jClient.query(
                "MATCH (i:Ingredient {name: $name}) " +
                "RETURN i.name AS name, " +
                "i.function AS function, " +
                "i.comedogenicRating AS comedogenicRating"
        )
        .bind(name)
        .to("name")
        .fetch()
        .one()
        .map(this::mapToIngredient);
    }

    private Ingredient mapToIngredient(Map<String, Object> row) {

        Integer rating = row.get("comedogenicRating") != null
                ? ((Number) row.get("comedogenicRating")).intValue()
                : null;

        return new Ingredient(
                (String) row.get("name"),
                (String) row.get("function"),
                rating
        );
    }
}