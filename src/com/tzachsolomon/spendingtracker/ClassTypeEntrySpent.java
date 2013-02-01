package com.tzachsolomon.spendingtracker;

public class ClassTypeEntrySpent {
	
	
	private String mRowId;
	private String mAmount;
	private String mCategory;
	private String mDate;
	
	public ClassTypeEntrySpent(String rowId, String amount, String category, String date){
		mRowId = rowId;
		mAmount = amount;
		mCategory = category;
		mDate = date;
	}
	
	public String getAmount (){
		return mAmount;
	}
	
	

	public String getRowId() {
		// 
		return mRowId;
		
	}

	public String getCategory() {
		// 
		return mCategory;
	}

	public String getDate() {
		// 
		return mDate.split("T")[0];
	}
	
	public String getTime() {
		return mDate.split("T")[1].substring(0, 6);
	}
	
	

}
