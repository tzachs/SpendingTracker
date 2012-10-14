package com.tzachsolomon.spendingtracker;

import java.util.Calendar;

import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.location.Location;
import android.location.LocationListener;


import android.os.Bundle;
import android.os.SystemClock;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
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
		OnClickListener, OnTabChangeListener, OnCheckedChangeListener,
		LocationListener {

	// TODO: export to Google Document
	// TODO: add option to place tab on bottom instead of up
	// TODO: add colors to categories
	// TODO: add graphs
	// TODO: auto export database (auto backup)
	// TODO: auto import in case re installing the app
	// TODO: add learning mode

	/** Called when the activity is first created. */
	private static final String TAG = SpendingTrackerActivity.class
			.getSimpleName();

	private TabHost tabHostMain;
	private TabSpec tabSpec;

	private EditText editTextQuickAddAmount;
	private EditText editTextComment;
	private EditText editTextDayInMonthReminder;
	private EditText editTextLocationName;

	private Button buttonQuickAddInsert;
	private Button buttonShowTodayEntries;
	private Button buttonShowWeeklyEntries;
	private Button buttonShowMonthEntries;
	private Button buttonShowTimeReminderEntries;
	private Button buttonShowLocationReminderEntries;
	private Button buttonAddTimeReminder;
	private Button buttonAddLocationReminder;
	private Button buttonCategoriesEdit;
	private Button buttonDeleteAllEnteries;

	private TextView textViewSpentToday;
	private TextView textViewSpentWeek;
	private TextView textViewSpentMonth;

	private TextView textViewTabTextGeneral;
	private TextView textViewTabTextTimeReminders;
	private TextView textViewTabTextLocationReminders;
	private TextView textViewTabTextEntries;

	private TextView textViewAccuracyLabel;
	private TextView textViewAccuracyText;
	private TextView textViewAltitudeLabel;
	private TextView textViewAltitudeText;
	private TextView textViewBearingLabel;
	private TextView textViewBearingText;
	private TextView textViewLatitudeLabel;
	private TextView textViewLatitudeText;
	private TextView textViewLongitudeLabel;
	private TextView textViewLongitudeText;
	private TextView textViewProviderLabel;
	private TextView textViewProviderText;
	private TextView textViewSpeedLabel;
	private TextView textViewSpeedText;
	private TextView textViewTimeLabel;
	
	private TextView textViewTimeText;

	private Spinner spinnerCategories;
	private TimePicker timePickerDay;

	private CheckBox checkBoxSunday, checkBoxMonday, checkBoxTuesday,
			checkBoxWednesday, checkBoxThursday, checkBoxFriday,
			checkBoxSaturday;

	private RadioGroup radioGroupReminder;

	private final String TAB_TAG_GENERAL = "tagGeneral";
	private final String TAB_TAG_ENTRIES = "tagEntries";
	private final String TAB_TAG_TIME_REMINDERS = "tagTimeReminders";
	private final String TAB_TAG_LOCATION_REMINDERS = "tagLocationReminders";

	private SharedPreferences m_SharedPreferences;

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private String m_CategorySelected;
	private String[] m_Categories;

	private PendingIntent m_TimeAlarmSender;
	private PendingIntent m_LocationAlarmSender;

	private boolean m_DebugMode;
	
	NotificationManager nm;

	private BroadcastReceiver m_BroadcastReceiverLocationUpdate;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// waiting for the debugger to attach in order to the debug the service
		// android.os.Debug.waitForDebugger();

		m_TimeAlarmSender = PendingIntent.getService(
				SpendingTrackerActivity.this, 0, new Intent(
						SpendingTrackerActivity.this,
						SpendingTrackerTimeService.class), 0);
		m_LocationAlarmSender = PendingIntent.getService(
				SpendingTrackerActivity.this, 0, new Intent(
						SpendingTrackerActivity.this,
						SpendingTrackerLocationService.class), 0);
		initPreferences();

		initVariables();

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		

	}

	private void updateLocale() {
		//
		String language = m_SharedPreferences.getString("prefAppLanguage",
				"English");
		String languageToLoad = "en";

		if (language.contentEquals("Italian")) {

			languageToLoad = "it";

		}
		Locale locale = new Locale(languageToLoad);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		this.getBaseContext().getResources().updateConfiguration(config, null);

	}

	private void updateStringsFromResource() {

		spinnerCategories.setPrompt(getString(R.string.spinnerPrompt));

		textViewSpentMonth.setText(getString(R.string.textViewSpentMonthText));
		textViewSpentToday.setText(getString(R.string.textViewSpentTodayText));
		textViewSpentWeek.setText(getString(R.string.textViewSpentWeekText));

		textViewTabTextEntries
				.setText(getString(R.string.textViewTabTextEntries));
		textViewTabTextGeneral
				.setText(getString(R.string.textViewTabTextGeneral));
		textViewTabTextTimeReminders
				.setText(getString(R.string.textViewTabTextTimeReminders));
		textViewTabTextLocationReminders
				.setText(getString(R.string.textViewTabTextLocationReminders));
		textViewAccuracyLabel
				.setText(getString(R.string.textViewAccuracyLabel));
		textViewAltitudeLabel
				.setText(getString(R.string.textViewAltitudeLabel));
		textViewBearingLabel.setText(getString(R.string.textViewBearingLabel));
		textViewLatitudeLabel
				.setText(getString(R.string.textViewLatitudeLabel));
		textViewLongitudeLabel
				.setText(getString(R.string.textViewLongitudeLabel));
		textViewProviderLabel
				.setText(getString(R.string.textViewProviderLabel));
		textViewSpeedLabel.setText(getString(R.string.textViewSpeedLabel));
		textViewTimeLabel.setText(getString(R.string.textViewTimeLabel));

		editTextComment.setHint(getString(R.string.editTextCommentHint));
		editTextDayInMonthReminder
				.setHint(getString(R.string.editTextDayInMonthHint));
		editTextQuickAddAmount
				.setHint(getString(R.string.editTextQuickAddAmountHint));

		buttonAddTimeReminder
				.setText(getString(R.string.buttonAddReminderText));
		buttonAddLocationReminder
				.setText(getString(R.string.buttonAddReminderText));
		buttonCategoriesEdit
				.setText(getString(R.string.buttonCategoriesEditText));
		buttonDeleteAllEnteries
				.setText(getString(R.string.buttonDeleteAllEnteriesText));
		buttonQuickAddInsert
				.setText(getString(R.string.buttonQuickAddInsertText));
		buttonShowMonthEntries
				.setText(getString(R.string.buttonShowMonthEntriesText));
		buttonShowTimeReminderEntries
				.setText(getString(R.string.buttonShowTimeReminderEntriesText));
		buttonShowTodayEntries
				.setText(getString(R.string.buttonShowTodayEntriesText));
		buttonShowWeeklyEntries
				.setText(getString(R.string.buttonShowWeeklyEntriesText));

		
		checkBoxFriday.setText(getString(R.string.checkBoxFridayText));
		checkBoxMonday.setText(getString(R.string.checkBoxMondayText));
		checkBoxSaturday.setText(getString(R.string.checkBoxSaturdayText));
		checkBoxSunday.setText(getString(R.string.checkBoxSundayText));
		checkBoxThursday.setText(getString(R.string.checkBoxThursdayText));
		checkBoxTuesday.setText(getString(R.string.checkBoxTuesdayText));
		checkBoxWednesday.setText(getString(R.string.checkBoxWednesdayText));

	}

	private void cancelTimeAlarmManager() {
		//
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(m_TimeAlarmSender);
	}

	private void cancelLocationAlarmManager() {
		//
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(m_LocationAlarmSender);
	}

	private void startTimeAlarmManager() {
		//
		Calendar firstTime = Calendar.getInstance();
		firstTime.setTimeInMillis(SystemClock.elapsedRealtime());

		int secondsToAdd = 60 - firstTime.get(Calendar.SECOND);

		Log.v(TAG, "Starting Time service using alarm manager in " + secondsToAdd
				+ " Seconds");

		firstTime.add(Calendar.SECOND, secondsToAdd);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				firstTime.getTimeInMillis(), 60000, m_TimeAlarmSender);

	}

	private void startLocationAlarmManager() {
		//
		Calendar firstTime = Calendar.getInstance();
		firstTime.setTimeInMillis(SystemClock.elapsedRealtime());

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				firstTime.getTimeInMillis(), 60000, m_LocationAlarmSender);

	}

	private void initPreferences() {
		m_SharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		m_DebugMode = m_SharedPreferences.getBoolean("checkBoxPreferenceDebug",
				false);

	}

	@Override
	protected void onResume() {

		super.onResume();

		initPreferences();
		
		startLocationService();
		
		updateLocale();
		updateStringsFromResource();

		checkServiceStatus();
		checkPendingReminder();

		updateDaySpent();
		updateWeekSpent();
		updateMonthSpent();

	}

	@Override
	protected void onPause() {
		//
		super.onPause();

		unregisterReceiver(m_BroadcastReceiverLocationUpdate);
		stopLocationService();
		
		}

	private void checkPendingReminder() {
		// Function checks if there is a pending reminder
		// Currently only support one reminder

		final EditText editTextAmount;
		
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
			} else if (extras
					.containsKey(SpendingTrackerDbEngine.KEY_REMINDER_TYPE)) {
				Log.d(TAG, "Starting from location reminder");
				isReminder = true;
				String rowId = extras
						.getString(SpendingTrackerDbEngine.KEY_REMINDER_ID);
				m_SpendingTrackerDbEngine
						.changeLocationReminderToPressed(rowId);
			}

			if (isReminder) {
				final CheckBox checkBoxAutoExit;
				final String amount = extras
				.getString(SpendingTrackerDbEngine.KEY_AMOUNT);
		final String category = extras
				.getString(SpendingTrackerDbEngine.KEY_CATEGORY);
				
				int notificationId = extras.getInt("notificationId");
				
				// Cancel notification
				nm.cancel(notificationId);
				nm.cancel(12021982);

				LayoutInflater factory = LayoutInflater.from(this);
				final View notifyLayout;
				notifyLayout = factory.inflate(R.layout.notify_layout, null);
				editTextAmount = (EditText) notifyLayout.findViewById(R.id.editTextNotifyAmount);
				editTextAmount.setText(amount);
				checkBoxAutoExit = (CheckBox)notifyLayout.findViewById(R.id.checkBoxNotifyAutoExit);
				checkBoxAutoExit.setChecked(true);
				
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				StringBuilder sb = new StringBuilder();
				getIntent().removeExtra(SpendingTrackerDbEngine.KEY_AMOUNT);
				getIntent().removeExtra(SpendingTrackerDbEngine.KEY_CATEGORY);

				sb.append(getString(R.string.stringDidYouSpend));
				sb.append(' ');
				sb.append(category);
				sb.append(' ');
				sb.append("?");

				alertDialog
						.setTitle(getString(R.string.spentMoneyDialogTitleText));

				alertDialog.setMessage(sb.toString());
				alertDialog.setView(notifyLayout);

				alertDialog.setPositiveButton(
						getString(R.string.dialogPositiveYes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//
								EditText editTextAmount = (EditText)notifyLayout.findViewById(R.id.editTextNotifyAmount);
								
								m_SpendingTrackerDbEngine
										.insertNewSpending(editTextAmount.getText().toString(), category,
												"From reminder", null);

								if (checkBoxAutoExit.isChecked()) {
									finish();
								}

							}
						});
				alertDialog.setNegativeButton(
						getString(R.string.dialogPositiveNo),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
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

	private void checkServiceStatus() {
		//
		try {
			boolean checkBoxPreferencsReminderService = m_SharedPreferences
					.getBoolean("checkBoxPreferencsReminderService", true);

			// checking service always should be on and the service is not
			// running
			cancelTimeAlarmManager();

			if (checkBoxPreferencsReminderService) {

				startTimeAlarmManager();
				tabHostMain.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
			}
			else {
				tabHostMain.getTabWidget().getChildAt(1).setVisibility(View.GONE);
			}
			

		} catch (Exception e) {
			if (m_DebugMode) {
				Toast.makeText(this, e.getMessage().toString(),
						Toast.LENGTH_SHORT).show();
			}
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
			if (m_DebugMode) {
				Toast.makeText(this, e.getMessage().toString(),
						Toast.LENGTH_SHORT).show();
			}

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

		textViewAccuracyText = (TextView) findViewById(R.id.textViewAccuracyText);
		textViewAccuracyLabel = (TextView) findViewById(R.id.textViewAccuracyLabel);

		textViewAltitudeText = (TextView) findViewById(R.id.textViewAltitudeText);
		textViewAltitudeLabel = (TextView) findViewById(R.id.textViewAltitudeLabel);

		textViewBearingText = (TextView) findViewById(R.id.textViewBearingText);
		textViewBearingLabel = (TextView) findViewById(R.id.textViewBearingLabel);

		textViewLatitudeText = (TextView) findViewById(R.id.textViewLatitudeText);
		textViewLatitudeLabel = (TextView) findViewById(R.id.textViewLatitudeLabel);

		textViewLongitudeText = (TextView) findViewById(R.id.textViewLongitudeText);
		textViewLongitudeLabel = (TextView) findViewById(R.id.textViewLongitudeLabel);

		textViewProviderText = (TextView) findViewById(R.id.textViewProviderText);
		textViewProviderLabel = (TextView) findViewById(R.id.textViewProviderLabel);

		textViewSpeedText = (TextView) findViewById(R.id.textViewSpeedText);
		textViewSpeedLabel = (TextView) findViewById(R.id.textViewSpeedLabel);

		textViewTimeText = (TextView) findViewById(R.id.textViewTimeText);
		textViewTimeLabel = (TextView) findViewById(R.id.textViewTimeLabel);

	}

	private void initButtons() {
		//
		buttonQuickAddInsert = (Button) findViewById(R.id.buttonQuickAddInsert);

		buttonShowTodayEntries = (Button) findViewById(R.id.buttonShowTodayEntries);
		buttonShowMonthEntries = (Button) findViewById(R.id.buttonShowMonthEntries);
		buttonShowTimeReminderEntries = (Button) findViewById(R.id.buttonShowTimeReminderEntries);
		buttonShowLocationReminderEntries = (Button) findViewById(R.id.buttonShowLocationReminderEntries);
		buttonShowWeeklyEntries = (Button) findViewById(R.id.buttonShowWeeklyEntries);

		buttonAddTimeReminder = (Button) findViewById(R.id.buttonAddTimeReminder);
		buttonAddLocationReminder = (Button) findViewById(R.id.buttonAddLocationReminder);
		buttonCategoriesEdit = (Button) findViewById(R.id.buttonCategoriesEdit);
		buttonDeleteAllEnteries = (Button) findViewById(R.id.buttonDeleteAllEnteries);

		buttonQuickAddInsert.setOnClickListener(this);
		buttonShowTodayEntries.setOnClickListener(this);
		buttonAddTimeReminder.setOnClickListener(this);
		buttonAddLocationReminder.setOnClickListener(this);
		buttonShowWeeklyEntries.setOnClickListener(this);
		buttonShowTimeReminderEntries.setOnClickListener(this);
		buttonShowLocationReminderEntries.setOnClickListener(this);
		buttonShowMonthEntries.setOnClickListener(this);
		buttonCategoriesEdit.setOnClickListener(this);
		buttonDeleteAllEnteries.setOnClickListener(this);

	}

	private void initTabs() {

		Resources resources = getResources();

		// setting up the tabs
		tabHostMain = (TabHost) findViewById(R.id.tabhostMain);
		tabHostMain.setup();

		tabHostMain.setOnTabChangedListener(this);

		// setup general tab
		tabSpec = tabHostMain.newTabSpec(TAB_TAG_GENERAL);
		tabSpec.setContent(R.id.tabGeneral);
		textViewTabTextGeneral = new TextView(this);
		textViewTabTextGeneral
				.setText(getString(R.string.textViewTabTextGeneral));

		textViewTabTextGeneral.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		textViewTabTextGeneral.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(textViewTabTextGeneral);

		tabHostMain.addTab(tabSpec);

		// setup Time Reminders tab
		tabSpec = tabHostMain.newTabSpec(TAB_TAG_TIME_REMINDERS);
		tabSpec.setContent(R.id.tabReminders);

		textViewTabTextTimeReminders = new TextView(this);
		textViewTabTextTimeReminders
				.setText(getString(R.string.textViewTabTextTimeReminders));

		textViewTabTextTimeReminders.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		textViewTabTextTimeReminders
				.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(textViewTabTextTimeReminders);

		tabHostMain.addTab(tabSpec);

		// setup Location Reminders tab
		tabSpec = tabHostMain.newTabSpec(TAB_TAG_LOCATION_REMINDERS);
		tabSpec.setContent(R.id.tabLocationReminders);

		textViewTabTextLocationReminders = new TextView(this);
		textViewTabTextLocationReminders
				.setText(getString(R.string.textViewTabTextLocationReminders));

		textViewTabTextLocationReminders.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		textViewTabTextLocationReminders
				.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(textViewTabTextLocationReminders);

		tabHostMain.addTab(tabSpec);

		// setup Entries tab
		tabSpec = tabHostMain.newTabSpec(TAB_TAG_ENTRIES);
		tabSpec.setContent(R.id.tabEntries);
		textViewTabTextEntries = new TextView(this);
		textViewTabTextEntries
				.setText(getString(R.string.textViewTabTextEntries));

		textViewTabTextEntries.setBackgroundDrawable(resources
				.getDrawable(R.drawable.custom_tab));
		textViewTabTextEntries.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tabSpec.setIndicator(textViewTabTextEntries);

		tabHostMain.addTab(tabSpec);

	}

	private void initEditTexts() {
		//
		editTextDayInMonthReminder = (EditText) findViewById(R.id.editTextDayInMonth);
		editTextDayInMonthReminder.setVisibility(View.GONE);
		editTextQuickAddAmount = (EditText) findViewById(R.id.editTextQuickAddAmount);
		editTextComment = (EditText) findViewById(R.id.editTextComment);
		editTextLocationName = (EditText) findViewById(R.id.editTextLocationName);

	}

	private void initCheckBoxes() {
		//

		

		checkBoxSunday = (CheckBox) findViewById(R.id.checkBoxSunday);
		checkBoxMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
		checkBoxTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
		checkBoxWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
		checkBoxThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
		checkBoxFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
		checkBoxSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);

	}

	private void initRadioButtons() {
		//
		radioGroupReminder = (RadioGroup) findViewById(R.id.radioGroupReminders);

		radioGroupReminder.setOnCheckedChangeListener(this);

	}

	private void setCheckboxesVisible(int i_Visibility) {
		//
		checkBoxSunday.setVisibility(i_Visibility);
		checkBoxMonday.setVisibility(i_Visibility);
		checkBoxTuesday.setVisibility(i_Visibility);
		checkBoxWednesday.setVisibility(i_Visibility);
		checkBoxThursday.setVisibility(i_Visibility);
		checkBoxFriday.setVisibility(i_Visibility);
		checkBoxSaturday.setVisibility(i_Visibility);

	}

	@Override
	protected void onStart() {

		super.onStart();

		initSpinnerCategories();

	}

	/**
	 * Function will start the location service and cancel the location alarm
	 * manager in order prevent from the alarm manager to stop the location
	 * service
	 * 
	 */
	private void startLocationService() {
		//

		boolean checkBoxPreferencesLocationService = m_SharedPreferences
				.getBoolean("checkBoxPreferencesLocationService", false);
		
		cancelLocationAlarmManager();

		// checking the location service option is enabled
		if (checkBoxPreferencesLocationService) {
			// the location service is enabled, thus disabling the alarm
			// location manager

			Intent service = new Intent(this,
					SpendingTrackerLocationService.class);

			startService(service);

			tabHostMain.getTabWidget().getChildAt(2).setVisibility(View.VISIBLE);
		} else {
			tabHostMain.getTabWidget().getChildAt(2).setVisibility(View.GONE);
			
		}

		IntentFilter iFilter = new IntentFilter(
				SpendingTrackerLocationService.ACTION_FILTER);

		m_BroadcastReceiverLocationUpdate = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				//
				Location location = (Location) intent.getExtras().get(
						"location");
				if ( m_DebugMode ){
					Toast.makeText(SpendingTrackerActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
				}
				updateLocationTextViews(location);

			}
		};

		registerReceiver(m_BroadcastReceiverLocationUpdate, iFilter);

	}
	
	/**
	 * Function will start the location service and cancel the location alarm
	 * manager in order prevent from the alarm manager to stop the location
	 * service
	 * 
	 */
	private void stopLocationService() {
		//

		boolean checkBoxPreferencesLocationService = m_SharedPreferences
				.getBoolean("checkBoxPreferencesLocationService", false);

		Intent service = new Intent(this,
				SpendingTrackerLocationService.class);

		stopService(service);
		
		// checking the location service option is enabled
		if (checkBoxPreferencesLocationService) {
			// the location service is enabled, thus disabling the alarm
			// location manager
			startLocationAlarmManager();

		}

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

		

	}

	private void initSpinnerCategories() {
		//
		try {

			m_Categories = m_SpendingTrackerDbEngine.getCategories();
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, m_Categories);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

		} catch (Exception e) {
			if (m_DebugMode) {
				Toast.makeText(this, e.getMessage().toString(),
						Toast.LENGTH_SHORT).show();
			}

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

		case R.id.buttonShowTimeReminderEntries:
			buttonShowTimeReminderEnteries_Clicked();
			break;
		case R.id.buttonShowLocationReminderEntries:
			buttonShowLocationReminderEnteries_Clicked();
			break;
		// User wants to add entry to database
		case R.id.buttonQuickAddInsert:
			buttonAddEntry_Clicked();
			break;

		case R.id.buttonAddTimeReminder:
			buttonAddTimeReminderToDatabase_Clicked();
			break;

		case R.id.buttonAddLocationReminder:
			buttonAddLocationReminder_Clicked();
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

	private void buttonShowLocationReminderEnteries_Clicked() {
		//
		Intent i = new Intent(this, ViewEntriesRemindersLocation.class);

		startActivity(i);

	}

	private void buttonAddLocationReminder_Clicked() {
		//
		String amount = editTextQuickAddAmount.getText().toString();
		String locationName = editTextLocationName.getText().toString();

		if (locationName.length() > 0) {

			if (amount.length() == 0) {
				amount = "-1";

			}

			m_SpendingTrackerDbEngine.insertNewLocationReminder(
					textViewAccuracyText.getText().toString(),
					textViewAltitudeText.getText().toString(),
					textViewBearingText.getText().toString(),
					textViewLatitudeText.getText().toString(),
					textViewLongitudeText.getText().toString(),
					textViewProviderText.getText().toString(),
					textViewSpeedText.getText().toString(), textViewTimeText
							.getText().toString(), amount, m_CategorySelected,
					locationName);

		} else {
			Toast.makeText(this, "You must enter location name",
					Toast.LENGTH_SHORT).show();
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

		alertDialog.setTitle(getString(R.string.dialogTitleDeleteAllEntries));
		alertDialog
				.setMessage(getString(R.string.dialogMessageDeleteAllEntries));
		alertDialog.setPositiveButton(getString(R.string.dialogPositiveYes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						m_SpendingTrackerDbEngine.deleteSpentEntries();
						
						updateDaySpent();
						updateMonthSpent();
						updateWeekSpent();

						initSpinnerCategories();
					}
				});
		alertDialog.setNegativeButton(getString(R.string.dialogPositiveNo),
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

	private void buttonShowTimeReminderEnteries_Clicked() {
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
					Toast.makeText(SpendingTrackerActivity.this,
							getString(R.string.toastMessageEntryAdded),
							Toast.LENGTH_SHORT).show();
				}

				// update edit texts
				updateDaySpent();
				updateWeekSpent();
				updateMonthSpent();
				
			} catch (Exception e) {
				if (m_DebugMode) {
					Toast.makeText(this, e.getMessage().toString(),
							Toast.LENGTH_SHORT).show();
				}
				e.printStackTrace();
				Log.e(TAG,
						getString(R.string.dialogTitleErrorAddingEntryToUser));
			}
		} else {
			Dialog d = new Dialog(this);
			d.setTitle(getString(R.string.dialogTitleErrorAddingEntryToUser));
			TextView message = new TextView(this);
			message.setText(getString(R.string.dialogMessageErrorAddingSpentEntryToUser));
			d.setContentView(message);
			d.show();

		}
	}

	private void buttonAddTimeReminderToDatabase_Clicked() {
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
			m_SpendingTrackerDbEngine.insertNewTimeReminder(
					SpendingTrackerDbEngine.TYPE_REMINDER_EVERYDAY,
					currentHour, currentMinute,
					SpendingTrackerDbEngine.TYPE_REMINDER_DAY_DONT_CARE,
					amount, m_CategorySelected);

			sb.append(getString(R.string.toastMessageAddedEverydayReminder)
					+ currentHour + ":" + currentMinute);

			break;
		case R.id.radioButtonWeekly:

			if (checkBoxSunday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_SUNDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxSundayText));
			}
			if (checkBoxMonday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_MONDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxMondayText));
			}
			if (checkBoxTuesday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_TUESDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxTuesdayText));
			}
			if (checkBoxWednesday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_WEDNESDAY,
						amount, m_CategorySelected);
				sb.append(getString(R.string.checkBoxWednesdayText));
			}
			if (checkBoxThursday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_THURSDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxThursdayText));
			}
			if (checkBoxFriday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_FRIDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxFridayText));
			}
			if (checkBoxSaturday.isChecked()) {
				m_SpendingTrackerDbEngine.insertNewTimeReminder(
						SpendingTrackerDbEngine.TYPE_REMINDER_WEEKLY,
						currentHour, currentMinute,
						SpendingTrackerDbEngine.TYPE_REMINDER_SATURDAY, amount,
						m_CategorySelected);
				sb.append(getString(R.string.checkBoxSaturdayText));
			}
			if (sb.length() > 0) {
				sb.insert(0,
						getString(R.string.toastMessageAddedWeeklyReminderOn));
				sb.append(getString(R.string.toastMessageWithTime)
						+ currentHour + ":" + currentMinute);

			}

			sb.setLength(0);

			break;

		case R.id.radioButtonMonthly:

			m_SpendingTrackerDbEngine.insertNewTimeReminder(
					SpendingTrackerDbEngine.TYPE_REMINDER_MONTHLY, currentHour,
					currentMinute, editTextDayInMonthReminder.getText()
							.toString(), amount, m_CategorySelected);
			sb.append(getString(R.string.toastMessageAddedMonthlyReminder)
					+ currentHour + ":" + currentMinute);
			break;
		default:

			break;
		}

		if (sb.length() > 0) {
			Log.i(TAG, sb.toString());
			Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
		} else {
			if (m_DebugMode) {
				Toast.makeText(this,
						"error occoured during addReminderToDatabase",
						Toast.LENGTH_LONG).show();
			}
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
			menuPreferences_Clicked();

		default:
			ret = super.onOptionsItemSelected(item);
		}

		return ret;

	}

	private void menuPreferences_Clicked() {
		//
		Intent pref = new Intent("com.tzachsolomon.spendingtracker.PREFERENCES");

		startActivity(pref);

	}

	private void menuAbout_Clicked() {
		//
		Intent intent = new Intent(this, About.class);

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

	private void updateLocationTextViews(Location location) {

		if (location != null) {

			textViewAccuracyText
					.setText(String.valueOf(location.getAccuracy()));
			textViewAltitudeText
					.setText(String.valueOf(location.getAltitude()));
			textViewBearingText.setText(String.valueOf(location.getBearing()));
			textViewLatitudeText
					.setText(String.valueOf(location.getLatitude()));
			textViewLongitudeText.setText(String.valueOf(location
					.getLongitude()));
			textViewProviderText
					.setText(String.valueOf(location.getProvider()));
			textViewSpeedText.setText(String.valueOf(location.getSpeed()));
			textViewTimeText.setText(String.valueOf(String.valueOf(location
					.getTime())));

		}
	}

	@Override
	public void onTabChanged(String tabId) {
		//
		if (TAB_TAG_TIME_REMINDERS.contentEquals(tabId)) {
			timePickerDay.setCurrentHour(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
			timePickerDay.setCurrentMinute(Calendar.getInstance().get(
					Calendar.MINUTE));
		} else if (TAB_TAG_LOCATION_REMINDERS.contentEquals(tabId)) {

		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//
		switch (checkedId) {
		case R.id.radioButtonEveryday:
			setCheckboxesVisible(View.GONE);
			editTextDayInMonthReminder.setVisibility(View.GONE);
			break;

		case R.id.radioButtonWeekly:
			setCheckAccordingToDate();

			setCheckboxesVisible(View.VISIBLE);
			editTextDayInMonthReminder.setVisibility(View.GONE);

			break;

		case R.id.radioButtonMonthly:
			setCheckboxesVisible(View.GONE);
			editTextDayInMonthReminder.setVisibility(View.VISIBLE);
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

	@Override
	public void onLocationChanged(Location location) {
		//
		updateLocationTextViews(location);

	}

	@Override
	public void onProviderDisabled(String provider) {
		// 

	}

	@Override
	public void onProviderEnabled(String provider) {
		// 

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// 

	}
}