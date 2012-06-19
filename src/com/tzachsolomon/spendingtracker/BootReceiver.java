package com.tzachsolomon.spendingtracker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences m_SharedPreferences;
	private PendingIntent m_TimeAlarmSender;

	@Override
	public void onReceive(Context context, Intent intent) {
		//
		if (m_SharedPreferences.getBoolean("checkBoxPreferencsReminderService",
				true)) {

			m_TimeAlarmSender = PendingIntent.getService(context, 0,
					new Intent(context, SpendingTrackerTimeService.class), 0);

			
			Calendar firstTime = Calendar.getInstance();
			firstTime.setTimeInMillis(SystemClock.elapsedRealtime());

			int secondsToAdd = 60 - firstTime.get(Calendar.SECOND);

			firstTime.add(Calendar.SECOND, secondsToAdd);

			// Schedule the alarm!
			AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
					firstTime.getTimeInMillis(), 60000, m_TimeAlarmSender);
			

		}

	}

	

}
