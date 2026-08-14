package com.glowgraph.model;
import org.springframework.data.neo4j.core.schema.*;
import java.util.List;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node
public class Product {  
	@Id @GeneratedValue(UUIDStringGenerator.class)
	 private String id;
	private String name;
	private String brand; 
	private String category; 
	private Double price; 
	private String imageUrl;
	public Product(String name, String brand, String category, Double price, String imageUrl) {

		this.name = name;
		this.brand = brand;
		this.category = category;
		this.price = price;
		this.imageUrl = imageUrl;
	}
	public Product() {
		super();
	}
	public  String  getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING) 
	private List<Contains> ingredients; 
	public List<Contains> getIngredients() { 
		return ingredients; 
	} 
	public void setIngredients(List<Contains> ingredients) { 
		this.ingredients = ingredients; 
	} 

}
