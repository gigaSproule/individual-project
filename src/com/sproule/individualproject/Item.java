package com.sproule.individualproject;

public class Item extends List {
	private String item;
	
	public Item() {
		item = "Unknown";
	}
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
}
