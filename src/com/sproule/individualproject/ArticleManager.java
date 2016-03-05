package com.sproule.individualproject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class ArticleManager {
	protected final static String PATH = Environment.getExternalStorageDirectory() + "/IndividualProject/";
	protected final static String XML = "xml";
	protected final static String IMAGES = "images";
	
	public static void unpackFiles(Context context) throws IOException, XmlPullParserException {
		String[] privateArticles = getFileList("");
		AssetManager assetManager = context.getAssets();
		String[] packagedArticles = null;
		try {
			packagedArticles = assetManager.list(XML);
		} catch (IOException e) {
			Log.e("ArticleManager.filesExist", e.getMessage());
		}
		if (privateArticles.length < packagedArticles.length) {
			for (String fileName : packagedArticles) {
				try {
					InputStream inputStream = assetManager.open(XML + "/" + fileName);
					copyFile(fileName, inputStream);
				} catch (Exception e) {
					Log.e("ArticleManager.filesExist", e.getMessage());
				}
			}
		}
		
		String[] privateImages = getFileList(IMAGES);
		String[] packagedImages = null;
		try {
			packagedImages = assetManager.list(IMAGES);
		} catch (IOException e) {
			Log.e("ArticleManager.filesExist", e.getMessage());
		}
		if (privateImages.length < packagedImages.length) {
			for (String fileName : packagedImages) {
				try {
					InputStream inputStream = assetManager.open(IMAGES + "/" + fileName);
					copyFile(IMAGES + "/" + fileName, inputStream);
				} catch (Exception e) {
					Log.e("ArticleManager.filesExist", e.getMessage());
				}
			}
		}
	}
	
	public static String[] getFileList(String path) {
		ArrayList<String> filesList = new ArrayList<String>();
		String storageState = Environment.getExternalStorageState(); 
		
		if(storageState.equals(Environment.MEDIA_MOUNTED)){
			File files = new File(PATH + path);
			if (!files.exists()) {
				files.mkdirs();
			}
			
			File[] filesArray = files.listFiles();
			for (int uu = 0; uu < filesArray.length; uu++) {
				if (filesArray[uu].isFile()) {
					String fileName = filesArray[uu].getName();
					filesList.add(fileName);
				}
			}
		}
		String[] filesArray = filesList.toArray(new String[0]);
		
		return filesArray;
	}
	
	public static String checkForNewArticles(Context context) {
		Log.i("ArticleManager.checkForNewArticles", "Checking for new articles");
		String articleName = "Unkown Article";
		SharedPreferences preferences = context.getSharedPreferences("preferences", 0);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://uni.britintel.co.uk/articles.php");

		try {
			// Execute HTTP Post Request
			HttpResponse httpResponse = httpClient.execute(httpPost);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent()));

			articleName = rd.readLine();
			Log.i("Main.checkForNewArticle", "HTTPResponse: " + articleName);
			String fileName = stripFileNameOfTags(articleName);
			
			String[] articles = context.fileList();
			
			boolean fileFound = false;
			for (String file : articles) {
				if (file.equals(fileName + "." + XML)) {
					fileFound = true;
				}
			}
			
			if (!fileFound) {
				downloadFile(context, fileName + "." + XML);
				preferences.edit().putLong("timeElapsedInSeconds", 0);
			}

		} catch (ClientProtocolException e) {
			Log.e("ArticleManager.checkForNewArticles", e.getMessage());
		} catch (IOException e) {
			Log.e("ArticleManager.checkForNewArticles", e.getMessage());
		}
		
		preferences.edit().putString("latestArticleName", articleName).commit();
		
		return articleName;
	}
	
	public static void downloadFile(Context context, String uri) {
		try {
			URL url = new URL("http://uni.britintel.co.uk/" + uri);
			URLConnection ucon = url.openConnection();

			InputStream inputStream = ucon.getInputStream();
			
			copyFile(uri, inputStream);
		} catch (IOException e) {
			Log.e("ArticleManager.downloadArticle", e.getMessage());
		}
	}

	public static void copyFile(String uri, InputStream inputStream)
			throws IOException, FileNotFoundException {
		File file = new File(PATH + uri);
		
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int current = 0;
		while ((current = bis.read()) != -1) {
		        baf.append((byte) current);
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(baf.toByteArray());
		fos.close();
	}
	
	public static boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static String[] getArticleTitles(Context context) throws XmlPullParserException,
			IOException {
		String[] articles = getFileList("");
		String[] titles = new String[articles.length];
		int rr = 0;
		boolean titleFound = false;
		for (String file : articles) {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			InputStream inputStream = new FileInputStream(PATH + file);
			xpp.setInput(inputStream, null);
			xpp.next();
			int eventType = xpp.getEventType();
			String tag = "";
			titleFound = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !titleFound) {
				if (eventType == XmlPullParser.START_TAG) {
					tag = xpp.getName();
					eventType = xpp.next();
					if (tag.equalsIgnoreCase("arttitle")) {
						titles[rr] = xpp.getText();
						titleFound = true;
					}
				} else {
					eventType = xpp.next();
				}
			}
			rr++;
		}
		return titles;
	}

	public static boolean checkFileExists(String uri) {
		File file = new File(uri);
		
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String stripFileNameOfTags(String fileName) {
		fileName = fileName.replace(" ", "");
		fileName = fileName.replace("(", "");
		fileName = fileName.replace(")", "");
		fileName = fileName.replace("-", "");
		fileName = fileName.replace("_", "");
		fileName = fileName.toLowerCase();
		return fileName;
	}
}
