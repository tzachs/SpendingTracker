package com.tzachsolomon.spendingtracker;

import android.util.Log;

public final class ClassCommonUtilities {
	
	public static boolean DEBUG_DB = false;
	
	public static void DebugDb (String tag, String message){
		if ( DEBUG_DB){
			Log.d(tag, message);
		}
	}

}
