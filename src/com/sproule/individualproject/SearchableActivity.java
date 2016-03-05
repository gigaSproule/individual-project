package com.sproule.individualproject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class SearchableActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);

	    Intent intent = getIntent();

	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      searchArticles(query);
	    }
	}

	public void searchArticles(String query) {
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);

		query.replace(" ", "_");
		webView.loadUrl("http://en.m.wikipedia.org/wiki/"+query);
	}
}
