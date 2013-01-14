package com.tzachsolomon.spendingtracker;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentEntries extends SherlockFragment implements
		OnCheckedChangeListener {

	private static final String TAG = FragmentEntries.class.getSimpleName();

	private ListView listViewEntriesSpent;
	private ClassEntriesSpentAdapter mEntriesSpent;
	private SpendingTrackerDbEngine mDbEngine;
	private Calendar mCalendar;
	private RadioGroup radioGroupEntries;
	private SherlockFragmentActivity mActivity;
	private SpentEntryListener mSpentEntryListener;

	public interface SpentEntryListener {
		public void onSpentEntryDeleted(String rowId);

		public void onSpentEntryEdited(String rowId);
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

		listViewEntriesSpent = (ListView) view
				.findViewById(R.id.listViewEntriesSpent);
		radioGroupEntries = (RadioGroup) view
				.findViewById(R.id.radioGroupEntries);
		radioGroupEntries.setOnCheckedChangeListener(this);

		((ActivityMain1) mActivity).setFragmentEntriesRef(getTag());

		return view;
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {

		}

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
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 1, Menu.NONE, "Edit");
		menu.add(Menu.NONE, 2, Menu.NONE, "Delete");
		menu.add(Menu.NONE, 3, Menu.NONE, "Share");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
			if (mSpentEntryListener != null) {
				mSpentEntryListener.onSpentEntryEdited(mEntriesSpent
						.getRowIdAtIndex(info.position));
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

		mDbEngine = new SpendingTrackerDbEngine(this.getSherlockActivity());
	}

	@Override
	public void onResume() {
		//
		super.onResume();
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());

		updateListViewAdapter();
	}

	public void updateListViewAdapter() {
		updateListViewAdapter(radioGroupEntries.getCheckedRadioButtonId());
	}

	private void updateListViewAdapter(int checkedId) {
		//
		switch (checkedId) {
		case R.id.radioButtonEntiresMonthly:
			mEntriesSpent = new ClassEntriesSpentAdapter(
					this.getSherlockActivity(),
					mDbEngine.getSpentThisMonthEnteries(mCalendar));
			break;
		case R.id.radioButtonEntriesEveryday:
			mEntriesSpent = new ClassEntriesSpentAdapter(
					this.getSherlockActivity(),
					mDbEngine.getSpentDailyEntries(mCalendar));
			break;
		case R.id.radioButtonEntriesWeekly:
			mEntriesSpent = new ClassEntriesSpentAdapter(
					this.getSherlockActivity(),
					mDbEngine.getSpentThisWeekEnteries(1, mCalendar));
			break;

		}

		if (listViewEntriesSpent != null) {
			listViewEntriesSpent.setAdapter(mEntriesSpent);
		} 


	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		//
		updateListViewAdapter();

	}

}
