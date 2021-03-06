package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity {

	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		TextView textViewVersion = (TextView) findViewById(R.id.textViewVersion);
		TextView textViewAppDescription = (TextView) findViewById(R.id.textViewAppDescription);

		String version = "N/A";
		try {
			version = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			//
			e.printStackTrace();
		}
		textViewVersion.setText(getString(R.string.textViewAboutVersion)
				+ version);
		
		textViewAppDescription.setText(getString(R.string.app_description));

	
	
	}
}
