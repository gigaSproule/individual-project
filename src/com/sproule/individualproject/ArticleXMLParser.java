package com.sproule.individualproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class ArticleXMLParser {
	
	public static ArrayList<Article> getArticleFromXML(String articleFile, Context context)
			throws XmlPullParserException, IOException {
		ArrayList<Article> articleArray = new ArrayList<Article>();
		Article article = new Article();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		FileInputStream inputStream = new FileInputStream(ArticleManager.PATH + articleFile);
		Log.i("ArticleXMLParser.getArticleFromXML", ArticleManager.PATH + articleFile);
		xpp.setInput(inputStream, null);
		xpp.next();
		int eventType = xpp.getEventType();
		String tag = "";

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				tag = xpp.getName();
				eventType = xpp.next();

				if (tag.equalsIgnoreCase("arttitle")) {
					Log.i("Articles.getArticleFromXML", "Parsing " + tag);
					article.setArticleTitle(xpp.getText());
				} else if (tag.equalsIgnoreCase("intro")) {
					Log.i("Articles.getArticleFromXML", "Parsing " + tag);
					article.addIntro(xpp.getText());
				} else if (tag.equalsIgnoreCase("sec")) {
					Log.i("Articles.getArticleFromXML", "Parsing " + tag);
					Log.d("Articles.getArticleFromXML", "New Section");
					Section section = new Section();
					boolean gettingSection = true;
					while (gettingSection) {
						if (eventType == XmlPullParser.START_TAG) {
							tag = xpp.getName();
							Log.i("Articles.getArticleFromXML", "Parsing " + tag);
							if (tag.equalsIgnoreCase("title")) {
								eventType = xpp.next();
								Log.i("Articles.getArticleFromXML", "Parsing " + tag);
								section.setTitle(xpp.getText());
							} else if (tag.equalsIgnoreCase("cat")) {
								eventType = xpp.next();
								Log.i("Articles.getArticleFromXML", "Parsing " + tag);
								Category category = new Category();
								boolean gettingCategory = true;
								while (gettingCategory) {
									if (eventType == XmlPullParser.START_TAG) {
										tag = xpp.getName();
										if (tag.equalsIgnoreCase("head")) {
											eventType = xpp.next();
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											category.setHeading(xpp.getText());
										} else if (tag.equalsIgnoreCase("para")) {
											eventType = xpp.next();
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											Paragraph paragraph = new Paragraph();
											paragraph.setText(xpp.getText());
											category.addParagraph(paragraph);
										} else if(tag.equals("list")) {
											eventType = xpp.next();
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											List list = new List();
											boolean gettingList = true;
											while (gettingList) {
												if (eventType == XmlPullParser.START_TAG) {
													tag = xpp.getName();
													if (tag.equalsIgnoreCase("item")) {
														eventType = xpp.next();
														Log.i("Articles.getArticleFromXML", "Parsing " + tag);
														Item item = new Item();
														item.setItem(xpp.getText());
														list.addItemToList(item);
													} else if (tag.equalsIgnoreCase("subItem")) {
														eventType = xpp.next();
														Log.i("Articles.getArticleFromXML", "Parsing " + tag);
														SubItem subItem = new SubItem();
														subItem.setSubItem(xpp.getText());
														list.addSubItemToList(subItem);
													}
												} else if (eventType == XmlPullParser.END_TAG) {
													if (xpp.getName().equalsIgnoreCase("list")) {
														Log.i("Articles.getArticleFromXML", "Finished parsing " + tag);
														gettingList = false;
													} else {
														eventType = xpp.next();
													}
												} else {
													eventType = xpp.next();
												}
											}
											category.addList(list);
										} else if (tag.equalsIgnoreCase("img")) {
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											Image image = new Image();

											String uri = xpp.getAttributeValue(null, "uri");
											Log.i("Articles.getArticleFromXML", "URI: " + uri);
											image.setURI(uri);
														
											String[] file = uri.split("\\.");
											String fileExtension = file[file.length-1];
											String[] fileName = file[0].split("/");
											image.setFileExtension(fileExtension);
											image.setFileName(fileName[fileName.length-1]);
											
											String imageText = xpp.getAttributeValue(null, "text");
											image.setImageText(imageText);
											
											if(!ArticleManager.checkFileExists(ArticleManager.PATH + image.getURI())) {
												ArticleManager.downloadFile(context, image.getURI());
											}

											category.addImage(image);
											Log.d("Articles.getArticleFromXML", "Number of objects in section: " + ((Integer)section.getAllObjectsInSection().size()).toString());
											
											eventType = xpp.next();
										}
									} else if (eventType == XmlPullParser.END_TAG) {
										if (xpp.getName().equalsIgnoreCase("cat")) {
											Log.i("Articles.getArticleFromXML", "Finished parsing " + tag);
											gettingCategory = false;
										} else {
											eventType = xpp.next();
										}
									} else {
										eventType = xpp.next();
									}
								}

								section.addCategory(category);
								Log.d("Articles.getArticlesFromXML", "Number of objects in section - Category: " + ((Integer)section.getAllObjectsInSection().size()).toString());
							} else if (tag.equalsIgnoreCase("table")) {
								eventType = xpp.next();
								Log.i("Articles.getArticleFromXML", "Parsing " + tag);
								Table table = new Table();
								boolean gettingTable = true;
								int numberOfHeaders = 0;
								int numberOfContents = 0;
								while (gettingTable) {
									if (eventType == XmlPullParser.START_TAG) {
										tag = xpp.getName();
										Log.i("Articles.getArticleFromXML", "Parsing " + tag);
										eventType = xpp.next();
										if (tag.equalsIgnoreCase("th")) {
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											table.addHeader(xpp.getText());
											numberOfHeaders++;
										} else if (tag.equalsIgnoreCase("tc")) {
											Log.i("Articles.getArticleFromXML", "Parsing " + tag);
											table.addContent(xpp.getText());
											numberOfContents++;
										}
									} else if (eventType == XmlPullParser.END_TAG) {
										if (xpp.getName().equalsIgnoreCase("table")) {
											Log.i("Articles.getArticleFromXML", "Finished parsing " + tag);
											gettingTable = false;
										} else {
											eventType = xpp.next();
										}
									} else {
										eventType = xpp.next();
									}
								}

								int numberOfRows = (numberOfContents / numberOfHeaders) + 1;
								
								table.setNumberOfHeaders(numberOfHeaders);
								table.setNumberOfRows(numberOfRows);

								section.addTable(table);
								Log.d("Articles.getArticleFromXML", "Number of objects in section: " + ((Integer)section.getAllObjectsInSection().size()).toString());
							}
						} else if (eventType == XmlPullParser.END_TAG) {
							if (xpp.getName().equalsIgnoreCase("sec")) {
								Log.i("Articles.getArticleFromXML", "Finished parsing " + tag);
								gettingSection = false;
							} else {
								eventType = xpp.next();
							}
						} else {
							eventType = xpp.next();
						}
					}

					articleArray.add(section);
				}
			} else {
				eventType = xpp.next();
			}
		}

		articleArray.add(article);

		return articleArray;
	}
}
