package com.tzachsolomon.spendingtracker;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class ActivityPreferences extends SherlockPreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private PreferenceCategory preferenceCategoryLocationAdvanced;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);

	}

	@Override
	protected void onResume() {
		//
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@SuppressWarnings("deprecation")
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference != null)
			if (preference instanceof PreferenceScreen)
				if (((PreferenceScreen) preference).getDialog() != null)
					((PreferenceScreen) preference)
							.getDialog()
							.getWindow()
							.getDecorView()
							.setBackgroundDrawable(
									this.getWindow().getDecorView()
											.getBackground().getConstantState()
											.newDrawable());
		return false;
	}

	@Override
	protected void onPause() {
		//
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//
		if (key.contentEquals("prefMondayFirstDayOfWeek")) {
			Editor editor = sharedPreferences.edit();
			if (sharedPreferences.getBoolean("prefMondayFirstDayOfWeek", true)) {
				editor.putBoolean("prefWorkdaySunday", false);
				editor.putBoolean("prefWorkdayMonday", true);
				editor.putBoolean("prefWorkdayTuesday", true);
				editor.putBoolean("prefWorkdayWednesday", true);
				editor.putBoolean("prefWorkdayThursday", true);
				editor.putBoolean("prefWorkdayFriday", true);
				editor.putBoolean("prefWorkdaySaturday", false);

			} else {
				editor.putBoolean("prefWorkdaySunday", true);
				editor.putBoolean("prefWorkdayMonday", true);
				editor.putBoolean("prefWorkdayTuesday", true);
				editor.putBoolean("prefWorkdayWednesday", true);
				editor.putBoolean("prefWorkdayThursday", true);
				editor.putBoolean("prefWorkdayFriday", false);
				editor.putBoolean("prefWorkdaySaturday", false);
			}
			editor.commit();
		}

	}

	// @Override
	// public void onSharedPreferenceChanged(SharedPreferences
	// sharedPreferences,
	// String key) {
	// //
	// boolean answer;
	// StringBuilder sb = new StringBuilder();
	//
	// if ( key.contentEquals("checkBoxPreferencsReminderService")){
	// if ( sharedPreferences.getBoolean("checkBoxPreferencsReminderService",
	// true) ) {
	// sb.append(getString(R.string.toastMessageEnabledTimeReminderService));
	// } else {
	// sb.append(getString(R.string.toastMessageDisabledTimeReminderService));
	// }
	// }else if (key.contentEquals("checkBoxPreferencesLocationService")) {
	//
	// answer =
	// sharedPreferences.getBoolean("checkBoxPreferencesLocationService",
	// false);
	//
	// preferenceCategoryLocationAdvanced.setEnabled(answer);
	//
	// if ( answer) {
	// sb.append(getString(R.string.toastMessageEnabledLocationReminderService));
	//
	// }else {
	// sb.append(getString(R.string.toastMessageDisabledLocationReminderService));
	// }
	//
	// }
	//
	// if ( sb.length() > 0 ){
	//
	// Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
	// }
	//
	// sb.setLength(0);
	//
	//
	// }

}
