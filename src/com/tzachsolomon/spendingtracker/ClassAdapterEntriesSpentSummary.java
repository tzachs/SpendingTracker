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

public class ClassAdapterEntriesSpentSummary extends BaseAdapter {

	private ArrayList<ClassTypeEntrySpentSummary> mItem;
	private LayoutInflater mLayoutInflater;
	private SherlockFragmentActivity mActivity;

	public ClassAdapterEntriesSpentSummary(
			SherlockFragmentActivity sherlockFragmentActivity,
			ArrayList<ClassTypeEntrySpentSummary> items) {
		mItem = items;
		mActivity = sherlockFragmentActivity;
		mLayoutInflater = (LayoutInflater) sherlockFragmentActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//
		View ret = convertView;
		Currency currency = Currency.getInstance(Locale.getDefault());
		String symbol = currency.getSymbol();

		if (ret == null) {
			ret = mLayoutInflater.inflate(
					R.layout.list_item_entry_spent_summary, null);
		}

		TextView category = (TextView) ret.findViewById(R.id.textViewCategory);
		TextView numberOfEntries = (TextView) ret
				.findViewById(R.id.textViewNumberOfEntries);
		TextView average = (TextView) ret.findViewById(R.id.textViewAverage);
		TextView max = (TextView) ret.findViewById(R.id.textViewMax);
		TextView min = (TextView) ret.findViewById(R.id.textViewMin);

		category.setText(mActivity.getString(R.string.category) + ": "
				+ mItem.get(position).getCategory());

		numberOfEntries.setText(mActivity.getString(R.string.number_of_entries)
				+ ": " + mItem.get(position).getNumberOfEntries());
		average.setText(mActivity.getString(R.string.average_spending) + ": "
				+ mItem.get(position).getAverage() + symbol);

		max.setText(mActivity.getString(R.string.max) + ": "
				+ mItem.get(position).getMax()  + symbol);
		min.setText(mActivity.getString(R.string.min) + ": "
				+ mItem.get(position).getMin()  + symbol);
		
		if ( position % 2 == 0 ){
			ret.setBackgroundColor(Color.WHITE);
			
			numberOfEntries.setTextColor(ColorStateList.valueOf(Color.BLUE));
			average.setTextColor(ColorStateList.valueOf(Color.BLUE));
			category.setTextColor(ColorStateList.valueOf(Color.BLUE));
			max.setTextColor(ColorStateList.valueOf(Color.BLUE));
			min.setTextColor(ColorStateList.valueOf(Color.BLUE));
		}else{
			ret.setBackgroundColor(Color.rgb(0, 0, 125));
			numberOfEntries.setTextColor(ColorStateList.valueOf(Color.WHITE));
			average.setTextColor(ColorStateList.valueOf(Color.WHITE));
			category.setTextColor(ColorStateList.valueOf(Color.WHITE));
			max.setTextColor(ColorStateList.valueOf(Color.WHITE));
			min.setTextColor(ColorStateList.valueOf(Color.WHITE));
			
		}

		return ret;
	}

	public int getCount() {
		//
		return mItem.size();
	}

	public Object getItem(int position) {
		//
		return position;
	}

	public long getItemId(int position) {
		//
		return position;
	}

}
