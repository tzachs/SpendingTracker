package com.tzachsolomon.spendingtracker;

import android.util.Log;

public final class ClassCommonUtilities {

	public static final String TYPE_REMINDER_TIME_EVERYDAY = "Everyday";
	public static final String TYPE_REMINDER_TIME_WEEKLY = "Weekly";
	public static final String TYPE_REMINDER_TIME_MONTHLY = "Monthly";

	public static final String TYPE_REMINDER_TIME_SUNDAY = "1";
	public static final String TYPE_REMINDER_TIME_MONDAY = "2";
	public static final String TYPE_REMINDER_TIME_TUESDAY = "3";
	public static final String TYPE_REMINDER_TIME_WEDNESDAY = "4";
	public static final String TYPE_REMINDER_TIME_THURSDAY = "5";
	public static final String TYPE_REMINDER_TIME_FRIDAY = "6";
	public static final String TYPE_REMINDER_TIME_SATURDAY = "7";
	public static final String TYPE_REMINDER_TIME_DAY_DONT_CARE = "-1";
	public static final int REQUEST_CODE_ACTIVITY_PREFERENCES = 1000;
	public static final String PREF_KEY_DEBUG_DB = "checkBoxPrefDebugDb";
	public static final String PREF_KEY_DEBUG_ACTIVITY_MAIN = "checkBoxPrefActivityMain";
	public static final String PREF_KEY_DEBUG_FRAGMENT_GENERAL = "checkBoxPrefFragmentGeneral";
	public static final String PREF_KEY_DEBUG_FRAGMENT_REMINDER_TIME = "checkBoxPrefFragmentReminderTime";
	public static final String PREF_KEY_DEBUG_FRAGMENT_REMINDER_LOCATION = "checkBoxPrefFragmentReminderLocation";
	public static final String PREF_KEY_DEBUG_FRAGMENT_ADMIN = "checkBoxPrefFragmentAdmin";
	public static final String PREF_KEY_DEBUG_SERVICE_REMINDER_TIME = "checkBoxPrefServiceReminderTime";
	public static final String PREF_KEY_DEBUG_SERVICE_REMINDER_LOCATION = "checkBoxPrefServiceReminderLocation";
	public static final String NOTIFICATION_ID = "notification_id";

	public static boolean DEBUG_ACTIVITY_MAIN = false;
	public static boolean DEBUG_DB = false;
	public static boolean DEBUG_FRAGMENT_GENERAL = false;
	public static boolean DEBUG_FRAGMENT_REMINDER_TIME = false;
	public static boolean DEBUG_FRAGMENT_REMINDER_LOCATION = false;
	public static boolean DEBUG_SERVICE_REMINDER_LOCATION = false;
	public static boolean DEBUG_SERVICE_REMINDER_TIME = false;
	
	

	public static void DebugDb(String message) {
		if (DEBUG_DB) {
			Log.d("DEBUG_DB", message);
		}
	}

	public static void DebugFragmentGeneral(String message) {
		if (DEBUG_FRAGMENT_GENERAL) {
			Log.d("DEBUG_FRAGMENT_GENERAL", message);
		}
	}

	public static void DebugFragmentReminderTime(String message) {
		if (DEBUG_FRAGMENT_REMINDER_TIME) {
			Log.d("DEBUG_FRAGMENT_REMINDER_TIME", message);
		}
	}

	public static void DebugFragmentReminderLocation(String message) {
		if (DEBUG_FRAGMENT_REMINDER_LOCATION) {
			Log.d("DEBUG_FRAGMENT_REMINDER_LOCATION", message);
		}
	}

	public static void DebugServiceReminderTime(String message) {
		//
		if (DEBUG_SERVICE_REMINDER_TIME) {
			Log.d("DEBUG_SERVICE_REMINDER_TIME", message);
		}

	}

	public static void DebugActivityMain(String message) {
		//
		if (DEBUG_ACTIVITY_MAIN) {
			Log.d("DEBUG_ACTIVITY_MAIN", message);
		}

	}

}
