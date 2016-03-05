package com.sproule.individualproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class NotificationReminderReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences preferences = context.getSharedPreferences("preferences", 0);
		String savedArticleName = preferences.getString("latestArticleName", "Unknown");
		boolean newArticle = false;
		
		if (ArticleManager.isOnline(context)) {
			String articleName = ArticleManager.checkForNewArticles(context);
			Log.i("AppNotification.onStart", "Check for new articles");
			
			if(!savedArticleName.equals(articleName)) {
				newArticle = true;
				savedArticleName = articleName;
			}
		}
		Editor editor = preferences.edit();
		editor.putBoolean("newArticle", newArticle);
		editor.putString("articleName", savedArticleName);
		editor.putLong("timeElapsedInSeconds", preferences.getLong("timeElapsedInSeconds", 0));
		editor.commit();
		
		Intent serviceIntent = new Intent(context, AppNotification.class);
		context.startService(serviceIntent);
	}
}