package com.sproule.individualproject;

import java.util.ArrayList;

public class List extends Category {
	private ArrayList<List> listObjects;
	
	public List() {
		this.listObjects = new ArrayList<List>();
	}
	
	public ArrayList<List> getListObjects() {
		return listObjects;
	}
	
	public void setListObjects(ArrayList<List> listObjects) {
		this.listObjects = listObjects;
	}
	
	public int getNumberOfItems() {
		return listObjects.size();
	}
	
	public List getListObject(int index) {
		return listObjects.get(index);
	}
	
	public void addItemToList(Item item) {
		listObjects.add(item);
	}
	
	public void addSubItemToList(SubItem subItem) {
		listObjects.add(subItem);
	}
}