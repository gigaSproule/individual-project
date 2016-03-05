package com.sproule.individualproject;

public class Paragraph extends Category {
	private String text;
	
	public Paragraph() {
		this.text = "Unknown";
	}
	
	public String getParagraphText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}