package com.sproule.individualproject;

import java.util.ArrayList;

public class Category extends Section {
	private String heading;
	private ArrayList<Category> objects;
	
	public Category() {
		this.heading = "Unknown";
		this.objects = new ArrayList<Category>();
	}

	public int getNumberOfObjects() {
		return objects.size();
	}
	
	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}
	
	public ArrayList<Category> getObjects() {
		return objects;
	}
	
	public void setObjects(ArrayList<Category> objects) {
		this.objects = objects;
	}
	
	public Category getObjectInCategory(int index) {
		return objects.get(index);
	}
	
	public void addParagraph(Paragraph paragraph) {
		objects.add(paragraph);
	}
	
	public void addList(List list) {
		objects.add(list);
	}
	
	public void addImage(Image image) {
		objects.add(image);
	}
}