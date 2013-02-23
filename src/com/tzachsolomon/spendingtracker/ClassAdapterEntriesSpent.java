package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassAdapterEntriesSpent extends BaseAdapter {

	private ArrayList<ClassTypeEntrySpent> mItem;
	private LayoutInflater mLayoutInflater;
	private SherlockFragmentActivity mActivity;

	public ClassAdapterEntriesSpent(
			SherlockFragmentActivity sherlockFragmentActivity,
			ArrayList<ClassTypeEntrySpent> items) {
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
			ret = mLayoutInflater.inflate(R.layout.list_item_entry_spent, null);
		}

		TextView amount = (TextView) ret.findViewById(R.id.textViewAmount);
		TextView rowId = (TextView) ret.findViewById(R.id.textViewRowNumber);
		TextView category = (TextView) ret.findViewById(R.id.textViewCategory);
		TextView date = (TextView) ret.findViewById(R.id.textViewDate);
		TextView time = (TextView) ret.findViewById(R.id.textViewTime);
		amount.setText(mActivity.getString(R.string.amount) + ": "
				+ mItem.get(position).getAmount() + symbol);
		rowId.setText(mItem.get(position).getRowId()  + ") ");
		category.setText(mActivity.getString(R.string.category) + ": "
				+ mItem.get(position).getCategory());
		date.setText(mActivity.getString(R.string.date) + ": "
				+ mItem.get(position).getDate());
		time.setText(mActivity.getString(R.string.time) + ": "
				+ mItem.get(position).getTime());
		
		if ( position % 2 == 0 ){
			ret.setBackgroundColor(Color.WHITE);
			
			amount.setTextColor(ColorStateList.valueOf(Color.BLUE));
			rowId.setTextColor(ColorStateList.valueOf(Color.BLUE));
			category.setTextColor(ColorStateList.valueOf(Color.BLUE));
			date.setTextColor(ColorStateList.valueOf(Color.BLUE));
			time.setTextColor(ColorStateList.valueOf(Color.BLUE));
		}else{
			ret.setBackgroundColor(Color.rgb(0, 0, 125));
			amount.setTextColor(ColorStateList.valueOf(Color.WHITE));
			rowId.setTextColor(ColorStateList.valueOf(Color.WHITE));
			category.setTextColor(ColorStateList.valueOf(Color.WHITE));
			date.setTextColor(ColorStateList.valueOf(Color.WHITE));
			time.setTextColor(ColorStateList.valueOf(Color.WHITE));
			
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

	public String getRowIdAtIndex(int position) {
		//

		return mItem.get(position).getRowId();
	}

	public Bundle getItemAsBundle(int position) {
		//
		Bundle ret = new Bundle();
		ret.putString("id", mItem.get(position).getRowId());
		ret.putString("amount", mItem.get(position).getAmount());
		ret.putString("date", mItem.get(position).getDate());
		ret.putString("time", mItem.get(position).getTime());
		ret.putString("category", mItem.get(position).getCategory());

		return ret;
	}

}
