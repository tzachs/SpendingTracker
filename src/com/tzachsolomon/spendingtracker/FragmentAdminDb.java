package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;

public class FragmentAdminDb extends SherlockFragment implements
		OnClickListener {

	private Button buttonDbExport;
	private Button buttonDbImport;
	private AdminDbListener mAdminDbListener;
	private Button buttonDeleteSpentEntries;
	private Button buttonDeleteRemindersTime;
	private Button buttonDeleteReminderLocationEnteries;

	public interface AdminDbListener {
		public void onDatabaseExportClicked();

		public void onDatabaseImportClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);

		try {
			mAdminDbListener = (AdminDbListener) activity;
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

		buttonDeleteSpentEntries = (Button) view
				.findViewById(R.id.buttonDeleteSpentEnteries);
		buttonDeleteRemindersTime = (Button)view.findViewById(R.id.buttonDeleteReminderTimeEnteries);
		buttonDeleteReminderLocationEnteries = (Button)view.findViewById(R.id.buttonDeleteReminderLocationEnteries);
		
		buttonDeleteSpentEntries.setOnClickListener(this);
		buttonDeleteRemindersTime.setOnClickListener(this);
		buttonDeleteReminderLocationEnteries.setOnClickListener(this);
		
		
		

		buttonDbExport.setOnClickListener(this);
		buttonDbImport.setOnClickListener(this);

		return view;
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonDeleteReminderLocationEnteries:
			buttonDeleteReminderLocationEnteries_Clicked();
			break;
		
		case R.id.buttonDeleteReminderTimeEnteries:
			buttonDeleteReminderTimeEnteries_Clicked();
			break;

		case R.id.buttonDeleteSpentEnteries:
			buttonDeleteSpentEntries_Clicked();
			break;
		case R.id.buttonDbExport:
			buttonDbExport_Clicked();
			break;

		case R.id.buttonDbImport:
			buttonDbImport_Clicked();
			break;

		}

	}

	private void buttonDeleteReminderLocationEnteries_Clicked() {
		// 
		AlertDeleteCancel.newInstance("Delete Location Reminders entries?",
				ClassCommonUtilities.DELETE_TYPE_REMINDERS_LOCATION_ENTRIES).show(
				getFragmentManager(), "deleteRemindersLocationEntires");
	}

	private void buttonDeleteReminderTimeEnteries_Clicked() {
		// 
		AlertDeleteCancel.newInstance("Delete Time Reminders entries?",
				ClassCommonUtilities.DELETE_TYPE_REMINDERS_TIME_ENTRIES).show(
				getFragmentManager(), "deleteRemindersTimeEntires");
	}

	private void buttonDeleteSpentEntries_Clicked() {
		new AlertDeleteCancel();
		//
		AlertDeleteCancel.newInstance("Delete spent entries?",
				ClassCommonUtilities.DELETE_TYPE_SPENT_ENTRIES).show(
				getFragmentManager(), "deleteSpentEntires");

	}

	private void buttonDbExport_Clicked() {
		//
		if (mAdminDbListener != null) {
			mAdminDbListener.onDatabaseExportClicked();
		}
	}

	private void buttonDbImport_Clicked() {
		//
		if (mAdminDbListener != null) {
			mAdminDbListener.onDatabaseImportClicked();
		}

	}

	public static class AlertDeleteCancel extends SherlockDialogFragment {
		public static AlertDeleteCancel newInstance(String title, int deleteType) {
			AlertDeleteCancel frag = new AlertDeleteCancel();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putInt("deleteType", deleteType);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			String title = getArguments().getString("title");
			final int deleteType = getArguments().getInt("deleteType");
			//
			return new AlertDialog.Builder(getSherlockActivity())
					.setTitle(title)
					.setPositiveButton("Delete",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									//
									((ActivityMain1) getSherlockActivity())
											.doDeleteCallback(deleteType);

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									//

								}
							}).create();

		}
	}

}
