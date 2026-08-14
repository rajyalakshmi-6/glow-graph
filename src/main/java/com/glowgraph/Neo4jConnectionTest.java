package com.glowgraph;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jConnectionTest {

    public static void main(String[] args) {

        Driver driver = GraphDatabase.driver(
                "bolt+s://db-40e15442.databases.cognodb.com",
                AuthTokens.basic(
                        "cognodb",
                        "aa6c02595238b90674c4f2c6f699f2a8"
                )
        );

        try {
            var result = driver.executableQuery("RETURN 1 AS result")
                    .execute();

            System.out.println(
                    "Neo4j connection successful: " +
                    result.records().get(0).get("result").asInt()
            );

        } finally {
            driver.close();
        }
    }
}