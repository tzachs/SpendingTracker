package com.tzachsolomon.spendingtracker;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentEntries extends SherlockFragment implements
		OnCheckedChangeListener, OnClickListener {

	private ListView listViewEntriesSpent;
	private ClassAdapterEntriesSpent mEntriesSpent;
	private ClassAdapterEntriesSpentSummary mEntriesSpentSummary;
	private ClassDbEngine mDbEngine;
	private Calendar mCalendar;
	private RadioGroup radioGroupEntries;
	private SherlockFragmentActivity mActivity;
	private SpentEntryListener mSpentEntryListener;
	private Button buttonEntriesNext;
	private Button buttonEntriesBack;

	private RadioGroup radioGroupType;

	public interface SpentEntryListener {
		public void onSpentEntryDeleted(String rowId);

		public void onSpentEntryEditRequest(Bundle values);
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);
		mActivity = (SherlockFragmentActivity) activity;

		try {
			mSpentEntryListener = (SpentEntryListener) mActivity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement SpentEntryListener ");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_entries, null);

		initializeVariables(view);

		((ActivityMain) mActivity).setFragmentEntriesRef(getTag());

		return view;
	}

	private void initializeVariables(View view) {
		//
		listViewEntriesSpent = (ListView) view
				.findViewById(R.id.listViewEntriesSpent);
		radioGroupEntries = (RadioGroup) view
				.findViewById(R.id.radioGroupEntries);
		radioGroupEntries.setOnCheckedChangeListener(this);
		radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroupType);
		radioGroupType.setOnCheckedChangeListener(this);

		buttonEntriesNext = (Button) view.findViewById(R.id.buttonEntriesNext);
		buttonEntriesBack = (Button) view.findViewById(R.id.buttonEntriesBack);

		buttonEntriesNext.setOnClickListener(this);
		buttonEntriesBack.setOnClickListener(this);

		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());

	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonEntriesBack:
			buttonEntriesBack_Clicked();
			break;

		case R.id.buttonEntriesNext:
			buttonEntriesNext_Clicked();
			break;
		}

	}

	private void buttonEntriesBack_Clicked() {
		//
		updateListViewAdapter(-1);

	}

	private void buttonEntriesNext_Clicked() {
		//
		updateListViewAdapter(1);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(listViewEntriesSpent);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//
		if (radioGroupType.getCheckedRadioButtonId() == R.id.radioButtonDetails) {

			menu.add(Menu.NONE, 1, Menu.NONE, "Edit");
			menu.add(Menu.NONE, 2, Menu.NONE, "Delete");
			menu.add(Menu.NONE, 3, Menu.NONE, "Share");
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
			if (mSpentEntryListener != null) {
				mSpentEntryListener.onSpentEntryEditRequest(mEntriesSpent
						.getItemAsBundle(info.position));

			}

			break;
		case 2:
			if (mSpentEntryListener != null) {
				mSpentEntryListener.onSpentEntryDeleted(mEntriesSpent
						.getRowIdAtIndex(info.position));
			}

			break;
		case 3:
			break;

		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		mDbEngine = new ClassDbEngine(this.getSherlockActivity());
	}

	@Override
	public void onResume() {
		//
		super.onResume();
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());

		updateListViewAdapter();
	}

	public void updateListViewAdapter(int direction) {
		updateListViewAdapter(radioGroupEntries.getCheckedRadioButtonId(),
				direction);
	}

	private void updateListViewAdapter(int checkedId, int direction) {
		//
		boolean detailsChosen = false;
		if (radioGroupType.getCheckedRadioButtonId() == R.id.radioButtonDetails) {
			detailsChosen = true;
		}

		switch (checkedId) {
		case R.id.radioButtonEntiresMonthly:
			mCalendar.add(Calendar.MONTH, direction);
			if (detailsChosen) {
				mEntriesSpent = new ClassAdapterEntriesSpent(
						this.getSherlockActivity(),
						mDbEngine.getSpentThisMonthEnteries(mCalendar));
			} else {
				mEntriesSpentSummary = new ClassAdapterEntriesSpentSummary(
						this.getSherlockActivity(),
						mDbEngine.getSpentSummary(mCalendar));
			}
			break;
		case R.id.radioButtonEntriesEveryday:
			mCalendar.add(Calendar.DAY_OF_MONTH, direction);
			if (detailsChosen) {
				mEntriesSpent = new ClassAdapterEntriesSpent(
						this.getSherlockActivity(),
						mDbEngine.getSpentDailyEntries(mCalendar));
			} else {
				mEntriesSpentSummary = new ClassAdapterEntriesSpentSummary(
						this.getSherlockActivity(),
						mDbEngine.getSpentSummary(mCalendar));
			}
			break;
		case R.id.radioButtonEntriesWeekly:
			mCalendar.add(Calendar.DATE, direction * 7);
			if (detailsChosen) {
				mEntriesSpent = new ClassAdapterEntriesSpent(
						this.getSherlockActivity(),
						mDbEngine.getSpentThisWeekEnteries(1, mCalendar));
			} else {
				mEntriesSpentSummary = new ClassAdapterEntriesSpentSummary(
						this.getSherlockActivity(),
						mDbEngine.getSpentSummary(mCalendar));
			}
			break;

		}

		if (listViewEntriesSpent != null) {
			if (detailsChosen) {
				listViewEntriesSpent.setAdapter(mEntriesSpent);
			} else {
				listViewEntriesSpent.setAdapter(mEntriesSpentSummary);
			}
		}

	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//
		updateListViewAdapter();

	}

	public void updateListViewAdapter() {
		//
		updateListViewAdapter(0);

	}

}
