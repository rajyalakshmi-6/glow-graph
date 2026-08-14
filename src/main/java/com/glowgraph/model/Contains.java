package com.glowgraph.model;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties 
public class Contains {
	@RelationshipId 
	private String id; 
	private String concentration; 
	@TargetNode 
	private Ingredient ingredient; 
	public Contains() 
	{

	} 
	public Contains(String concentration, Ingredient ingredient) { 
		this.concentration = concentration; 
		this.ingredient = ingredient; 
	} 
	public String getConcentration() { 
		return concentration;
	} 
	public Ingredient getIngredient() 
	{ 
		return ingredient; 
	}


}
