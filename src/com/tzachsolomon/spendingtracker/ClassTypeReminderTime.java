package com.tzachsolomon.spendingtracker;

import static com.tzachsolomon.spendingtracker.ClassCommonUtilities.*;

public class ClassTypeReminderTime {

	private String mRowId;
	private String mType;
	private String mHour;
	private String mMinute;
	private String mDay;
	private String mCategory;
	private String mAmount;

	public ClassTypeReminderTime(String mRowId, String mType, String mHour,
			String mMinute, String mDay, String mCategory, String mAmount) {

		this.mRowId = mRowId;
		this.mType = mType;
		this.mHour = mHour;
		this.mMinute = mMinute;
		this.mDay = mDay;
		this.mCategory = mCategory;
		this.mAmount = mAmount;
	}

	public String getmRowId() {
		return mRowId;

	}

	public void setmRowId(String mRowId) {
		this.mRowId = mRowId;
	}

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public String getmHour() {
		return mHour;
	}

	public void setmHour(String mHour) {
		this.mHour = mHour;
	}

	public String getmMinute() {
		return mMinute;
	}

	public void setmMinute(String mMinute) {
		this.mMinute = mMinute;
	}

	public String getmDay() {
		return mDay;
		
	}

	public String getmDayNormilized() {
		String ret = "";
		if (mType.contentEquals(TYPE_REMINDER_TIME_EVERYDAY)) {
			ret = "Every day";
		} else if (mType.contentEquals(TYPE_REMINDER_TIME_WEEKLY)) {
			if (mDay.contentEquals("1")) {
				ret = "Sunday";
			} else if (mDay.contentEquals("2")) {
				ret = "Monday";
			} else if (mDay.contentEquals("3")) {
				ret = "Tuesday";
			} else if (mDay.contentEquals("4")) {
				ret = "Wendsday";
			} else if (mDay.contentEquals("5")) {
				ret = "Thursday";
			} else if (mDay.contentEquals("6")) {
				ret = "Friday";
			} else if (mDay.contentEquals("7")) {
				ret = "Saturday";
			}

		} else if (mType.contentEquals(TYPE_REMINDER_TIME_MONTHLY)) {
			ret = mDay;
		}
		return ret;

	}

	public void setmDay(String mDay) {
		this.mDay = mDay;
		
	}

	public String getmCategory() {
		return mCategory;
	}

	public void setmCategory(String mCategory) {
		this.mCategory = mCategory;
	}

	public String getmAmount() {
		return mAmount;
	}

	public void setmAmount(String mAmount) {
		this.mAmount = mAmount;
	}

	public String toToastMessage() {
		//
		return "Day: " + mDay + " Hour: " + mHour + " Minute: " + mMinute;
	}

}
