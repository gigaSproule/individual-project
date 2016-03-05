package com.sproule.individualproject;

import java.util.ArrayList;

public class Section extends Article {
	private String title;
	private ArrayList<Section> objectsInSection;
	
	public Section() {
		this.title = "Unknown";
		this.objectsInSection = new ArrayList<Section>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumberOfObjectsInSection() {
		return objectsInSection.size();
	}

	public ArrayList<Section> getAllObjectsInSection() {
		return objectsInSection;
	}

	public void setObjectsInSection(ArrayList<Section> objectsInSection) {
		this.objectsInSection = objectsInSection;
	}
	
	public Section getObjectInSection(int index) {
		return this.objectsInSection.get(index);
	}
	
	public void addCategory(Category category) {
		this.objectsInSection.add(category);
	}
	
	public void addTable(Table table) {
		this.objectsInSection.add(table);
	}
}
