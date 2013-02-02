package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentDialogSpentNotification extends SherlockDialogFragment
		implements OnClickListener, OnItemSelectedListener {

	private Button buttonSpent;
	private Button buttonNo;
	private EditText editTextAmount;
	private Spinner spinnerCategories;
	private CheckBox checkBoxAutoClose;
	private FragmentDialogSpentListener mFragmentDialogSpentListener;
	
	private Context mActivity;
	private ClassDbEngine m_SpendingTrackerDbEngine;
	private String mAmount;
	private String mCategory;

	public interface FragmentDialogSpentListener {
		public void onSpentClicked(Bundle values);
		public void onSpentNoClicked(boolean autoClose);
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);
		try {
			mFragmentDialogSpentListener = (FragmentDialogSpentListener) activity;
		} catch (ClassCastException e) {
			new Throwable(activity.toString()
					+ " must implement FragmentDialogSpentListener");
		}

		mActivity = (SherlockFragmentActivity) activity;

		m_SpendingTrackerDbEngine = new ClassDbEngine(mActivity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		mAmount = getArguments().getString(ClassDbEngine.KEY_AMOUNT);
		mCategory = getArguments().getString(ClassDbEngine.KEY_CATEGORY);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(
				R.layout.fragment_dialog_spent_notification, container);

		initiliazeVariables(view);
		initSpinnerCategories();
		
		getDialog().setTitle("Spent reminder");

		return view;
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
			// Log.d(TAG, e.toString());
		}

	}

	private void initiliazeVariables(View view) {
		//
		buttonSpent = (Button) view.findViewById(R.id.buttonSpent);
		buttonNo = (Button) view.findViewById(R.id.buttonNo);

		buttonSpent.setOnClickListener(this);
		buttonNo.setOnClickListener(this);

		editTextAmount = (EditText) view.findViewById(R.id.editTextSpentAmount);
		editTextAmount.setText(mAmount);
		spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);

		spinnerCategories.setOnItemSelectedListener(this);

		checkBoxAutoClose = (CheckBox) view.findViewById(R.id.checkBoxAutoExit);

	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonNo:
			buttonNo_Clicked();
			break;
		case R.id.buttonSpent:
			buttonSpent_Clicked();
			break;
		}

	}

	private void buttonSpent_Clicked() {
		//
		if (editTextAmount.getText().length() > 0) {
			if (mFragmentDialogSpentListener != null) {
				Bundle values = new Bundle();
				values.putString("amount", editTextAmount.getText().toString());
				values.putString("category", mCategory);
				values.putString("comments", "Filled in by auto reminder");
				values.putBoolean("autoClose", checkBoxAutoClose.isChecked());
				mFragmentDialogSpentListener.onSpentClicked(values);
				
				
				if (checkBoxAutoClose.isChecked()){
					this.dismiss();
				}
			}
		}else {
			Toast.makeText(mActivity, "Please fill in amount", Toast.LENGTH_LONG).show();
		}

	}

	private void buttonNo_Clicked() {
		//
		if ( mFragmentDialogSpentListener!=null){
			mFragmentDialogSpentListener.onSpentNoClicked(checkBoxAutoClose.isChecked());
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
