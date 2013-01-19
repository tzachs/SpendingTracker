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
	
	public static boolean DEBUG_DB = false;
	
	public static void DebugDb (String tag, String message){
		if ( DEBUG_DB){
			Log.d(tag, message);
		}
	}

}
