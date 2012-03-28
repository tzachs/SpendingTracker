package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditEntrySpent extends Activity implements OnClickListener {

	private static final String TAG = EditEntrySpent.class.getSimpleName();

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;

	private EditText editTextSpentEditAmount;
	private EditText editTextSpentEditDate;
	private EditText editTextSpentEditTime;

	private Button buttonSpentEditUpdate;

	private String m_RowId, m_Amount, m_Date;
	private String[] m_Categories;
	private Spinner spinnerCategories;
	private String m_CategorySelected;
	ArrayAdapter<String> m_SpinnerArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		String[] row;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_spent_edit);

		initializeVariables();

		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);

		Bundle extras = getIntent().getExtras();

		m_RowId = extras.getString("RowId");

		row = m_SpendingTrackerDbEngine.getEntrySpentByRowId(m_RowId);

		// checking if a row was found
		if (row == null) {

			Toast.makeText(this, "No row was found", Toast.LENGTH_LONG).show();
			finish();

		} else {

			m_Amount = row[1];
			m_Date = row[3];

			editTextSpentEditAmount.setText(m_Amount);
			editTextSpentEditDate.setText(m_Date.substring(0, 8));
			editTextSpentEditTime.setText(m_Date.substring(9, 13));

			initSpinnerCategories();
			spinnerCategories.setSelection(m_SpinnerArrayAdapter
					.getPosition(row[2]));

		}

	}

	private void initSpinnerCategories() {
		//
		try {

			m_Categories = m_SpendingTrackerDbEngine.getCategories();
			m_SpinnerArrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, m_Categories);

			spinnerCategories.setAdapter(m_SpinnerArrayAdapter);

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
		}

	}

	private void initializeVariables() {
		//
		initializeEditText();
		initSpinners();

		buttonSpentEditUpdate = (Button) findViewById(R.id.buttonSpentEditUpdate);

		buttonSpentEditUpdate.setOnClickListener(this);

	}

	private void initSpinners() {
		//
		spinnerCategories = (Spinner) findViewById(R.id.spinnerCategories);

		spinnerCategories
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						//
						m_CategorySelected = parent.getItemAtPosition(pos)
								.toString();

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

						m_CategorySelected = "";
					}

				});

	}

	private void initializeEditText() {
		//
		editTextSpentEditAmount = (EditText) findViewById(R.id.editTextSpentEditAmount);
		editTextSpentEditDate = (EditText) findViewById(R.id.editTextSpentEditDate);
		editTextSpentEditTime = (EditText) findViewById(R.id.editTextSpentEditTime);

	}

	@Override
	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonSpentEditUpdate:
			buttonSpentEditUpdate_Clicked();
			break;

		}

	}

	private void buttonSpentEditUpdate_Clicked() {
		//
		String newAmount = editTextSpentEditAmount.getText().toString();
		String newDate = editTextSpentEditDate.getText().toString();
		String newTime = editTextSpentEditTime.getText().toString();
		String toastMessage;
		String newmDate;
		
		newmDate = newDate + "T" + newTime + m_Date.substring(13);
		

		if (newAmount.length() > 0 && newDate.length() == 8
				&& newTime.length() == 4 && m_CategorySelected.length() > 0 ) {

			m_SpendingTrackerDbEngine.updateSpentByRowId(m_RowId, newAmount,
					newmDate, m_CategorySelected);

			toastMessage = getString(R.string.entryUpdated);
			
		} else {
			toastMessage = getString(R.string.toastMessageErrorUpdateRecord);
			
		}
		
		Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();

	}

}
