package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentCategoriesManager extends SherlockDialogFragment {
	
	private ListView listViewCategories;
	private SherlockFragmentActivity mActivity;
	private Object m_Categories;
	private SpendingTrackerDbEngine mSpendingTrackerDbEngine;
	
	@Override
	public void onAttach(Activity activity) {
		// 
		super.onAttach(activity);
		mActivity = (SherlockFragmentActivity)activity;
		
		mSpendingTrackerDbEngine = new SpendingTrackerDbEngine(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 
		View view = inflater.inflate(R.layout.fragment_categories_manager, null);
		
		listViewCategories = (ListView)view.findViewById(R.id.listViewCategories);
		
		return view;
	}
	
	private void initSpinnerCategories() {
		//
		try {

			m_Categories = mSpendingTrackerDbEngine.getCategories();
			
			listViewCategories.setAdapter(adapter)
			

			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					mActivity, android.R.layout.simple_spinner_item,
					m_Categories);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

		} catch (Exception e) {
			// if (m_DebugMode) {
			// Toast.makeText(this, e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
			// }

			// e.printStackTrace();
			// Log.d(TAG, e.toString());
		}

	}

}
