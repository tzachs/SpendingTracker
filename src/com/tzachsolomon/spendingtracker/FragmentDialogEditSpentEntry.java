package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentDialogEditSpentEntry extends SherlockDialogFragment
		implements OnClickListener, OnItemSelectedListener {

	private static final String TAG = FragmentDialogEditSpentEntry.class
			.getSimpleName();
	private EditText editTextAmount;
	private EditText editTextDate;
	private EditText editTextTime;
	private SherlockFragmentActivity mActivity;
	private UpdateSpentEntryListener mUpdateSpentEntryListener;
	private String mAmount;
	private String mId;
	private String mDate;
	private String mTime;
	private String mCategory;
	private Button buttonUpdateSpentEntry;
	private TextView textViewEntryId;
	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private Spinner spinnerCategories;

	public interface UpdateSpentEntryListener {
		public void onUpdateSpentEntryClicked(Bundle values);
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);
		mActivity = (SherlockFragmentActivity) activity;

		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(mActivity);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		//
		super.onActivityCreated(arg0);

		try {
			mUpdateSpentEntryListener = (UpdateSpentEntryListener) mActivity;
		} catch (ClassCastException e) {
			new Throwable(mActivity.toString()
					+ " must implement UpdateSpentEntryListener class");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View ret = inflater.inflate(R.layout.fragment_dialog_edit_spent_entry,
				container);

		textViewEntryId = (TextView) ret.findViewById(R.id.textViewEntryId);

		editTextAmount = (EditText) ret.findViewById(R.id.editTextAmount);
		editTextDate = (EditText) ret.findViewById(R.id.editTextDate);
		editTextTime = (EditText) ret.findViewById(R.id.editTextTime);

		editTextAmount.setText(mAmount);
		editTextDate.setText(mDate);
		editTextTime.setText(mTime);

		textViewEntryId.setText("#" + mId);

		buttonUpdateSpentEntry = (Button) ret
				.findViewById(R.id.buttonUpdateSpentEntry);

		buttonUpdateSpentEntry.setOnClickListener(this);

		spinnerCategories = (Spinner) ret.findViewById(R.id.spinnerCategories);

		spinnerCategories.setOnItemSelectedListener(this);

		initSpinnerCategories();

		getDialog().setTitle("Update spent entry");

		return ret;
	}

	public void initSpinnerCategories() {
		//
		try {
			int index;
			boolean foundCategory = false;
			String[] m_Categories1 = m_SpendingTrackerDbEngine
					.getCategoriesStringArray();
			index = m_Categories1.length - 1;

			while (index > 0 && !foundCategory) {
				if (m_Categories1[index].contentEquals(mCategory)) {
					foundCategory = true;
				} else {
					index--;
				}
			}

			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					mActivity, android.R.layout.simple_spinner_item,
					m_Categories1);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

			spinnerCategories.setSelection(index, false);

		} catch (Exception e) {
			// if (m_DebugMode) {
			// Toast.makeText(this, e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
			// }

			e.printStackTrace();
			Log.d(TAG, e.toString());
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		mId = getArguments().getString("id");
		mAmount = getArguments().getString("amount");
		mDate = getArguments().getString("date");
		mTime = getArguments().getString("time");
		mCategory = getArguments().getString("category");
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonUpdateSpentEntry:
			buttonUpdateSpentEntry_Clicked();
			break;
		}

	}

	private void buttonUpdateSpentEntry_Clicked() {
		//
		boolean validEntry = true;
		String amount = editTextAmount.getText().toString();
		String date = editTextDate.getText().toString();
		String time = editTextTime.getText().toString();

		if (amount.length() < 1) {
			Toast.makeText(mActivity, "Please fill in amount",
					Toast.LENGTH_LONG).show();
			validEntry = false;
		}

		if (date.length() != 8) {
			Toast.makeText(mActivity,
					"Please fill in date in the following format: YYYYMMDD",
					Toast.LENGTH_LONG).show();
			validEntry = false;
		}

		if (time.length() != 6) {
			Toast.makeText(mActivity,
					"Please fill in time  in the following format: HHMMSS",
					Toast.LENGTH_LONG).show();
			validEntry = false;
		}

		if (mUpdateSpentEntryListener != null && validEntry) {
			Bundle values = new Bundle();
			values.putString("id", mId);
			values.putString("amount", amount);
			values.putString("date", date);
			values.putString("time", time);
			values.putString("category", mCategory);

			mUpdateSpentEntryListener.onUpdateSpentEntryClicked(values);
		}

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		//
		mCategory = parent.getItemAtPosition(pos).toString();

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		//
		mCategory = "";
	}

}
