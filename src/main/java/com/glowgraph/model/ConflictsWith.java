package com.glowgraph.model; 
import org.springframework.data.neo4j.core.schema.*; 

@RelationshipProperties 
public class ConflictsWith { 
	@RelationshipId 
	private String id; 
	private String reason;
	private String severity; 
	@TargetNode 
	private Ingredient conflictingIngredient; 
	public ConflictsWith() 
	{

	} 
	public ConflictsWith(String reason, String severity, Ingredient conflictingIngredient) { 
		this.reason = reason; 
		this.severity = severity; 
		this.conflictingIngredient = conflictingIngredient; 
	} 
	public String getReason() { 
		return reason; 
	} 
	public String getSeverity() { 
		return severity; 
	} 
	public Ingredient getConflictingIngredient() { 
		return conflictingIngredient; 
	} 
}
