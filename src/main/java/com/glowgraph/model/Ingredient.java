package com.glowgraph.model;
import java.util.List; 
import org.springframework.data.neo4j.core.schema.*;

import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.data.neo4j.core.schema.*;
@Node
public class Ingredient { 
	@Id @GeneratedValue(UUIDStringGenerator.class)
	 private String id;
	private String name; 
	private String function; 
	private Integer comedogenicRating; 
	public Ingredient() 
	{

	}
	public Ingredient(String name, String function, Integer comedogenicRating) 
	{ 
		this.name = name; 
		this.function = function; 
		this.comedogenicRating = comedogenicRating; 
	} 
	public String  getId() 
	{ 
		return id; 
	} 
	public String getName() 
	{ 
		return name; 
	} 
	public void setName(String name) 
	{ 
		this.name = name; 
	} 
	public String getFunction() 
	{ 
		return function; 
	} 
	public void setFunction(String function) { 
		this.function = function; 
	} 
	public Integer getComedogenicRating() { 
		return comedogenicRating; 
	} 
	public void setComedogenicRating(Integer r) { 
		this.comedogenicRating = r; 
	} 

	@Relationship(type = "CONFLICTS_WITH", direction = Relationship.Direction.OUTGOING)
	private List<ConflictsWith> conflicts; 
	public List<ConflictsWith> getConflicts() 
	{
		return conflicts; 
	}
	public void setConflicts(List<ConflictsWith> conflicts) { 
		this.conflicts = conflicts; 
	}


	@RelationshipProperties 
	public class PairsWellWith { 
		@RelationshipId 
		private Long id; 
		private String benefit; 
		@TargetNode 
		private Ingredient pairedIngredient; 
		public PairsWellWith() {

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
}