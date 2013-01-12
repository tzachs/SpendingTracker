package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.tzachsolomon.spendingtracker.FragmentEntries.SpentEntryListener;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonAddEntrySpentListener;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonCategoriesEditListener;
import com.tzachsolomon.spendingtracker.FragmentRemindersTime.AddTimeReminderListener;

public class ActivityMain1 extends SherlockFragmentActivity implements
		ButtonAddEntrySpentListener, ButtonCategoriesEditListener,
		AddTimeReminderListener, SpentEntryListener {

	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private SpendingTrackerDbEngine mSpendingTrackerDbEngine;
	private SharedPreferences mSharedPreferences;
	private FragmentGeneral mFragemtGeneral;
	private FragmentEntries mFragmentEntries;

	// TODO: delete entry with dialog
	// TODO: update spent entry
	// TODO: admin reminders
	// TODO: default TAB
	// TODO: first time setup
	// TODO: import, export db async
	// TODO: share spending
	// TODO: time reminder auto sync to date / time

	@Override
	protected void onCreate(Bundle arg0) {
		//
		super.onCreate(arg0);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);

		setContentView(mViewPager);

		initializeActionBar();

		initializeVariables();
	}

	private void initializeVariables() {
		//
		mSpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

	}

	private void initializeActionBar() {
		//

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mTabsAdapter = new TabsAdapter(this, mViewPager);

		mTabsAdapter.addTab(actionBar.newTab().setText("General"),
				FragmentGeneral.class, null);
		mTabsAdapter.addTab(actionBar.newTab().setText("Entries"),
				FragmentEntries.class, null);
		mTabsAdapter.addTab(actionBar.newTab().setText("Time Reminder"),
				FragmentRemindersTime.class, null);
		mTabsAdapter.addTab(actionBar.newTab().setText("Location Reminder"),
				FragmentRemindersLocation.class, null);
		mTabsAdapter.addTab(actionBar.newTab().setText("Admin"),
				FragmentAdminDb.class, null);

	}

	public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);

		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}

	public void onButtonCategoriesEditClicked() {
		//
		Toast.makeText(ActivityMain1.this, "onButtonCategoriesEditClicked",
				Toast.LENGTH_LONG).show();

	}

	public void onAddTimeReminderClicked() {
		// TODO Auto-generated method stub

	}

	public void onButtonAddEntrySpentClicked(Bundle values) {
		//
		String amount = values.getString("amount");
		String category = values.getString("category");
		String comments = values.getString("comments");
		boolean validEntry = true;

		if (amount.isEmpty()) {
			Toast.makeText(ActivityMain1.this, "Please fill in the amount",
					Toast.LENGTH_LONG).show();
			validEntry = false;
		}

		if (category.isEmpty()) {
			Toast.makeText(ActivityMain1.this, "Please fill in the category",
					Toast.LENGTH_LONG).show();
			validEntry = false;
		}

		// if ( comments.isEmpty() ){
		// Toast.makeText(ActivityMain1.this, "Please fill in the comments",
		// Toast.LENGTH_LONG).show();
		// validEntry = false;
		// }

		if (validEntry) {
			mSpendingTrackerDbEngine.insertNewSpending(amount, category,
					comments, null);
		}

		// showing message to user that entry was added
		if (mSharedPreferences.getBoolean("cbShowEntryAdded", true)) {
			Toast.makeText(ActivityMain1.this,
					getString(R.string.toastMessageEntryAdded),
					Toast.LENGTH_SHORT).show();
		}

		if (mFragemtGeneral != null) {
			mFragemtGeneral.updateSpentDayWeekMonth();
			
		}
		
		if (mFragmentEntries != null){
			mFragmentEntries.updateListViewAdapter();
		}

	}

	public void setFragmentGeneralRef(String tag) {
		//
		mFragemtGeneral = (FragmentGeneral) getSupportFragmentManager()
				.findFragmentByTag(tag);

	}

	public void setFragmentEntriesRef(String tag) {
		// 
		mFragmentEntries = (FragmentEntries) getSupportFragmentManager()
				.findFragmentByTag(tag);
		
	}

	public void onSpentEntryDeleted(String rowId) {
		// 
		mSpendingTrackerDbEngine.deleteSpentEntryByRowId(rowId);
		mFragmentEntries.updateListViewAdapter();
		mFragemtGeneral.updateSpentDayWeekMonth();
		
		
	}

	public void onSpentEntryEdited(String rowId) {
		// TODO Auto-generated method stub
		
	}

}
