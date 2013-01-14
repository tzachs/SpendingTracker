package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;


import android.widget.ListView;
import android.widget.TextView;

public class CategoriesManager extends ListActivity implements
		OnItemClickListener {

	private static final String TAG = CategoriesManager.class.getSimpleName();

	private SpendingTrackerDbEngine m_SpendingTrackerDb;
	private ArrayList<String> m_CategoriesList;
	private ActivityList m_ListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		initializeList();

	}

	private void initializeList() {
		m_SpendingTrackerDb = new SpendingTrackerDbEngine(this);

		m_CategoriesList = new ArrayList<String>();

		m_CategoriesList.add(getString(R.string.addCategoryTitle));
//		m_CategoriesList.addAll(Arrays.asList(m_SpendingTrackerDb
//				.getCategories()));

		m_ListAdapter = new ActivityList(CategoriesManager.this,
				android.R.id.text1, m_CategoriesList);

		setListAdapter(m_ListAdapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this);
	}

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		//
//		final String category = (String) m_CategoriesList.get(position);
//
//		if (position == 0) {
//			showAddNewCategoryDialog();
//			
//		} else {
//			showDeleteCategoryDialog(category);
//		}
//		Log.i(TAG, category);
//
//	}

//	private void showDeleteCategoryDialog(final String category) {
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//		alert.setTitle(getString(R.string.deleteCategoryTitle));
//		alert.setMessage(category);
//
//		alert.setPositiveButton("Delete",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//
//						m_SpendingTrackerDb.deleteCategory(category);
//						initializeList();
//					}
//				});
//
//		alert.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//
//
//					}
//				});
//
//		alert.show();
//	}

	private void showAddNewCategoryDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.addCategoryTitle));
		alert.setMessage(getString(R.string.addCategoryMessage));

		final EditText input = new EditText(this);
		alert.setView(input);

//		alert.setPositiveButton("Add",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//
//						String value = input.getText().toString();
//						m_SpendingTrackerDb.insertNewCategory(value);
//						
//						initializeList();
//					}
//				});
//
//		alert.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						//
//
//					}
//				});

		alert.show();
	}

	private class ActivityList extends ArrayAdapter<String> {

		public ActivityList(Context context, int textViewResourceId,
				ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(CategoriesManager.this);
//				convertView = inflater.inflate(R.layout.categories_manager,
//						parent, false);
				holder = new ViewHolder();

//				holder.text = (TextView) convertView
//						.findViewById(R.id.textViewCategoryName);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (getItem(position).contentEquals(
					getString(R.string.addCategoryTitle))) {

				holder.text.setText(getItem(position));

			} else {
				holder.text.setText(getItem(position));

			}

			return convertView;

		}

	}

	static class ViewHolder {
		TextView text;

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
