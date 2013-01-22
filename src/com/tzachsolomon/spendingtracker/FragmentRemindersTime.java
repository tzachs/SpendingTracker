package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;
import java.util.Calendar;
import static com.tzachsolomon.spendingtracker.ClassCommonUtilities.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentRemindersTime extends SherlockFragment implements
		OnCheckedChangeListener, OnClickListener {

	public interface TimeReminderListener {
		public void onAddTimeReminderClicked(
				ArrayList<ClassTypeReminderTime> values);

		public void onManagerReminderTimeClicked();
	}

	private RelativeLayout relativeLayoutDayCheckboxes;
	private RadioGroup radioGroupReminders;
	private EditText editTextDayInMonth;

	private TimeReminderListener mButtonAddEntrySpentListener;
	private Button buttonAddTimeReminder;
	private Button buttonManageTimeReminderEntries;
	private TimePicker timePicker;
	private CheckBox checkBoxSunday;
	private CheckBox checkBoxMonday;
	private CheckBox checkBoxTuesday;
	private CheckBox checkBoxWednesday;
	private CheckBox checkBoxThursday;
	private CheckBox checkBoxFriday;
	private CheckBox checkBoxSaturday;

	@Override
	public void onAttach(Activity activity) {
		//

		try {
			mButtonAddEntrySpentListener = (TimeReminderListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimeReminderListener ");
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

		buttonAddTimeReminder = (Button) view
				.findViewById(R.id.buttonAddTimeReminder);
		buttonAddTimeReminder.setOnClickListener(this);
		buttonManageTimeReminderEntries = (Button) view
				.findViewById(R.id.buttonManageTimeReminderEntries);
		buttonManageTimeReminderEntries.setOnClickListener(this);

		timePicker = (TimePicker) view.findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		initTimePicker();

		checkBoxSunday = (CheckBox) view.findViewById(R.id.checkBoxSunday);
		checkBoxMonday = (CheckBox) view.findViewById(R.id.checkBoxMonday);
		checkBoxTuesday = (CheckBox) view.findViewById(R.id.checkBoxTuesday);
		checkBoxWednesday = (CheckBox) view
				.findViewById(R.id.checkBoxWednesday);
		checkBoxThursday = (CheckBox) view.findViewById(R.id.checkBoxThursday);
		checkBoxFriday = (CheckBox) view.findViewById(R.id.checkBoxFriday);
		checkBoxSaturday = (CheckBox) view.findViewById(R.id.checkBoxSaturday);

		return view;

	}

	@Override
	public void onResume() {
		//
		super.onResume();
		initTimePicker();
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
			setCheckAccordingToDate();

			break;

		case R.id.radioButtonMonthly:

			relativeLayoutDayCheckboxes.setVisibility(View.GONE);
			editTextDayInMonth.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	private void setCheckAccordingToDate() {
		//
		Calendar cal = Calendar.getInstance();

		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			checkBoxSunday.setChecked(true);
			break;

		case Calendar.MONDAY:
			checkBoxMonday.setChecked(true);
			break;

		case Calendar.TUESDAY:
			checkBoxTuesday.setChecked(true);
			break;

		case Calendar.WEDNESDAY:
			checkBoxWednesday.setChecked(true);
			break;

		case Calendar.THURSDAY:
			checkBoxThursday.setChecked(true);
			break;

		case Calendar.FRIDAY:
			checkBoxFriday.setChecked(true);
			break;

		case Calendar.SATURDAY:
			checkBoxSaturday.setChecked(true);
			break;

		default:
			break;
		}

	}

	private void initTimePicker() {
		timePicker.setCurrentHour(Calendar.getInstance().get(
				Calendar.HOUR_OF_DAY));
		timePicker
				.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonAddTimeReminder:
			buttonAddTimeReminder_Clicked();
			break;
		case R.id.buttonManageTimeReminderEntries:
			buttonManageTimeReminderEntries_Clicked();
			break;
		}

	}

	private void buttonManageTimeReminderEntries_Clicked() {
		//
		if (mButtonAddEntrySpentListener != null) {
			mButtonAddEntrySpentListener.onManagerReminderTimeClicked();
		}

	}

	private void buttonAddTimeReminder_Clicked() {
		//

		boolean valid = false;

		if (mButtonAddEntrySpentListener != null) {
			ArrayList<ClassTypeReminderTime> values = new ArrayList<ClassTypeReminderTime>();
			String currentHour = timePicker.getCurrentHour().toString();
			String currentMinute = timePicker.getCurrentMinute().toString();

			switch (radioGroupReminders.getCheckedRadioButtonId()) {
			case R.id.radioButtonEveryday:
				values.add(new ClassTypeReminderTime("-1",
						TYPE_REMINDER_TIME_EVERYDAY, currentHour,
						currentMinute, TYPE_REMINDER_TIME_DAY_DONT_CARE, "", ""));
				valid = true;
				break;

			case R.id.radioButtonWeekly:
				if (checkBoxSunday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_SUNDAY, "", ""));
					valid = true;

				}
				if (checkBoxMonday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_MONDAY, "", ""));
					valid = true;

				}
				if (checkBoxTuesday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_TUESDAY, "", ""));
					valid = true;
				}
				if (checkBoxWednesday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_WEDNESDAY, "", ""));
					valid = true;
				}
				if (checkBoxThursday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_THURSDAY, "", ""));
					valid = true;
				}
				if (checkBoxFriday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_FRIDAY, "", ""));
					valid = true;
				}
				if (checkBoxSaturday.isChecked()) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_WEEKLY, currentHour,
							currentMinute, TYPE_REMINDER_TIME_SATURDAY, "", ""));
					valid = true;
				}

				if (!valid) {
					Toast.makeText(getSherlockActivity(),
							"Make sure at least one day is checked",
							Toast.LENGTH_LONG).show();
				}

				break;

			case R.id.radioButtonMonthly:
				if (editTextDayInMonth.getText().length() > 0) {
					values.add(new ClassTypeReminderTime("-1",
							TYPE_REMINDER_TIME_MONTHLY, currentHour,
							currentMinute, editTextDayInMonth.getText()
									.toString(), "", ""));
					valid = true;
				} else {
					Toast.makeText(getSherlockActivity(),
							"Please fill in day in month", Toast.LENGTH_LONG)
							.show();
				}

				break;

			default:
				break;
			}
			if (valid) {
				mButtonAddEntrySpentListener.onAddTimeReminderClicked(values);
			}
		}

	}

}
