package com.tzachsolomon.spendingtracker;

import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.util.Log;

public class SpendingTrackerTimeService extends Service {

	private static final String TAG = SpendingTrackerTimeService.class
			.getSimpleName();

	private Calendar m_Calendar;
	private Intent m_Intent;
	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;

	NotificationManager m_NotificationManager;
	Notification m_Notification;
	
	@Override
	public void onCreate() {
		//
		super.onCreate();

		Log.i(TAG, "Service onCreate");

		initializeVariables();

		m_Calendar.setTimeInMillis(System.currentTimeMillis());
		
		checkReminders();
		
		this.stopSelf();

	}

	@Override
	public void onStart(Intent intent, int startId) {
		//
		Log.i(TAG, "Service onStart");
		

		super.onStart(intent, startId);
	}
	private void initializeVariables() {
		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);
		m_Calendar = Calendar.getInstance();

		m_NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		//
		super.onDestroy();

		
		Log.i(TAG, "Destroying service");

	}

	
	private void checkReminders() {

		try {
			Log.i(TAG, "checkReminders started");
			// Getting reminders from the database
			String[][] reminders = m_SpendingTrackerDbEngine.getTimeReminders();
			int i = 0;
			int length = reminders.length;
			boolean flag = false;

			m_Calendar.setTimeInMillis(System.currentTimeMillis());

			Log.i(TAG, "Looping over " + Integer.toString(length)
					+ " reminders");
			// going over the reminders
			for (i = 0; i < length; i++) {
				flag = false;
				Log.v(TAG, String.format("Reminder %s type %s at %s:%s amount %s",
						reminders[i][0],reminders[i][1],reminders[i][2],reminders[i][3],
						reminders[i][4]));
						                    
						
				// checking if the reminder is the current hour and minute
				if (m_Calendar.get(Calendar.HOUR_OF_DAY) == Integer
						.parseInt(reminders[i][2])
						&& m_Calendar.get(Calendar.MINUTE) == Integer
								.parseInt(reminders[i][3])) {

					// checking if the reminder is of type everyday
					if (reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY)) {
						flag = true;
						// checking if the reminder is of type weekly
					} else if (reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY)) {
						// reminder is of type weekly, checking if day in week
						// match
						if (reminders[i][4]
								.contentEquals(Integer.toString((m_Calendar
										.get(Calendar.DAY_OF_WEEK))))) {
							flag = true;
						}

					} else if (reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY)) {
						if (reminders[i][4].contentEquals(Integer
								.toString((m_Calendar
										.get(Calendar.DAY_OF_MONTH))))) {
							flag = true;
						}

					}

				}

				// flag equals true then we need to start the program
				if (flag) {
					Bundle extras = new Bundle();

					m_Intent = new Intent(getBaseContext(),
							ActivitySpendingTracker.class);

					Log.i(TAG, "Found reminder in database, sending notification");

					// When activity will start up, it needs to check if the
					// first
					// extra
					// is either
					// SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY
					// SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY
					// SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY
					
					

					extras.putBoolean(reminders[i][1], true);
					extras.putString(SpendingTrackerDbEngine.KEY_AMOUNT,
							reminders[i][5]);
					extras.putString(SpendingTrackerDbEngine.KEY_CATEGORY,
							reminders[i][6]);
					
					m_Intent.putExtras(extras);

					m_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					m_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					// Instead of starting the application start notification
					
						boolean enableVibrate = PreferenceManager
								.getDefaultSharedPreferences(this).getBoolean(
										"cbNotifictionVibrate", true);
						boolean enableSound = PreferenceManager
								.getDefaultSharedPreferences(this).getBoolean(
										"cbNotifictionSound", true);
						boolean enableLights = PreferenceManager
								.getDefaultSharedPreferences(this).getBoolean(
										"cbNotifictionLights", true);

						StringBuilder sb = new StringBuilder();

						sb.append(getString(R.string.stringDidYouSpend));
						sb.append(' ');
						sb.append(reminders[i][5]);
						sb.append(' ');
						try {
							sb.append(Currency.getInstance(Locale.getDefault())
									.getSymbol());
						} catch (Exception e) {
							// TODO: m_Debug option 
							Log.d(TAG, e.getMessage().toString());
							
						}
						
						sb.append(getString(R.string.stringDidYouSpend));
						sb.append(' ');
						sb.append(reminders[i][6]);
						sb.append(' ');
						sb.append("?");

						m_Notification = new Notification(R.drawable.notify,
								"Spending notification",
								System.currentTimeMillis());

						PendingIntent contentIntent = PendingIntent
								.getActivity(this, 0, m_Intent, PendingIntent.FLAG_UPDATE_CURRENT);

						m_Notification.setLatestEventInfo(
								getApplicationContext(),
								getString(R.string.app_name), sb.toString(),
								contentIntent);
						if (enableLights) {
							m_Notification.defaults |= Notification.DEFAULT_LIGHTS;
						}
						if (enableSound) {
							m_Notification.defaults |= Notification.DEFAULT_SOUND;
						}
						if (enableVibrate) {
							m_Notification.defaults |= Notification.DEFAULT_VIBRATE;
						}
						

						m_NotificationManager.notify(12021982, m_Notification);
						
				}

			}
		} catch (Exception e) {
			// 
			Log.d(TAG, e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// 
		return null;
	}
}