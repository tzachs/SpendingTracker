package com.tzachsolomon.spendingtracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentAdminDb extends SherlockFragment implements OnClickListener {

	private final static String XMLFILE = "spendingTracker.xml";
	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private Button buttonDbExport;
	private Button buttonDbImport;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		
		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this.getSherlockActivity());
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_admin_db, null);
		
		buttonDbExport = (Button)view.findViewById(R.id.buttonDbExport);
		buttonDbImport = (Button)view.findViewById(R.id.buttonDbImport);
		
		buttonDbExport.setOnClickListener(this);
		buttonDbImport.setOnClickListener(this);

				
		return view;
	}

	public void onClick(View v) {
		// 
		switch (v.getId()){
		case R.id.buttonDbExport:
			buttonDbExport_Clicked();
			break;
			
		case R.id.buttonDbImport:
			buttonDbImport_Clicked();
			break;
			
		}
		
		
		
				
	}

	private void buttonDbExport_Clicked() {
		// TODO Auto-generated method stub
		
	}

	private void buttonDbImport_Clicked() {
		// TODO: change this to Async listener
		String result = "";

		try {
			result = m_SpendingTrackerDbEngine.importFromXMLFile(XMLFILE);
			

		} catch (Exception e) {
			//
			result = e.getMessage();
			e.printStackTrace();
		}

		Toast.makeText(this.getSherlockActivity(), result, Toast.LENGTH_LONG).show();
		


	}

}
