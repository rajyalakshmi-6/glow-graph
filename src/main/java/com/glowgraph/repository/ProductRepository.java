package com.glowgraph.repository;

import com.glowgraph.model.Product;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepository {

    private final Neo4jClient neo4jClient;

    public ProductRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Product save(
            String name,
            String brand,
            String category,
            Double price,
            String imageUrl
    ) {

        Map<String, Object> row = neo4jClient.query(
                "MERGE (p:Product {name: $name}) " +
                "SET p.brand = $brand, " +
                "p.category = $category, " +
                "p.price = $price, " +
                "p.imageUrl = $imageUrl " +
                "RETURN p.name AS name, " +
                "p.brand AS brand, " +
                "p.category AS category, " +
                "p.price AS price, " +
                "p.imageUrl AS imageUrl"
        )
        .bind(name)
        .to("name")
        .bind(brand)
        .to("brand")
        .bind(category)
        .to("category")
        .bind(price)
        .to("price")
        .bind(imageUrl)
        .to("imageUrl")
        .fetch()
        .one()
        .orElseThrow();

        return mapToProduct(row);
    }

    public List<Product> findAll() {

        Collection<Map<String, Object>> rows = neo4jClient.query(
                "MATCH (p:Product) " +
                "RETURN p.name AS name, " +
                "p.brand AS brand, " +
                "p.category AS category, " +
                "p.price AS price, " +
                "p.imageUrl AS imageUrl"
        )
        .fetch()
        .all();

        List<Product> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            result.add(mapToProduct(row));
        }

        return result;
    }

    private Product mapToProduct(Map<String, Object> row) {

        Double price = row.get("price") != null
                ? ((Number) row.get("price")).doubleValue()
                : null;

        return new Product(
                (String) row.get("name"),
                (String) row.get("brand"),
                (String) row.get("category"),
                price,
                (String) row.get("imageUrl")
        );
    }
}