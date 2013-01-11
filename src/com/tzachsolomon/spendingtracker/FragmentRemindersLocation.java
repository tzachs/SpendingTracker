package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tzachsolomon.spendingtracker.FragmentGeneral.ButtonAddEntrySpentListener;

public class FragmentRemindersLocation extends SherlockFragment {

	public interface AddLocationReminderListener {
		public void onAddLocationReminderClicked();
	}

	private SherlockFragmentActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		//
		mActivity = (SherlockFragmentActivity) activity;

		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//
		View view = inflater
				.inflate(R.layout.fragment_reminders_location, null);

		return view;

	}

	public void onClick(View v) {
		//

	}

}
