package com.tzachsolomon.spendingtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	
	private PreferenceCategory	preferenceCategoryLocationAdvanced;
	private SharedPreferences m_SharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		m_SharedPreferences = PreferenceManager
			.getDefaultSharedPreferences(getBaseContext());
		
		preferenceCategoryLocationAdvanced = (PreferenceCategory)findPreference("preferenceCategoryLocationAdvanced");
		
		preferenceCategoryLocationAdvanced.setEnabled(m_SharedPreferences.getBoolean("checkBoxPreferencesLocationService", false));
		
		
	}
	
	@Override
	protected void onResume() {
		// 
		super.onResume();
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
	}
	
	@Override
	protected void onPause() {
		// 
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// 
		boolean answer;
		StringBuilder sb = new StringBuilder();
		
		if ( key.contentEquals("checkBoxPreferencsReminderService")){
			if ( sharedPreferences.getBoolean("checkBoxPreferencsReminderService", true) ) {
				sb.append(getString(R.string.toastMessageEnabledTimeReminderService));
			} else {
				sb.append(getString(R.string.toastMessageDisabledTimeReminderService));
			}
		}else if (key.contentEquals("checkBoxPreferencesLocationService")) {
			
			answer = sharedPreferences.getBoolean("checkBoxPreferencesLocationService", false);
			
			preferenceCategoryLocationAdvanced.setEnabled(answer);
			
			if ( answer) {
				sb.append(getString(R.string.toastMessageEnabledLocationReminderService));
				
			}else {
				sb.append(getString(R.string.toastMessageDisabledLocationReminderService));
			}
				
		}
		
		if ( sb.length() > 0 ){
		
			Toast.makeText(this, sb.toString(),	Toast.LENGTH_SHORT).show();
		}
		
		sb.setLength(0);
		
		
	}

}
