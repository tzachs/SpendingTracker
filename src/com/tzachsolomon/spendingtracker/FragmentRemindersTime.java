package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonAddEntrySpentListener;

public class FragmentRemindersTime extends SherlockFragment implements
		OnCheckedChangeListener, OnClickListener {

	public interface AddTimeReminderListener {
		public void onAddTimeReminderClicked();
	}
	
	private RelativeLayout relativeLayoutDayCheckboxes;
	private RadioGroup radioGroupReminders;
	private EditText editTextDayInMonth;
	private SherlockFragmentActivity mActivity;
	private AddTimeReminderListener mButtonAddEntrySpentListener;
	private Button buttonAddTimeReminder;
	
	@Override
	public void onAttach(Activity activity) {
		// 
		mActivity = (SherlockFragmentActivity)activity;
		

		try {
			mButtonAddEntrySpentListener = (AddTimeReminderListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement AddTimeReminderListener ");
		}
		
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_reminders_time, null);

		relativeLayoutDayCheckboxes = (RelativeLayout) view
				.findViewById(R.id.relativeLayoutDayCheckboxes);

		radioGroupReminders = (RadioGroup) view
				.findViewById(R.id.radioGroupReminders);

		radioGroupReminders.setOnCheckedChangeListener(this);

		editTextDayInMonth = (EditText) view
				.findViewById(R.id.editTextDayInMonth);
		
		buttonAddTimeReminder = (Button) view.findViewById(R.id.buttonAddTimeReminder);
		buttonAddTimeReminder.setOnClickListener(this);

		return view;

	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//
		switch (checkedId) {
		case R.id.radioButtonEveryday:
			relativeLayoutDayCheckboxes.setVisibility(View.GONE);

			editTextDayInMonth.setVisibility(View.GONE);
			break;

		case R.id.radioButtonWeekly:

			relativeLayoutDayCheckboxes.setVisibility(View.VISIBLE);

			editTextDayInMonth.setVisibility(View.GONE);

			break;

		case R.id.radioButtonMonthly:

			relativeLayoutDayCheckboxes.setVisibility(View.GONE);
			editTextDayInMonth.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	public void onClick(View v) {
		// 
		switch (v.getId()){
		case R.id.buttonAddTimeReminder:
			buttonAddTimeReminder_Clicked();
			break;
		}
		
	}

	private void buttonAddTimeReminder_Clicked() {
		// 
		if (mButtonAddEntrySpentListener != null){
			mButtonAddEntrySpentListener.onAddTimeReminderClicked();
		}
			
		
	}

}
