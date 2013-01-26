package com.tzachsolomon.spendingtracker;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

	private TextView textViewAccuracyLabel;
	private TextView textViewAccuracyText;
	private TextView textViewAltitudeLabel;
	private TextView textViewAltitudeText;
	private TextView textViewBearingLabel;
	private TextView textViewBearingText;
	private TextView textViewLatitudeLabel;
	private TextView textViewLatitudeText;
	private TextView textViewLongitudeLabel;
	private TextView textViewLongitudeText;
	private TextView textViewProviderLabel;
	private TextView textViewProviderText;
	private TextView textViewSpeedLabel;
	private TextView textViewSpeedText;
	private TextView textViewTimeLabel;

	private TextView textViewTimeText;

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

		initTextViews(view);
		
		((ActivityMain1) mActivity).setFragmentReminderLocationRef(getTag());

		return view;

	}

	private void initTextViews(View view) {
		//

		textViewAccuracyText = (TextView) view
				.findViewById(R.id.textViewAccuracyText);
		textViewAccuracyLabel = (TextView) view
				.findViewById(R.id.textViewAccuracyLabel);

		textViewAltitudeText = (TextView) view
				.findViewById(R.id.textViewAltitudeText);
		textViewAltitudeLabel = (TextView) view
				.findViewById(R.id.textViewAltitudeLabel);

		textViewBearingText = (TextView) view
				.findViewById(R.id.textViewBearingText);
		textViewBearingLabel = (TextView) view
				.findViewById(R.id.textViewBearingLabel);

		textViewLatitudeText = (TextView) view
				.findViewById(R.id.textViewLatitudeText);
		textViewLatitudeLabel = (TextView) view
				.findViewById(R.id.textViewLatitudeLabel);

		textViewLongitudeText = (TextView) view
				.findViewById(R.id.textViewLongitudeText);
		textViewLongitudeLabel = (TextView) view
				.findViewById(R.id.textViewLongitudeLabel);

		textViewProviderText = (TextView) view
				.findViewById(R.id.textViewProviderText);
		textViewProviderLabel = (TextView) view
				.findViewById(R.id.textViewProviderLabel);

		textViewSpeedText = (TextView) view
				.findViewById(R.id.textViewSpeedText);
		textViewSpeedLabel = (TextView) view
				.findViewById(R.id.textViewSpeedLabel);

		textViewTimeText = (TextView) view.findViewById(R.id.textViewTimeText);
		textViewTimeLabel = (TextView) view
				.findViewById(R.id.textViewTimeLabel);

	}

	public void onClick(View v) {
		//

	}

	public void updateLocationTextViews(Location location) {
		//
		if (location != null) {

			textViewAccuracyText
					.setText(String.valueOf(location.getAccuracy()));
			textViewAltitudeText
					.setText(String.valueOf(location.getAltitude()));
			textViewBearingText.setText(String.valueOf(location.getBearing()));
			textViewLatitudeText
					.setText(String.valueOf(location.getLatitude()));
			textViewLongitudeText.setText(String.valueOf(location
					.getLongitude()));
			textViewProviderText
					.setText(String.valueOf(location.getProvider()));
			textViewSpeedText.setText(String.valueOf(location.getSpeed()));
			textViewTimeText.setText(String.valueOf(String.valueOf(location
					.getTime())));

		}

	}

}
