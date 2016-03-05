package com.sproule.individualproject;

public class Image extends Category {
	private String imageText;
	private String uri;
	private String fileName;
	private String fileExtension;
	
	public Image() {
		this.imageText = "Unkown";
		this.uri = "Unknown";
		this.fileName = "Unknown";
		this.fileExtension = "Unknown";
	}
	
	public String getImageText() {
		return imageText;
	}
	
	public void setImageText(String imageText) {
		this.imageText = imageText;
	}
	
	public String getURI() {
		return uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
