package com.tzachsolomon.spendingtracker;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassAdapterCategory extends BaseAdapter {

	private ArrayList<ClassTypeCategory> mItems;
	private LayoutInflater mLayoutInflater;

	public ClassAdapterCategory(SherlockFragmentActivity activity,
			ArrayList<ClassTypeCategory> items) {
		mItems = items;
		mLayoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

		if (ret == null) {
			ret = mLayoutInflater.inflate(R.layout.list_item_category, null);
		}

		TextView textViewListItemCategory = (TextView) ret
				.findViewById(R.id.textViewListItemCategory);

		textViewListItemCategory
				.setText(mItems.get(position).getCategoryName());
		
		if ( position % 2 == 0 ){
			ret.setBackgroundColor(Color.WHITE);
			
			textViewListItemCategory.setTextColor(ColorStateList.valueOf(Color.BLUE));
			
		}else{
			ret.setBackgroundColor(Color.rgb(0, 0, 125));
			textViewListItemCategory.setTextColor(ColorStateList.valueOf(Color.WHITE));
			
			
		}

		return ret;
	}
}
