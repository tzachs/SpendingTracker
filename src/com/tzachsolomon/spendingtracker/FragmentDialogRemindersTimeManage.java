package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tzachsolomon.spendingtracker.FragmentDialogCategoriesManager.CategoriesManagerListener;

public class FragmentDialogRemindersTimeManage extends SherlockDialogFragment
		implements OnMenuItemClickListener {

	private SherlockFragmentActivity mActivity;
	private ListView listViewTimeReminders;
	private ClassDbEngine mSpendingTrackerDbEngine;
	private ArrayList<ClassTypeReminderTime> mReminderTimeEntries;
	private ReminderTimeListener mReminderTimeListener;

	public interface ReminderTimeListener {
		public void onDeleteReminderTimeClicked(String rowId);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		//
		super.onActivityCreated(arg0);
		registerForContextMenu(listViewTimeReminders);
		listViewTimeReminders.setOnCreateContextMenuListener(this);

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME,com.actionbarsherlock.R.style.Theme_Sherlock_Light_Dialog);
				
	}

	@Override
	public void onAttach(Activity activity) {
		//
		mActivity = (SherlockFragmentActivity) activity;

		mSpendingTrackerDbEngine = new ClassDbEngine(mActivity);

		try {
			mReminderTimeListener = (ReminderTimeListener) mActivity;
		} catch (ClassCastException e) {
			new Throwable(mActivity.toString()
					+ " must implement ReminderTimeListener");
		}

		super.onAttach(activity);
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(
				R.layout.fragment_dialog_reminders_time_manage, container);

		initilaizeValues(view);
		
		getDialog().setTitle("Time Reminders Manager");
		
		

		((ActivityMain1) mActivity).setFragmentReminderTimeManagerRef(getTag());


		return view;
	}

	private void initilaizeValues(View view) {
		//
		listViewTimeReminders = (ListView) view
				.findViewById(R.id.listViewTimeReminders);

		initRemindersTimeListView();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 1, Menu.NONE, "Delete");

		menu.getItem(0).setOnMenuItemClickListener(this);

	}

	public void initRemindersTimeListView() {
		//
		try {

			mReminderTimeEntries = mSpendingTrackerDbEngine.getRemindersTime();

			listViewTimeReminders.setAdapter(new ClassAdapterReminderTime(
					getSherlockActivity(), mReminderTimeEntries));

		} catch (Exception e) {
			// if (m_DebugMode) {
			// Toast.makeText(this, e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
			// }

			// e.printStackTrace();
			// Log.d(TAG, e.toString());
		}

	}

	public boolean onMenuItemClick(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (mReminderTimeListener != null) {
			mReminderTimeListener
					.onDeleteReminderTimeClicked(mReminderTimeEntries.get(
							info.position).getmRowId());
		}

		return true;
	}

}
