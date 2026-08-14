package com.glowgraph.model;

import org.springframework.data.neo4j.core.schema.*; 
@RelationshipProperties 
public class PairsWellWith { 
	
	@RelationshipId 
	private String id; 
	private String benefit; 
	@TargetNode 
	private Ingredient pairedIngredient; 
	public PairsWellWith() 
	{

	} 
	public PairsWellWith(String benefit, Ingredient pairedIngredient) { 
		this.benefit = benefit; 
		this.pairedIngredient = pairedIngredient; 
	} 
	public String getBenefit() { 
		return benefit; 
	} 
	public Ingredient getPairedIngredient() { 
		return pairedIngredient; 
	} 
}