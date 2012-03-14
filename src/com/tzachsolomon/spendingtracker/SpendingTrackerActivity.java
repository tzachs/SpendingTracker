package com.tzachsolomon.spendingtracker;

import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;

import android.os.Bundle;
import android.os.SystemClock;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;

import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class SpendingTrackerActivity extends Activity implements
		OnClickListener, OnTabChangeListener, OnCheckedChangeListener {

 	// TODO: export to Google Document
	// TODO: delete spent entry by id
	// TODO: reminder by location
	// TODO: add option to place tab on bottom instead of up
	// TODO: Notification after updating record
	// TODO: add colors to categories
	// TODO: sort categories by name
	// TODO: Sort entries by name / date / row id
	// 

	/** Called when the activity is first created. */
	private static final String TAG = SpendingTrackerActivity.class
			.getSimpleName();

	private TabHost thMain;
	private TabSpec tabSpec;
	private EditText editTextQuickAddAmount;
	private EditText editTextComment;
	private EditText etDayInMonthReminder;

	private Button buttonQuickAddInsert;
	private Button buttonShowTodayEntries;
	private Button buttonShowWeeklyEntries;
	private Button buttonShowMonthEntries;
	private Button buttonShowReminderEntries;
	private Button buttonAddReminder;
	private Button buttonCategoriesEdit;
	private Button buttonDeleteAllEnteries;

	private TextView textViewSpentToday;
	private TextView textViewSpentWeek;
	private TextView textViewSpentMonth;

	private Spinner spinnerCategories;
	private TimePicker timePickerDay;

	private CheckBox cbSunday, cbMonday, cbTuesday, cbWednesday, cbThursday,
			cbFriday, cbSaturday;
	private CheckBox checkBoxAutoAddReminder;

	private RadioGroup radioGroupReminder;

	private final String TAB_TAG_GENERAL = "tagGeneral";
	private final String TAB_TAG_ENTRIES = "tagEntries";
	private final String TAB_TAG_REMINDERS = "tagReminders";

	private SharedPreferences m_SharedPreferences;

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private String m_CategorySelected;
	private String[] m_Categories;

	private PendingIntent m_AlarmSender;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// waiting for the debugger to attach in order to the debug the service
		// android.os.Debug.waitForDebugger();

		// starting the service to make sure the service will not stopped in
		// case
		// the service is unbinded
		// service will later be checked again by onStart to see if it needs to
		// be stopped

		m_AlarmSender = PendingIntent.getService(SpendingTrackerActivity.this,
				0, new Intent(SpendingTrackerActivity.this,
						SpendingTrackerService.class), 0);

		initPreferences();
		initVariables();

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(12021982);

	}

	private void stopAlarmManager() {
		//
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(m_AlarmSender);
	}

	private void startAlarmManager() {
		//
		Calendar firstTime = Calendar.getInstance();
		firstTime.setTimeInMillis(SystemClock.elapsedRealtime());

		int secondsToAdd = 60 - firstTime.get(Calendar.SECOND);

		Log.v(TAG, "Starting service using alarm manager in " + secondsToAdd
				+ " Seconds");

		firstTime.add(Calendar.SECOND, secondsToAdd);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				firstTime.getTimeInMillis(), 60000, m_AlarmSender);

	}

	private void initPreferences() {
		m_SharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

	}

	@Override
	protected void onResume() {

		super.onResume();

		checkServiceStatus(false);

		checkPendingReminder();

		updateDaySpent();
		updateWeekSpent();
		updateMonthSpent();

	}

	private void checkPendingReminder() {
		// Function checks if there is a pending reminder
		// Currently only support one reminder

		Bundle extras = getIntent().getExtras();

		int flag = getIntent().getFlags()
				& Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;

		if (extras != null && flag == 0) {

			getIntent().getExtras().clear();

			Log.i(TAG, "Found extras, checking if starting from reminder");
			boolean isReminder = false;
			if (extras
					.containsKey(SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY)) {
				Log.i(TAG, "Starting from everyday reminder");
				getIntent().removeExtra(
						SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY);

				isReminder = true;

			} else if (extras
					.containsKey(SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY)) {
				Log.i(TAG, "Starting from weekly reminder");
				getIntent().removeExtra(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY);
				isReminder = true;
			} else if (extras
					.containsKey(SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY)) {
				Log.i(TAG, "Starting from monthly reminder");
				getIntent().removeExtra(
						SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY);
				isReminder = true;
			}

			if (isReminder) {

				final CheckBox checkBoxAutoExit = new CheckBox(this);
				checkBoxAutoExit.setText("Auto close");
				checkBoxAutoExit.setChecked(true);

				final String amount = extras
						.getString(SpendingTrackerDbEngine.KEY_AMOUNT);
				final String category = extras
						.getString(SpendingTrackerDbEngine.KEY_CATEGORY);
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				StringBuilder sb = new StringBuilder();
				getIntent().removeExtra(SpendingTrackerDbEngine.KEY_AMOUNT);
				getIntent().removeExtra(SpendingTrackerDbEngine.KEY_CATEGORY);

				Log.i(TAG, "Found amount " + amount + " in extra");
				Log.i(TAG, "Found category " + category + " in extra");

				sb.append(getString(R.string.stringDidYouSpend));
				sb.append(' ');
				sb.append(amount);
				sb.append(' ');
				sb.append(Currency.getInstance(Locale.getDefault()).getSymbol());
				sb.append(' ');
				sb.append(getString(R.string.stringDidYouSpendOn));
				sb.append(' ');
				sb.append(category);
				sb.append(' ');
				sb.append("?");

				alertDialog.setTitle("Spent money?");

				alertDialog.setMessage(sb.toString());
				alertDialog.setView(checkBoxAutoExit);

				alertDialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//
								m_SpendingTrackerDbEngine
										.insertNewSpending(amount, category,
												"From reminder", null);

								if (checkBoxAutoExit.isChecked()) {
									finish();
								}

							}
						});
				alertDialog.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//
								if (checkBoxAutoExit.isChecked()) {
									finish();
								}

							}
						});

				alertDialog.show();

				sb.setLength(0);

			}

		}

	}

	private void checkServiceStatus(boolean i_DisplayToast) {
		//
		try {
			boolean serviceAlwaysOn = m_SharedPreferences.getBoolean(
					"cbServiceAlwaysOn", true);
			StringBuilder sb = new StringBuilder();

			// checking service always should be on and the service is not
			// running
			stopAlarmManager();

			if (serviceAlwaysOn) {
				sb.append("Enabled service");
				startAlarmManager();
			} else {
				sb.append("Disabled service");
			}

			if (i_DisplayToast) {
				Toast.makeText(SpendingTrackerActivity.this, sb.toString(),
						Toast.LENGTH_LONG).show();
			}

			sb.setLength(0);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}

	}

	private void initVariables() {
		try {

			m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);
			m_CategorySelected = "";

			initRadioButtons();
			initCheckBoxes();
			setCheckboxesVisible(View.GONE);

			initEditTexts();
			initTabs();
			initButtons();
			initTextViews();
			initSpinners();

			timePickerDay = (TimePicker) findViewById(R.id.timePicker);
			timePickerDay.setIs24HourView(true);

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

		} catch (Exception e) {
			Toast.makeText(this, "Error initializing variables!!!",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, e.toString());
		}

	}

	private void initSpinners() {
		//
		spinnerCategories = (Spinner) findViewById(R.id.spinnerCategories);

	}

	private void initTextViews() {
		//
		textViewSpentToday = (TextView) findViewById(R.id.textViewSpentToday);
		textViewSpentWeek = (TextView) findViewById(R.id.textViewSpentWeek);
		textViewSpentMonth = (TextView) findViewById(R.id.textViewSpentMonth);

	}

	private void initButtons() {
		//
		buttonQuickAddInsert = (Button) findViewById(R.id.buttonQuickAddInsert);

		buttonShowTodayEntries = (Button) findViewById(R.id.buttonShowTodayEntries);
		buttonShowMonthEntries = (Button) findViewById(R.id.buttonShowMonthEntries);
		buttonShowReminderEntries = (Button) findViewById(R.id.buttonShowReminderEntries);
		buttonShowWeeklyEntries = (Button) findViewById(R.id.buttonShowWeeklyEntries);

		buttonAddReminder = (Button) findViewById(R.id.buttonAddReminder);
		buttonCategoriesEdit = (Button) findViewById(R.id.buttonCategoriesEdit);
		buttonDeleteAllEnteries = (Button) findViewById(R.id.buttonDeleteAllEnteries);

		buttonQuickAddInsert.setOnClickListener(this);
		buttonShowTodayEntries.setOnClickListener(this);
		buttonAddReminder.setOnClickListener(this);
		buttonShowWeeklyEntries.setOnClickListener(this);
		buttonShowReminderEntries.setOnClickListener(this);
		buttonShowMonthEntries.setOnClickListener(this);
		buttonCategoriesEdit.setOnClickListener(this);
		buttonDeleteAllEnteries.setOnClickListener(this);

	}

	private void initTabs() {

		Resources resources = getResources();
		TextView tabContent;

		// setting up the tabs
		thMain = (TabHost) findViewById(R.id.tabhostMain);
		thMain.setup();

		thMain.setOnTabChangedListener(this);

		// setup general tab
		tabSpec = thMain.newTabSpec(TAB_TAG_GENERAL);
		tabSpec.setContent(R.id.tabGeneral);
		tabContent = new TextView(this);
		tabContent.setText(getString(R.string.tabGeneralText));

		tabContent.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		tabContent.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(tabContent);

		thMain.addTab(tabSpec);
		

		// setup Reminders tab
		tabSpec = thMain.newTabSpec(TAB_TAG_REMINDERS);
		tabSpec.setContent(R.id.tabReminders);

		tabContent = new TextView(this);
		tabContent.setText(getString(R.string.tabRemindersText));

		tabContent.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		tabContent.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(tabContent);

		thMain.addTab(tabSpec);

		// setup Entries tab
		tabSpec = thMain.newTabSpec(TAB_TAG_ENTRIES);
		tabSpec.setContent(R.id.tabEntries);
		tabContent = new TextView(this);
		tabContent.setText(getString(R.string.tabEntriesText));

		tabContent.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		tabContent.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(tabContent);

		thMain.addTab(tabSpec);

	}

	private void initEditTexts() {
		//
		etDayInMonthReminder = (EditText) findViewById(R.id.editTextDayInMonth);
		etDayInMonthReminder.setVisibility(View.GONE);
		editTextQuickAddAmount = (EditText) findViewById(R.id.editTextQuickAddAmount);
		editTextComment = (EditText) findViewById(R.id.editTextComment);

	}

	private void initCheckBoxes() {
		//

		checkBoxAutoAddReminder = (CheckBox) findViewById(R.id.checkBoxAutoAddReminder);

		cbSunday = (CheckBox) findViewById(R.id.checkBoxSunday);
		cbMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
		cbTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
		cbThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
		cbFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
		cbSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);

	}

	private void initRadioButtons() {
		//
		radioGroupReminder = (RadioGroup) findViewById(R.id.radioGroupReminders);

		radioGroupReminder.setOnCheckedChangeListener(this);

	}

	private void setCheckboxesVisible(int i_Visibility) {
		//
		cbSunday.setVisibility(i_Visibility);
		cbMonday.setVisibility(i_Visibility);
		cbTuesday.setVisibility(i_Visibility);
		cbWednesday.setVisibility(i_Visibility);
		cbThursday.setVisibility(i_Visibility);
		cbFriday.setVisibility(i_Visibility);
		cbSaturday.setVisibility(i_Visibility);

	}

	@Override
	protected void onStart() {

		super.onStart();

		initSpinnerCategories();
	}

	@Override
	protected void onDestroy() {
		//
		if (this.getIntent().getExtras() != null) {
			this.getIntent().getExtras().clear();
		}
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		//
		super.onStop();

		Log.d(TAG, "onStop start");

	}

	private void initSpinnerCategories() {
		//
		try {

			m_Categories = m_SpendingTrackerDbEngine.getCategories();
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, m_Categories);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.buttonCategoriesEdit:
			buttonCategoriesEdit();
			break;

		case R.id.buttonShowReminderEntries:
			buttonShowReminderEnteries_Clicked();
			break;
		// User wants to add entry to database
		case R.id.buttonQuickAddInsert:
			buttonAddEntry_Clicked();

			break;

		case R.id.buttonAddReminder:
			buttonAddReminderToDatabase_Clicked();
			break;

		case R.id.buttonShowMonthEntries:
			buttonShowMonthEntries_Clicked();
			break;

		case R.id.buttonShowTodayEntries:
			bShowTodayEnteriesClick();
			break;

		case R.id.buttonShowWeeklyEntries:
			buttonShowWeeklyEntries_Clicked();
			break;

		case R.id.buttonDeleteAllEnteries:
			buttonDeleteAllEnteries_Clicked();
			break;

		default:
			break;
		}

	}

	private void buttonShowMonthEntries_Clicked() {
		//
		Intent i = new Intent(this, ViewEntriesSpent.class);
		i.putExtra("TYPE", ViewEntriesSpent.TYPE_MONTH);
		startActivity(i);

	}

	private void buttonDeleteAllEnteries_Clicked() {
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle("Delete all entries");
		alertDialog.setMessage("Are you sure you want to delete all entries?");
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						m_SpendingTrackerDbEngine.deleteAll();
						updateDaySpent();
						updateMonthSpent();
						updateWeekSpent();

						initSpinnerCategories();
					}
				});
		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING

					}
				});

		alertDialog.show();

	}

	private void buttonShowWeeklyEntries_Clicked() {
		//
		Intent i = new Intent(this, ViewEntriesSpent.class);
		i.putExtra("TYPE", ViewEntriesSpent.TYPE_WEEK);
		startActivity(i);

	}

	private void buttonCategoriesEdit() {
		//
		Intent intent = new Intent(SpendingTrackerActivity.this,
				CategoriesManager.class);
		startActivity(intent);

	}

	private void bShowTodayEnteriesClick() {
		//
		Intent i = new Intent(this, ViewEntriesSpent.class);
		i.putExtra("TYPE", ViewEntriesSpent.TYPE_TODAY);
		startActivity(i);

	}

	private void buttonShowReminderEnteries_Clicked() {
		//
		Intent i = new Intent(this, ViewEntriesReminders.class);

		startActivity(i);

	}

	private void buttonAddEntry_Clicked() {
		// getting amount from edit text
		String amountToAdd = editTextQuickAddAmount.getText().toString();

		// getting comments
		String comments = editTextComment.getText().toString();

		// checking if user chose category and entered amount
		if ((amountToAdd.length() > 0) && (m_CategorySelected.length() > 0)) {

			try {
				m_SpendingTrackerDbEngine.insertNewSpending(amountToAdd,
						m_CategorySelected, comments, null);

				// showing message to user that entry was added
				if (m_SharedPreferences.getBoolean("cbShowEntryAdded", true)) {
					Toast.makeText(SpendingTrackerActivity.this, "Entry added",
							Toast.LENGTH_SHORT).show();
				}

				// update edit texts
				updateDaySpent();
				updateWeekSpent();
				updateMonthSpent();

				// if the add reminder is checked do not clear the texts and
				// change to tab reminders
				if (checkBoxAutoAddReminder.isChecked()) {

					thMain.setCurrentTabByTag(TAB_TAG_REMINDERS);
				} else {
					// initialize edit text
					editTextQuickAddAmount.setText("");
					editTextComment.setText("");
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "Error adding entry");
			}
		} else {
			Dialog d = new Dialog(this);
			d.setTitle("Error adding entry");
			TextView message = new TextView(this);
			message.setText("Either amount is not filled or category is not chosen!");
			d.setContentView(message);
			d.show();

		}
	}

	private void buttonAddReminderToDatabase_Clicked() {
		// Functions adds the reminder to the database
		String currentHour = timePickerDay.getCurrentHour().toString();
		String currentMinute = timePickerDay.getCurrentMinute().toString();
		String amount = editTextQuickAddAmount.getText().toString();
		StringBuilder sb = new StringBuilder();

		if (currentMinute.length() == 1) {
			currentMinute = "0" + currentMinute;
		}

		switch (radioGroupReminder.getCheckedRadioButtonId()) {
		case R.id.radioButtonEveryday:
			//
			m_SpendingTrackerDbEngine.insertNewReminder(
					SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY,
					currentHour, currentMinute,
					SpendingTrackerDbEngine.TYPE_REMINDER_DAY_DONT_CARE,
					amount, m_CategorySelected);

			sb.append("Added Everyday reminder at " + currentHour + ":"
					+ currentMinute);

			break;
		case R.id.radioButtonWeekly:

			if (cbSunday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_SUNDAY, amount,
						m_CategorySelected);
				sb.append(" Sunday");
			}
			if (cbMonday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_MONDAY, amount,
						m_CategorySelected);
				sb.append(" Monday");
			}
			if (cbTuesday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_TUESDAY, amount,
						m_CategorySelected);
				sb.append(" Tuesday");
			}
			if (cbWednesday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_WEDNESDAY,
						amount, m_CategorySelected);
				sb.append(" Wednesday");
			}
			if (cbThursday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_THURSDAY, amount,
						m_CategorySelected);
				sb.append(" Thursday");
			}
			if (cbFriday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_FRIDAY, amount,
						m_CategorySelected);
				sb.append(" Friday");
			}
			if (cbSaturday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_SATURDAY, amount,
						m_CategorySelected);
				sb.append(" Saturday");
			}
			if (sb.length() > 0) {
				sb.insert(0, "Added weekly reminder on ");
				sb.append(" with time " + currentHour + ":" + currentMinute);

			}

			sb.setLength(0);

			break;

		case R.id.radioButtonMonthly:

			m_SpendingTrackerDbEngine.insertNewReminder(
					SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY, currentHour,
					currentMinute, etDayInMonthReminder.getText().toString(),
					amount, m_CategorySelected);
			sb.append("Added Monthly reminder at " + currentHour + ":"
					+ currentMinute);
			break;
		default:

			break;
		}

		if (sb.length() > 0) {
			Log.i(TAG, sb.toString());
			Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
		} else {
			Log.i(TAG, "error occoured during addReminderToDatabase");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.mainmenu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean ret = true;

		switch (item.getItemId()) {
		case R.id.menuAbout:
			menuAbout_Clicked();
			

			break;

		case R.id.menuExit:

			finish();
			break;

		case R.id.menuPrefernces:
			menuPrefernces_Clicked();
			
		default:
			ret = super.onOptionsItemSelected(item);
		}

		return ret;

	}

	private void menuPrefernces_Clicked() {
		// 
		Intent pref = new Intent(
		"com.tzachsolomon.spendingtracker.PREFERENCES");

		startActivity(pref);
		
	}

	private void menuAbout_Clicked() {
		// 
		Intent intent = new Intent(this,About.class);
		
		startActivity(intent);
		
		
		
	}

	private void updateMonthSpent() {
		//
		textViewSpentMonth.setText(getString(R.string.textViewSpentMonthText)
				+ m_SpendingTrackerDbEngine.getSpentThisMonth());

	}

	private void updateWeekSpent() {
		// TODO: change 1 to variable indicating first day of week
		textViewSpentWeek.setText(getString(R.string.textViewSpentWeekText)
				+ m_SpendingTrackerDbEngine.getSpentThisWeek(1));

	}

	private void updateDaySpent() {

		textViewSpentToday.setText(getString(R.string.textViewSpentTodayText)
				+ m_SpendingTrackerDbEngine.getSpentToday());

	}

	@Override
	public void onTabChanged(String tabId) {
		//
		if (TAB_TAG_REMINDERS.contentEquals(tabId)) {
			timePickerDay.setCurrentHour(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
			timePickerDay.setCurrentMinute(Calendar.getInstance().get(
					Calendar.MINUTE));
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//
		switch (checkedId) {
		case R.id.radioButtonEveryday:
			setCheckboxesVisible(View.GONE);
			etDayInMonthReminder.setVisibility(View.GONE);
			break;

		case R.id.radioButtonWeekly:
			setCheckAccordingToDate();

			setCheckboxesVisible(View.VISIBLE);
			etDayInMonthReminder.setVisibility(View.GONE);

			break;

		case R.id.radioButtonMonthly:
			setCheckboxesVisible(View.GONE);
			etDayInMonthReminder.setVisibility(View.VISIBLE);
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
			cbSunday.setChecked(true);
			break;

		case Calendar.MONDAY:
			cbMonday.setChecked(true);
			break;

		case Calendar.TUESDAY:
			cbTuesday.setChecked(true);
			break;

		case Calendar.WEDNESDAY:
			cbWednesday.setChecked(true);
			break;

		case Calendar.THURSDAY:
			cbThursday.setChecked(true);
			break;

		case Calendar.FRIDAY:
			cbFriday.setChecked(true);
			break;

		case Calendar.SATURDAY:
			cbSaturday.setChecked(true);
			break;

		default:
			break;
		}

	}
}