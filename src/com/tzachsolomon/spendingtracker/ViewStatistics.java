package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ViewStatistics extends Activity {
	
	private final static String TAG = ViewStatistics.class.getSimpleName();
	private TextView textViewStatistics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		
		Log.v(TAG, "View statistics onCreate");
		
		textViewStatistics = (TextView)findViewById(R.id.textViewStatistics);
		
		Bundle bundle = getIntent().getExtras();
		textViewStatistics.setText(bundle.getString("textViewStatistics"));
		
		
		
	}

}
