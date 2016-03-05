package com.sproule.individualproject;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;


public class AppNotification extends Service {
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		SharedPreferences preferences = getSharedPreferences("preferences", 0);
		
		boolean newArticle = preferences.getBoolean("newArticle", false);
		String articleName = preferences.getString("articleName", "Unknown Article");
		long timeElapsed = preferences.getLong("timeElapsedInSeconds", 0);
		
		Calendar calendarForLog = Calendar.getInstance();
		Log.i("AppNotification.onStart", "Create the notification at " + calendarForLog.get(Calendar.HOUR_OF_DAY) + ":"
				 + calendarForLog.get(Calendar.MINUTE) + ":" + calendarForLog.get(Calendar.SECOND));
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Individual Project";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = articleName;
		
		CharSequence contentText = "";
		if (newArticle) {
			contentText = "There is a new article for you to read";
		} else {
			contentText = "You still have " + ((3600 - timeElapsed)/60) + " minutes left on this article";
		}
		Intent notificationIntent = new Intent(this, Articles.class);
		articleName = ArticleManager.stripFileNameOfTags(articleName);
		notificationIntent.putExtra("articleFile", articleName + ".xml");
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		final int NOTIFICATION_ID = 1;
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
