package com.sproule.individualproject;

import java.util.ArrayList;

public class Article {
	private String articleTitle;
	private ArrayList<String> intro;
	
	public Article() {
		this.articleTitle = "Unknown";
		this.intro = new ArrayList<String>();
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String title) {
		this.articleTitle = title;
	}

	public ArrayList<String> getIntro() {
		return intro;
	}

	public void setIntro(ArrayList<String> intro) {
		this.intro = intro;
	}

	public Integer getNumberOfIntroParagraphs() {
		return intro.size();
	}
	
	public void addIntro(String intro) {
		this.intro.add(intro);
	}
	
	public String getIntro(int index) {
		return intro.get(index);
	}

}
