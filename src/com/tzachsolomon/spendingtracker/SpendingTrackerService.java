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

public class SpendingTrackerService extends Service {

	private static final String TAG = SpendingTrackerService.class
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
			String[][] m_Reminders = m_SpendingTrackerDbEngine.getReminders();
			int i = 0;
			int length = m_Reminders.length;
			boolean flag = false;

			m_Calendar.setTimeInMillis(System.currentTimeMillis());

			Log.i(TAG, "Looping over " + Integer.toString(length)
					+ " reminders");
			// going over the reminders
			for (i = 0; i < length; i++) {
				flag = false;
				Log.v(TAG, String.format("Reminder %s type %s at %s:%s amount %s",
						m_Reminders[i][0],m_Reminders[i][1],m_Reminders[i][2],m_Reminders[i][3],
						m_Reminders[i][4]));
						                    
						
				// checking if the reminder is the current hour and minute
				if (m_Calendar.get(Calendar.HOUR_OF_DAY) == Integer
						.parseInt(m_Reminders[i][2])
						&& m_Calendar.get(Calendar.MINUTE) == Integer
								.parseInt(m_Reminders[i][3])) {

					// checking if the reminder is of type everyday
					if (m_Reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY)) {
						flag = true;
						// checking if the reminder is of type weekly
					} else if (m_Reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY)) {
						// reminder is of type weekly, checking if day in week
						// match
						if (m_Reminders[i][4]
								.contentEquals(Integer.toString((m_Calendar
										.get(Calendar.DAY_OF_WEEK))))) {
							flag = true;
						}

					} else if (m_Reminders[i][1]
							.contentEquals(SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY)) {
						if (m_Reminders[i][4].contentEquals(Integer
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
							SpendingTrackerActivity.class);

					Log.i(TAG, "Found reminder in database, sending notification");

					// When activity will start up, it needs to check if the
					// first
					// extra
					// is either
					// SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY
					// SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY
					// SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY
					
					

					extras.putBoolean(m_Reminders[i][1], true);
					extras.putString(SpendingTrackerDbEngine.KEY_AMOUNT,
							m_Reminders[i][5]);
					extras.putString(SpendingTrackerDbEngine.KEY_CATEGORY,
							m_Reminders[i][6]);
					
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
						sb.append(m_Reminders[i][5]);
						sb.append(' ');
						sb.append(Currency.getInstance(Locale.getDefault())
								.getSymbol());
						sb.append(' ');
						sb.append(getString(R.string.stringDidYouSpendOn));
						sb.append(' ');
						sb.append(m_Reminders[i][6]);
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
