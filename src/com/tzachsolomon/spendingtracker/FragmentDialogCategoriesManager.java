package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class FragmentDialogCategoriesManager extends SherlockDialogFragment implements
		OnClickListener, OnMenuItemClickListener {

	private ListView listViewCategories;
	private SherlockFragmentActivity mActivity;
	private ArrayList<ClassTypeCategory> m_Categories;
	private ClassDbEngine mSpendingTrackerDbEngine;
	private Button buttonAddCategory;
	private CategoriesManagerListener mCategoriesManagerListener;

	public interface CategoriesManagerListener {
		public void onDeleteCategoryClicked(String categoryName);
	}

	@Override
	public void onAttach(Activity activity) {
		//
		super.onAttach(activity);
		mActivity = (SherlockFragmentActivity) activity;

		mSpendingTrackerDbEngine = new ClassDbEngine(activity);

		try {
			mCategoriesManagerListener = (CategoriesManagerListener) mActivity;
		} catch (ClassCastException e) {
			new Throwable(mActivity.toString()
					+ " must implement CategoriesManagerListener");
		}

	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		//
		super.onActivityCreated(arg0);
		registerForContextMenu(listViewCategories);
		listViewCategories.setOnCreateContextMenuListener(this);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 1, Menu.NONE, "Delete");

		menu.getItem(0).setOnMenuItemClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater
				.inflate(R.layout.fragment_categories_manager, null);

		listViewCategories = (ListView) view
				.findViewById(R.id.listViewCategories);

		initCategories();

		buttonAddCategory = (Button) view.findViewById(R.id.buttonAddCategory);
		buttonAddCategory.setOnClickListener(this);

		getDialog().setTitle("Categories Manager");

		((ActivityMain1) mActivity).setFragmentCategoriesManagerRef(getTag());

		return view;
	}

	public void initCategories() {
		//
		try {

			m_Categories = mSpendingTrackerDbEngine.getCategories();

			listViewCategories.setAdapter(new ClassAdapterCategory(
					getSherlockActivity(), m_Categories));

		} catch (Exception e) {
			// if (m_DebugMode) {
			// Toast.makeText(this, e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
			// }

			// e.printStackTrace();
			// Log.d(TAG, e.toString());
		}

	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonAddCategory:
			buttonAddCategory_Clicked();

			break;

		default:
			break;
		}

	}

	private void buttonAddCategory_Clicked() {
		//
		FragmentDialogCategoryAdd fragmentCategoryAddDialog = new FragmentDialogCategoryAdd();
		fragmentCategoryAddDialog.show(getSherlockActivity()
				.getSupportFragmentManager(), "FragmentCategoryAddDialog");

	}

	public boolean onMenuItemClick(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (mCategoriesManagerListener != null) {
			mCategoriesManagerListener.onDeleteCategoryClicked(m_Categories
					.get(info.position).getCategoryName());
		}

		return true;
	}

}
