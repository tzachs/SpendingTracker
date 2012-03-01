package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import android.widget.TableLayout;
import android.widget.TextView;

public class ViewEntriesReminders extends Activity {

	private static final String TAG = ViewEntriesReminders.class
			.getSimpleName();

	private TableLayout tlRemindersEntries;

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reminders_enteries);
		initVars();

		try {

			updateTable();

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	private void updateTable() {
		String[][] data = m_SpendingTrackerDbEngine.getReminders();
		int howMuchRowsToRemove = tlRemindersEntries.getChildCount() - 1;

		// removing all entries except the headers
		tlRemindersEntries.removeViews(1, howMuchRowsToRemove);
		
		PopuldateRows(data);
	}

	private void PopuldateRows(String[][] i_Data) {
		int rows = i_Data.length;
		int i;

		for (i = 0; i < rows; i++) {
			AddRowToTable(i_Data[i]);

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

	private void menuItemReminderImport_Clicked() {
		// 
		
	}

	private void menuItemReminderExport_Clicked() {
		// 
	}

	private void menuItemReminderDelete_Clicked() {
		//
		final EditText editTextRowId = new EditText(this);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		editTextRowId.setInputType(InputType.TYPE_CLASS_NUMBER);

		alertDialog.setTitle(getString(R.string.alertDialogTitleDeleteReminder));
		alertDialog.setView(editTextRowId);
		alertDialog.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						String reminderId = editTextRowId.getText().toString();
						Log.v(TAG, "Delete reminder with id " + reminderId);

						m_SpendingTrackerDbEngine
								.deleteReminderById(reminderId);
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

	private void AddRowToTable(String[] i_Row) {
		int columns = i_Row.length;
		int i;
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView[] textViews = new TextView[columns];
		// taken from the todays_entries.xml
		int[] layout_weight = new int[] { 10, 10, 10, 10, 10, 10, 40 };

		for (i = 0; i < columns; i++) {
			textViews[i] = new TextView(this);
			textViews[i].setText(i_Row[i]);
			textViews[i].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					layout_weight[i]));

			tr.addView(textViews[i]);
		}

		tlRemindersEntries.addView(tr);

	}

	private void initVars() {
		// initialize members
		tlRemindersEntries = (TableLayout) findViewById(R.id.tableLayoutEnteriesReminders);

		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);

	}
}
