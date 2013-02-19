package com.tzachsolomon.spendingtracker;

import java.util.Calendar;
import static com.tzachsolomon.spendingtracker.ClassCommonUtilities.*;

import java.util.Currency;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.IBinder;

import android.preference.PreferenceManager;

public class SpendingTrackerTimeService extends Service {

	

	private Calendar m_Calendar;
	private Intent m_Intent;
	private ClassDbEngine m_SpendingTrackerDbEngine;

	NotificationManager m_NotificationManager;
	Notification m_Notification;
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate() {
		//
		super.onCreate();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		ClassCommonUtilities.DEBUG_SERVICE_REMINDER_TIME = mSharedPreferences.getBoolean(PREF_KEY_DEBUG_SERVICE_REMINDER_TIME,false);
		
		DebugServiceReminderTime("Time Service onCreate");

		initializeVariables();

		m_Calendar.setTimeInMillis(System.currentTimeMillis());
		
		checkReminders();
		
		this.stopSelf();

	}

	
	private void initializeVariables() {
		m_SpendingTrackerDbEngine = new ClassDbEngine(this);
		m_Calendar = Calendar.getInstance();

		m_NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		//
		super.onDestroy();

		
		DebugServiceReminderTime("Time Destroying service");

	}

	
	private void checkReminders() {

		try {
			DebugServiceReminderTime("checkReminders started");
			// Getting reminders from the database
			String[][] reminders = m_SpendingTrackerDbEngine.getRemindersTimeAsStringMatrix();
			int i = 0;
			int length = reminders.length;
			boolean flag = false;

			m_Calendar.setTimeInMillis(System.currentTimeMillis());

			DebugServiceReminderTime("Looping over " + Integer.toString(length)
					+ " reminders");
			// going over the reminders
			for (i = 0; i < length; i++) {
				flag = false;
				
						                    
						
				// checking if the reminder is the current hour and minute
				if (m_Calendar.get(Calendar.HOUR_OF_DAY) == Integer
						.parseInt(reminders[i][2])
						&& m_Calendar.get(Calendar.MINUTE) == Integer
								.parseInt(reminders[i][3])) {

					// checking if the reminder is of type everyday
					if (reminders[i][1]
							.contentEquals(TYPE_REMINDER_TIME_EVERYDAY)) {
						flag = true;
						// checking if the reminder is of type weekly
					} else if (reminders[i][1]
							.contentEquals(TYPE_REMINDER_TIME_WEEKLY)) {
						// reminder is of type weekly, checking if day in week
						// match
						if (reminders[i][4]
								.contentEquals(Integer.toString((m_Calendar
										.get(Calendar.DAY_OF_WEEK))))) {
							flag = true;
						}

					} else if (reminders[i][1]
							.contentEquals(TYPE_REMINDER_TIME_MONTHLY)) {
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
							ActivityMain1.class);
					DebugServiceReminderTime(String.format("Sending notification for Reminder %s type %s at %s:%s amount %s",
							reminders[i][0],reminders[i][1],reminders[i][2],reminders[i][3],
							reminders[i][4]));

					// When activity will start up, it needs to check if the
					// first
					// extra
					// is either
					// SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY
					// SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY
					// SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY
					
					

					extras.putBoolean(reminders[i][1], true);
					extras.putString(ClassDbEngine.KEY_AMOUNT,
							reminders[i][5]);
					extras.putString(ClassDbEngine.KEY_CATEGORY,
							reminders[i][6]);
					
					int notifyId = (int)(System.currentTimeMillis() / (long)1982);
					
					extras.putInt(ClassCommonUtilities.NOTIFICATION_ID, notifyId);
					
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
							// 
							DebugServiceReminderTime( e.getMessage().toString());
							
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

						m_NotificationManager.notify(notifyId, m_Notification);
						
				}else{
					DebugServiceReminderTime(String.format("Skipping Reminder %s type %s at %s:%s amount %s",
							reminders[i][0],reminders[i][1],reminders[i][2],reminders[i][3],
							reminders[i][4]));
				}

			}
		} catch (Exception e) {
			// 
			DebugServiceReminderTime( e.toString());
			
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// 
		return null;
	}
}
