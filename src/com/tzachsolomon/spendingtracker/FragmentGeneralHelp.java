package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonAddEntrySpentListener;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonCategoriesEditListener;

public class FragmentGeneralHelp extends SherlockFragment implements
		OnClickListener, OnItemSelectedListener, OnTouchListener {

	private ButtonAddEntrySpentListener mButtonAddEntrySpentListener;
	private ButtonCategoriesEditListener mButtonCategoriesEditListener;
	private Button buttonAddEntrySpent;
	private Button buttonCategoriesEdit;
	private EditText editTextEntrySpent;
	private EditText editTextComment;
	private Spinner spinnerCategories;
	private String mCategorySelected;
	private ClassDbEngine m_SpendingTrackerDbEngine;
	private SherlockFragmentActivity mActivity;
	private TextView textViewSpentToday;
	private TextView textViewSpentMonth;
	private TextView textViewSpentWeek;
	private boolean mAddedExampleCategories;
	private Double sum;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (SherlockFragmentActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_general, null);

		initializeVariables(view);

		// initSpinnerCategories();

		Toast.makeText(getSherlockActivity(),
				"Press on a button to show it's help text", Toast.LENGTH_LONG)
				.show();

		return view;
	}

	public void initSpinnerCategories() {
		//
		try {

			String[] m_Categories1 = new String[] { "Breakfast", "Lunch",
					"Train to/from work", "Rent", "Newspaper subscription" };

			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					mActivity, R.layout.spinner_item_line, m_Categories1);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

		} catch (Exception e) {
			// if (m_DebugMode) {
			// Toast.makeText(this, e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
			// }

			e.printStackTrace();

		}

	}

	private void initializeVariables(View view) {
		//
		sum = 0.0;
		mAddedExampleCategories = false;
		buttonAddEntrySpent = (Button) view
				.findViewById(R.id.buttonAddEntrySpent);
		buttonCategoriesEdit = (Button) view
				.findViewById(R.id.buttonCategoriesEdit);

		buttonAddEntrySpent.setOnClickListener(this);
		buttonCategoriesEdit.setOnClickListener(this);

		editTextEntrySpent = (EditText) view
				.findViewById(R.id.editTextEntrySpent);
		editTextEntrySpent.setOnClickListener(this);
		editTextComment = (EditText) view.findViewById(R.id.editTextComment);

		spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);
		spinnerCategories.setOnTouchListener(this);
		spinnerCategories.setOnItemSelectedListener(this);

		textViewSpentToday = (TextView) view
				.findViewById(R.id.textViewSpentToday);
		textViewSpentMonth = (TextView) view
				.findViewById(R.id.textViewSpentMonth);
		textViewSpentWeek = (TextView) view
				.findViewById(R.id.textViewSpentWeek);

	}

	public void displayHelp(String text) {
		Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_LONG).show();
	}

	public void onClick(View arg0) {
		//
		switch (arg0.getId()) {
		case R.id.buttonAddEntrySpent:
			String amount = editTextEntrySpent.getText().toString();
			if (amount.length() == 0) {
				displayHelp("Fill in amount, press this to add one time spending");
				displayHelp("You can setup  a reminder for this spending in case it happens every day/week/month or by location");

			} else if (mCategorySelected == null) {
				displayHelp("No category was selected, press the edit button to add some categories");
			} else {
				sum += Double.valueOf(amount);
				String sum1 = " " + String.valueOf(sum);
				textViewSpentMonth
						.setText(getString(R.string.textViewSpentMonthText)
								+ sum1);
				textViewSpentToday
						.setText(getString(R.string.textViewSpentTodayText)
								+ sum1);
				textViewSpentWeek
						.setText(getString(R.string.textViewSpentWeekText)
								+ sum1);
			}
			break;
		case R.id.buttonCategoriesEdit:
			displayHelp("Press this button when you want to add / edit / delete the categories you spend on.");
			if (!mAddedExampleCategories) {
				displayHelp("In the mean time, we've added some example categories. Press on Breakfast on the right to change the category");
				addExampleCategories();

			}
			break;
		case R.id.editTextEntrySpent:
			displayHelp("Type here the amount you spent, then choose the category spent on and press Add Entry");
			break;
		}

	}

	private void addExampleCategories() {
		//

		mAddedExampleCategories = true;
		initSpinnerCategories();

	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		//
		mCategorySelected = "bla";
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		//
		mCategorySelected = null;
	}

	public boolean onTouch(View v, MotionEvent event) {
		//
		if (event.getAction() == MotionEvent.ACTION_UP
				&& !mAddedExampleCategories) {
			displayHelp("Press on edit categories first");
			spinnerCategories.setPressed(false);
		}
		return true;
	}

}
