package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class ViewEntriesRemindersLocation extends Activity implements OnClickListener {

	private static final String TAG = ViewEntriesRemindersLocation.class
			.getSimpleName();

	private TableLayout tableLayoutRemindersEntries;

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private SharedPreferences m_SharedPreferences;
	private boolean m_DebugMode;
	private Button buttonDeleteSentLocationNotifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_reminders_entries);

		initPreferences();

		initVars();

		try {

			updateTable();

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	private void initPreferences() {
		try {
			m_SharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());

			m_DebugMode = m_SharedPreferences.getBoolean(
					"checkBoxPreferenceDebug", false);

		} catch (Exception e) {

			String message = e.getMessage().toString();

			if (m_DebugMode) {
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			}
			Log.e(TAG, message);
		}

	}

	private void updateTable() {
		String[][] data = m_SpendingTrackerDbEngine
				.getLocationRemindersAsStrings();
		int howMuchRowsToRemove = tableLayoutRemindersEntries.getChildCount() - 1;

		// removing all entries except the headers
		tableLayoutRemindersEntries.removeViews(1, howMuchRowsToRemove);

		PopuldateRows(data);
	}

	private void PopuldateRows(String[][] i_Data) {
		int rows = i_Data.length;
		int i;

		for (i = 0; i < rows; i++) {
			AddRowToTable(i_Data[i]);

		}

	}

	private void AddRowToTable(String[] i_Row) {
		int columns = i_Row.length;
		int i;
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView[] textViews = new TextView[columns];
		//
		int[] layout_weight = new int[] { 10, 40, 10, 40 };

		for (i = 0; i < columns; i++) {
			textViews[i] = new TextView(this);
			textViews[i].setText(i_Row[i]);
			textViews[i].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					layout_weight[i]));

			tr.addView(textViews[i]);
		}

		tableLayoutRemindersEntries.addView(tr);

	}

	private void initVars() {
		// initialize members
		tableLayoutRemindersEntries = (TableLayout) findViewById(R.id.tableLayoutEnteriesReminders);

		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);
		buttonDeleteSentLocationNotifications = (Button) findViewById(R.id.buttonDeleteSentLocationNotifications);
		
		buttonDeleteSentLocationNotifications.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// 
		super.onResume();

		if (m_DebugMode) {

			buttonDeleteSentLocationNotifications.setVisibility(View.VISIBLE);
		}else {
			buttonDeleteSentLocationNotifications.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		super.onCreateOptionsMenu(menu);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_reminders, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//
		boolean ret = false;
		switch (item.getItemId()) {
		case (R.id.menuItemReminderDelete):
			menuItemReminderDelete_Clicked();
			ret = true;
			break;

		default:
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}

	private void menuItemReminderDelete_Clicked() {
		//
		final EditText editTextRowId = new EditText(this);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		editTextRowId.setInputType(InputType.TYPE_CLASS_NUMBER);

		alertDialog
				.setTitle(getString(R.string.alertDialogTitleDeleteReminder));
		alertDialog.setView(editTextRowId);
		alertDialog.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						String reminderId = editTextRowId.getText().toString();
						Log.v(TAG, "Delete reminder with id " + reminderId);

						m_SpendingTrackerDbEngine
								.deleteLocationReminderById(reminderId);
						updateTable();

					}
				});
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//

					}
				});

		alertDialog.show();

	}

	@Override
	public void onClick(View v) {
		// 
		switch (v.getId()) {
		case R.id.buttonDeleteSentLocationNotifications:
			buttonDeleteSentLocationNotifications_Clicked();
			break;

		default:
			break;
		}
		
	}

	private void buttonDeleteSentLocationNotifications_Clicked() {
		// 
		m_SpendingTrackerDbEngine.deleteAllSentNotifications();
		
	}

}
