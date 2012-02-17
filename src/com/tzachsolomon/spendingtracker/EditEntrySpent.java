package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditEntrySpent extends Activity implements OnClickListener {

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;

	private EditText editTextSpentEditAmount;
	private EditText editTextSpentEditDate;

	private Button buttonSpentEditUpdate;

	private String m_RowId, m_Amount, m_Date;

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
			editTextSpentEditDate.setText(m_Date);
		}

	}

	private void initializeVariables() {
		//
		initializeEditText();

		buttonSpentEditUpdate = (Button) findViewById(R.id.buttonSpentEditUpdate);

		buttonSpentEditUpdate.setOnClickListener(this);

	}

	private void initializeEditText() {
		//
		editTextSpentEditAmount = (EditText) findViewById(R.id.editTextSpentEditAmount);
		editTextSpentEditDate = (EditText) findViewById(R.id.editTextSpentEditDate);

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
		m_SpendingTrackerDbEngine.updateSpentByRowId(m_RowId, newAmount,
				newDate);

	}

}
