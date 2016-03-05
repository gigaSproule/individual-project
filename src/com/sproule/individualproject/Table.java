package com.sproule.individualproject;

import java.util.ArrayList;

public class Table extends Section {
	private int numberOfHeaders;
	private int numberOfRows;
	private String tableTitle;
	private ArrayList<String> headers;
	private ArrayList<String> content;
	
	public Table() {
		this.numberOfHeaders = 0;
		this.numberOfRows = 0;
		this.tableTitle = "Unknown";
		this.headers = new ArrayList<String>();
		this.content = new ArrayList<String>();
	}

	public int getNumberOfHeaders() {
		return numberOfHeaders;
	}

	public void setNumberOfHeaders(int numberOfHeaders) {
		this.numberOfHeaders = numberOfHeaders;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
	
	public String getTableTitle() {
		return tableTitle;
	}
	
	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public ArrayList<String> getAllHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}

	public ArrayList<String> getAllContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}
	
	public void addHeader(String header) {
		this.headers.add(header);
	}
	
	public void addContent(String content) {
		this.content.add(content);
	}
	
	public String getHeader(int index) {
		return headers.get(index);
	}
	
	public String getContent(int index) {
		return content.get(index);
	}

}
