package com.tzachsolomon.spendingtracker;

import android.app.Activity;
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

public class FragmentAdminDb extends SherlockFragment implements
		OnClickListener {



	private Button buttonDbExport;
	private Button buttonDbImport;
	private AdminDbListener mAdminDbListener;

	public interface AdminDbListener {
		public void onDatabaseExportClicked();

		public void onDatabaseImportClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);

		try {
			mAdminDbListener = (AdminDbListener)activity;
		} catch (ClassCastException e) {

			throw new ClassCastException(activity.toString()
					+ " must implement AdminDbListener listener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_admin_db, null);

		buttonDbExport = (Button) view.findViewById(R.id.buttonDbExport);
		buttonDbImport = (Button) view.findViewById(R.id.buttonDbImport);

		buttonDbExport.setOnClickListener(this);
		buttonDbImport.setOnClickListener(this);

		return view;
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonDbExport:
			buttonDbExport_Clicked();
			break;

		case R.id.buttonDbImport:
			buttonDbImport_Clicked();
			break;

		}

	}

	private void buttonDbExport_Clicked() {
		// 
		if ( mAdminDbListener!= null){
			mAdminDbListener.onDatabaseExportClicked();
		}
	}

	private void buttonDbImport_Clicked() {
		// 
		if ( mAdminDbListener!= null){
			mAdminDbListener.onDatabaseImportClicked();
		}
		


	}
}
