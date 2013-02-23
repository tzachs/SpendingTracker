package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassAdapterReminderTime extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<ClassTypeReminderTime> mItems;

	public ClassAdapterReminderTime(SherlockFragmentActivity activity,
			ArrayList<ClassTypeReminderTime> items) {
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = items;
	}

	public int getCount() {
		//
		return mItems.size();
	}

	public Object getItem(int position) {
		//
		return position;
	}

	public long getItemId(int position) {
		//
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//
		View ret = convertView;
		Currency currency = Currency.getInstance(Locale.getDefault());
		String symbol = currency.getSymbol();

		if (ret == null) {
			ret = mInflater.inflate(R.layout.list_item_reminder_time, null);
		}
		
		
		
		

		TextView amount = (TextView) ret.findViewById(R.id.textViewAmount);
		TextView rowNumber = (TextView) ret
				.findViewById(R.id.textViewRowNumber);
		TextView category = (TextView) ret.findViewById(R.id.textViewCategory);
		TextView hour = (TextView) ret.findViewById(R.id.textViewHour);
		TextView minute = (TextView) ret.findViewById(R.id.textViewMinute);
		TextView day = (TextView) ret.findViewById(R.id.textViewDay);
		TextView type = (TextView) ret.findViewById(R.id.textViewType);
		
		amount.setText(mItems.get(position).getmAmount() + symbol);
		rowNumber.setText(mItems.get(position).getmRowId() + ") ");
		category.setText(mItems.get(position).getmCategory());
		hour.setText(mItems.get(position).getmHour());
		minute.setText(mItems.get(position).getmMinute());
		day.setText(mItems.get(position).getmDayNormilized());
		type.setText(mItems.get(position).getmType());
		
		int color = Color.rgb(14, 127, 200);
		
		if ( position % 2 == 0 ){
			ret.setBackgroundColor(Color.WHITE);
			
			amount.setTextColor(ColorStateList.valueOf(color));
			rowNumber.setTextColor(ColorStateList.valueOf(color));
			category.setTextColor(ColorStateList.valueOf(color));
			hour.setTextColor(ColorStateList.valueOf(color));
			minute.setTextColor(ColorStateList.valueOf(color));
			day.setTextColor(ColorStateList.valueOf(color));
			type.setTextColor(ColorStateList.valueOf(color));
		}else{
			ret.setBackgroundColor(color);
			
			amount.setTextColor(ColorStateList.valueOf(Color.WHITE));
			rowNumber.setTextColor(ColorStateList.valueOf(Color.WHITE));
			category.setTextColor(ColorStateList.valueOf(Color.WHITE));
			hour.setTextColor(ColorStateList.valueOf(Color.WHITE));
			minute.setTextColor(ColorStateList.valueOf(Color.WHITE));
			day.setTextColor(ColorStateList.valueOf(Color.WHITE));
			type.setTextColor(ColorStateList.valueOf(Color.WHITE));
			
		}

		

		return ret;
	}

}
