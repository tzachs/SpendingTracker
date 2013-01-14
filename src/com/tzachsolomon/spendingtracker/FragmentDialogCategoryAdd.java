package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class FragmentDialogCategoryAdd extends SherlockDialogFragment implements
		OnClickListener {

	private EditText editTextAddCategory;
	private Button buttonAddCategorySave;
	private AddCategoryListener mAddCategoryListener;

	public interface AddCategoryListener {
		public void onAddCategorySaveClicked(String categoryName);
		
	}

	@Override
	public void onAttach(Activity activity) {
		//

		super.onAttach(activity);

		try {
			mAddCategoryListener = (AddCategoryListener) activity;
		} catch (ClassCastException e) {
			new Throwable(activity.toString()
					+ " must implement AddCategoryListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater.inflate(R.layout.fragment_category_add_dialog,
				container);

		editTextAddCategory = (EditText) view
				.findViewById(R.id.editTextAddCategory);
		buttonAddCategorySave = (Button) view
				.findViewById(R.id.buttonAddCategorySave);

		buttonAddCategorySave.setOnClickListener(this);

		getDialog().setTitle("Add New Category");

		return view;
	}

	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.buttonAddCategorySave:
			buttonAddCategorySave_Clicked();

			break;

		default:
			break;
		}

	}

	private void buttonAddCategorySave_Clicked() {
		//
		if (mAddCategoryListener != null) {
			mAddCategoryListener.onAddCategorySaveClicked(editTextAddCategory
					.getText().toString());
		}

	}

}
