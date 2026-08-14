package com.glowgraph;

import com.glowgraph.repository.IngredientRepository;
import com.glowgraph.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

@Component
public class SeedDataRunner implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final Neo4jClient neo4jClient;

    public SeedDataRunner(
            IngredientRepository ingredientRepository,
            ProductRepository productRepository,
            Neo4jClient neo4jClient
    ) {
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.neo4jClient = neo4jClient;
    }

    @Override
    public void run(String... args) {

        if (args.length == 0 || !args[0].equals("seed")) {
            return;
        }

        System.out.println("Seeding started...");

        seedIngredients();
        seedConflicts();
        seedSynergies();
        seedConcerns();
        seedProducts();

        System.out.println("Seeding complete.");
    }

//    @Override
//    public void run(String... args) {
//
//        System.out.println("Seeding skipped temporarily.");
//        return;
//    }
    // ---------------------------------------------------------
    // 1. Seed Ingredients
    // ---------------------------------------------------------

    private void seedIngredients() {

        ingredientRepository.save(
                "Retinol",
                "Anti-aging",
                2
        );

        ingredientRepository.save(
                "Vitamin C",
                "Brightening",
                1
        );

        ingredientRepository.save(
                "Niacinamide",
                "Barrier repair & brightening",
                0
        );

        ingredientRepository.save(
                "Hyaluronic Acid",
                "Hydration",
                0
        );

        ingredientRepository.save(
                "Salicylic Acid",
                "Exfoliant / Acne treatment",
                1
        );

        ingredientRepository.save(
                "Benzoyl Peroxide",
                "Acne treatment",
                2
        );

        ingredientRepository.save(
                "Glycolic Acid",
                "Exfoliant",
                2
        );

        ingredientRepository.save(
                "Vitamin E",
                "Antioxidant",
                1
        );

        ingredientRepository.save(
                "Ceramides",
                "Barrier repair",
                0
        );

        ingredientRepository.save(
                "Zinc Oxide",
                "Sun protection & oil control",
                0
        );

        ingredientRepository.save(
                "Azelaic Acid",
                "Acne & brightening",
                0
        );

        ingredientRepository.save(
                "Squalane",
                "Moisturizing",
                0
        );

        System.out.println("Ingredients seeded.");
    }

    // ---------------------------------------------------------
    // 2. Seed Conflicts
    // ---------------------------------------------------------

    private void seedConflicts() {

        createConflict(
                "Retinol",
                "Vitamin C",
                "Different pH requirements cause instability and reduced efficacy when layered",
                "medium"
        );

        createConflict(
                "Retinol",
                "Benzoyl Peroxide",
                "Benzoyl peroxide oxidizes and deactivates retinol",
                "high"
        );

        createConflict(
                "Retinol",
                "Glycolic Acid",
                "Combined exfoliation and cell turnover increases irritation risk",
                "high"
        );

        createConflict(
                "Benzoyl Peroxide",
                "Vitamin C",
                "Benzoyl peroxide oxidizes vitamin C, reducing its effectiveness",
                "medium"
        );

        createConflict(
                "Salicylic Acid",
                "Retinol",
                "Both increase cell turnover; combined use raises irritation risk",
                "medium"
        );

        createConflict(
                "Benzoyl Peroxide",
                "Salicylic Acid",
                "Combined use can over-dry and compromise the skin barrier",
                "low"
        );

        System.out.println("Conflicts seeded.");
    }

    private void createConflict(
            String a,
            String b,
            String reason,
            String severity
    ) {

        neo4jClient.query(
                "MATCH (i1:Ingredient {name: $a}), " +
                "(i2:Ingredient {name: $b}) " +
                "MERGE (i1)-[r:CONFLICTS_WITH]->(i2) " +
                "SET r.reason = $reason, " +
                "r.severity = $severity"
        )
        .bind(a)
        .to("a")
        .bind(b)
        .to("b")
        .bind(reason)
        .to("reason")
        .bind(severity)
        .to("severity")
        .run();
    }

    // ---------------------------------------------------------
    // 3. Seed Synergies
    // ---------------------------------------------------------

    private void seedSynergies() {

        createSynergy(
                "Niacinamide",
                "Hyaluronic Acid",
                "Hydrates while strengthening the skin barrier"
        );

        createSynergy(
                "Vitamin C",
                "Vitamin E",
                "Vitamin E stabilizes vitamin C and boosts antioxidant protection"
        );

        createSynergy(
                "Retinol",
                "Hyaluronic Acid",
                "Hyaluronic acid offsets retinol's dryness for better tolerance"
        );

        createSynergy(
                "Niacinamide",
                "Zinc Oxide",
                "Supports oil control and barrier function together"
        );

        createSynergy(
                "Ceramides",
                "Hyaluronic Acid",
                "Combined hydration and barrier repair"
        );

        createSynergy(
                "Azelaic Acid",
                "Niacinamide",
                "Both calm inflammation and even skin tone"
        );

        createSynergy(
                "Salicylic Acid",
                "Niacinamide",
                "Niacinamide reduces irritation from BHA exfoliation"
        );

        System.out.println("Synergies seeded.");
    }

    private void createSynergy(
            String a,
            String b,
            String benefit
    ) {

        neo4jClient.query(
                "MATCH (i1:Ingredient {name: $a}), " +
                "(i2:Ingredient {name: $b}) " +
                "MERGE (i1)-[r:PAIRS_WELL_WITH]->(i2) " +
                "SET r.benefit = $benefit"
        )
        .bind(a)
        .to("a")
        .bind(b)
        .to("b")
        .bind(benefit)
        .to("benefit")
        .run();
    }

    // ---------------------------------------------------------
    // 4. Seed Concerns
    // ---------------------------------------------------------

    private void seedConcerns() {

        createTargets("Retinol", "Aging");

        createTargets("Vitamin C", "Hyperpigmentation");

        createTargets("Azelaic Acid", "Hyperpigmentation");

        createTargets("Niacinamide", "Hyperpigmentation");

        createTargets("Salicylic Acid", "Acne");

        createTargets("Benzoyl Peroxide", "Acne");

        createTargets("Azelaic Acid", "Acne");

        createTargets("Hyaluronic Acid", "Dryness");

        createTargets("Ceramides", "Dryness");

        createTargets("Squalane", "Dryness");

        System.out.println("Concerns seeded.");
    }

    private void createTargets(
            String ingredient,
            String concern
    ) {

        neo4jClient.query(
                "MERGE (c:Concern {name: $concern}) " +
                "WITH c " +
                "MATCH (i:Ingredient {name: $ingredient}) " +
                "MERGE (i)-[:TARGETS]->(c)"
        )
        .bind(ingredient)
        .to("ingredient")
        .bind(concern)
        .to("concern")
        .run();
    }

    // ---------------------------------------------------------
    // 5. Seed Products
    // ---------------------------------------------------------

    private void seedProducts() {

        createProductWithIngredients(
                "CeraVe Foaming Facial Cleanser",
                "CeraVe",
                "Cleanser",
                899.0,
                "cerave-cleanser.jpg",
                "Ceramides",
                "Niacinamide"
        );

        createProductWithIngredients(
                "Niacinamide 10% + Zinc 1%",
                "The Ordinary",
                "Serum",
                590.0,
                "ordinary-niacinamide.jpg",
                "Niacinamide",
                "Zinc Oxide"
        );

        createProductWithIngredients(
                "Retinol 0.5% in Squalane",
                "The Ordinary",
                "Serum",
                690.0,
                "ordinary-retinol.jpg",
                "Retinol",
                "Squalane"
        );

        createProductWithIngredients(
                "Vitamin C 16% Serum",
                "Minimalist",
                "Serum",
                599.0,
                "minimalist-vitc.jpg",
                "Vitamin C",
                "Vitamin E"
        );

        createProductWithIngredients(
                "Hydro Boost Water Gel",
                "Neutrogena",
                "Moisturizer",
                711.0,
                "neutrogena-hydroboost.jpg",
                "Hyaluronic Acid"
        );

        createProductWithIngredients(
                "Effaclar Duo",
                "La Roche-Posay",
                "Treatment",
                1450.0,
                "lrp-effaclar.jpg",
                "Salicylic Acid",
                "Niacinamide"
        );

        createProductWithIngredients(
                "Acne Foaming Wash 4%",
                "PanOxyl",
                "Cleanser",
                1200.0,
                "panoxyl-wash.jpg",
                "Benzoyl Peroxide"
        );

        createProductWithIngredients(
                "Azelaic Acid Suspension 10%",
                "The Ordinary",
                "Treatment",
                790.0,
                "ordinary-azelaic.jpg",
                "Azelaic Acid"
        );

        createProductWithIngredients(
                "Moisturizing Cream",
                "Cetaphil",
                "Moisturizer",
                565.0,
                "cetaphil-cream.jpg",
                "Ceramides",
                "Squalane"
        );

        System.out.println("Products seeded.");
    }

    private void createProductWithIngredients(
            String name,
            String brand,
            String category,
            Double price,
            String imageUrl,
            String... ingredients
    ) {

        productRepository.save(
                name,
                brand,
                category,
                price,
                imageUrl
        );

        for (String ingredient : ingredients) {

            neo4jClient.query(
                    "MATCH (p:Product {name: $name}), " +
                    "(i:Ingredient {name: $ingredient}) " +
                    "MERGE (p)-[:CONTAINS]->(i)"
            )
            .bind(name)
            .to("name")
            .bind(ingredient)
            .to("ingredient")
            .run();
        }
    }
}