package com.tzachsolomon.spendingtracker;

public class ClassTypeEntrySpentSummary {
	
	
	private String mAverage;
	private String mMax;
	private String mCategory;
	private String mMin;
	private String mNumberOfEntries;
	
	public ClassTypeEntrySpentSummary(String average, String max, String category, String min, String numberOfEntries){
		mAverage = average;
		mMax = max;
		mCategory = category;
		mMin = min;
		mNumberOfEntries = numberOfEntries;
	}
	
	
	

	public String getCategory() {
		// 
		return mCategory;
	}




	public String getNumberOfEntries() {
		// 
		return mNumberOfEntries;
	}




	public String getMax() {
		// 
		return mMax;
	}
	
	public String getMin() {
		// 
		return mMin;
	}




	public String getAverage() {
		// 
		return mAverage;
	}

	
	

}
