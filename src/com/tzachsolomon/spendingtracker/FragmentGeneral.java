package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentGeneral extends SherlockFragment implements OnClickListener, OnItemSelectedListener {
	
	
	
	public interface ButtonAddEntrySpentListener {
		public void onButtonAddEntrySpentClicked(Bundle values);
	}
	
	public interface ButtonCategoriesEditListener {
		public void onButtonCategoriesEditClicked();
	}
	
	private ButtonAddEntrySpentListener mButtonAddEntrySpentListener;
	private ButtonCategoriesEditListener mButtonCategoriesEditListener;
	private Button buttonAddEntrySpent;
	private Button buttonCategoriesEdit;
	private EditText editTextEntrySpent;
	private EditText editTextComment;
	private Spinner spinnerCategories;
	private String m_CategorySelected;
	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private SherlockFragmentActivity mActivity;
	private String[] m_Categories;
	
	

	
	@Override
	public void onAttach(Activity activity) {
		// 
		super.onAttach(activity);
		
		mActivity = (SherlockFragmentActivity)activity;
		
		try {
			mButtonAddEntrySpentListener = (ButtonAddEntrySpentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ButtonAddEntrySpentListener ");
		}
		
		
		try {
			mButtonCategoriesEditListener = (ButtonCategoriesEditListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ButtonCategoriesEditListener ");
		}
		
		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_general, null);
		
		buttonAddEntrySpent = (Button)view.findViewById(R.id.buttonAddEntrySpent); 
		buttonCategoriesEdit = (Button)view.findViewById(R.id.buttonCategoriesEdit);
		
		buttonAddEntrySpent.setOnClickListener(this);
		buttonCategoriesEdit.setOnClickListener(this);
		
		editTextEntrySpent = (EditText)view.findViewById(R.id.editTextEntrySpent);
		editTextComment = (EditText)view.findViewById(R.id.editTextComment);
		
		spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);
		
		spinnerCategories.setOnItemSelectedListener(this);
		
		initSpinnerCategories();
	
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 
		super.onActivityCreated(savedInstanceState);
		
		
	}


	public void onClick(View v) {
		// 
		switch (v.getId()){
		case R.id.buttonAddEntrySpent:
			buttonAddEntrySpent_Clicked();
			break;
			
		case R.id.buttonCategoriesEdit:
			buttonCategoriesEdit_Clicked();
			break;
			
		}
		
	}
	
	private void initSpinnerCategories() {
		//
		try {

			m_Categories = m_SpendingTrackerDbEngine.getCategories();
			
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					mActivity, android.R.layout.simple_spinner_item, m_Categories);

			spinnerCategories.setAdapter(spinnerArrayAdapter);

		} catch (Exception e) {
//			if (m_DebugMode) {
//				Toast.makeText(this, e.getMessage().toString(),
//						Toast.LENGTH_SHORT).show();
//			}

//			e.printStackTrace();
//			Log.d(TAG, e.toString());
		}

	}
	

	private void buttonCategoriesEdit_Clicked() {
		// 
		if (mButtonCategoriesEditListener != null){
			mButtonCategoriesEditListener.onButtonCategoriesEditClicked();
		}
		
	}


	private void buttonAddEntrySpent_Clicked() {
		// 
		if (mButtonAddEntrySpentListener != null){
			Bundle values = new Bundle();
			values.putString("comments", editTextComment.getText().toString());
			values.putString("amount", editTextEntrySpent.getText().toString());
			values.putString("category", m_CategorySelected);
			mButtonAddEntrySpentListener.onButtonAddEntrySpentClicked(values);
		}
		
	}


	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// 
		m_CategorySelected = parent.getItemAtPosition(pos)
				.toString();
		
	}


	public void onNothingSelected(AdapterView<?> arg0) {
		// 
		m_CategorySelected = "";
	}

}
