package com.tzachsolomon.spendingtracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;

public class SpendingTrackerDbEngine {

	private static final String TAG = "SpendingTrackerDbEngine";

	public static final String TYPE_REMINDER_EVERYDAY = "Everyday";
	public static final String TYPE_REMINDER_WEEKLY = "Weekly";
	public static final String TYPE_REMINDER_MONTHLY = "Monthly";

	public static final String TYPE_REMINDER_SUNDAY = "1";
	public static final String TYPE_REMINDER_MONDAY = "2";
	public static final String TYPE_REMINDER_TUESDAY = "3";
	public static final String TYPE_REMINDER_WEDNESDAY = "4";
	public static final String TYPE_REMINDER_THURSDAY = "5";
	public static final String TYPE_REMINDER_FRIDAY = "6";
	public static final String TYPE_REMINDER_SATURDAY = "7";
	public static final String TYPE_REMINDER_DAY_DONT_CARE = "-1";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_AMOUNT = "colAmount";
	public static final String KEY_CATEGORY = "colCategory";
	public static final String KEY_DATE = "colDate";
	public static final String KEY_COMMENT = "colComment";

	public static final String KEY_TYPE = "colType";
	public static final String KEY_HOUR = "colHour";
	public static final String KEY_MINUTE = "colMinute";
	public static final String KEY_DAY = "colDay";

	private static final String DATABASE_NAME = "SpendingTrackerDb";
	private static final String TABLE_SPENDING = "tblSpending";
	private static final String TABLE_CATEGORIES = "tblCategories";
	private static final String TABLE_REMINDERS = "tblReminders";

	private static final int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_SPENDING + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_AMOUNT
					+ " TEXT NOT NULL, " + KEY_DATE + " TEXT NOT NULL,"
					+ KEY_CATEGORY + " TEXT NOT NULL, " + KEY_COMMENT
					+ " TEXT " + ");");

			db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CATEGORY
					+ " TEXT NOT NULL, " + KEY_COMMENT + " TEXT " + ");");

			db.execSQL("CREATE TABLE " + TABLE_REMINDERS + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TYPE
					+ " TEXT NOT NULL, " + KEY_HOUR + " TEXT NOT NULL, "
					+ KEY_MINUTE + " TEXT NOT NULL, " + KEY_DAY
					+ " TEXT NOT NULL, " + KEY_AMOUNT + " TEXT NOT NULL, "
					+ KEY_CATEGORY + " TEXT NOT NULL "

					+ ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.v(TAG, DATABASE_NAME + "is upgraded from version " + oldVersion
					+ " to version " + newVersion);

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPENDING);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

			onCreate(db);
		}

	}

	public class XmlImportDataHandler extends DefaultHandler {

		boolean inTableCategories = false;
		boolean inTableReminders = false;
		boolean inTableSpending = false;

		private ArrayList<String[]> m_Categories;
		private ArrayList<String[]> m_Reminders;
		private ArrayList<String[]> m_Spending;

		public XmlImportDataHandler() {
			//
			m_Categories = new ArrayList<String[]>();
			m_Reminders = new ArrayList<String[]>();
			m_Spending = new ArrayList<String[]>();
		}

		@Override
		public void startDocument() throws SAXException {
			//
			super.startDocument();
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			//
			try {
				// Log.v(TAG, "startElement " + localName);
				if (localName.contentEquals("TABLE")) {
					String tableName = attributes.getValue("NAME");

					inTableCategories = false;
					inTableReminders = false;
					inTableSpending = false;

					if (tableName
							.contentEquals(SpendingTrackerDbEngine.TABLE_CATEGORIES)) {
						inTableCategories = true;
						Log.v(TAG, "in table " + TABLE_CATEGORIES);

					} else if (tableName
							.contentEquals(SpendingTrackerDbEngine.TABLE_REMINDERS)) {
						inTableReminders = true;
						Log.v(TAG, "in table " + TABLE_REMINDERS);
					} else if (tableName
							.contentEquals(SpendingTrackerDbEngine.TABLE_SPENDING)) {
						inTableSpending = true;
						Log.v(TAG, "in table " + TABLE_SPENDING);
					}

				} else if (localName.contentEquals("ENTRY")) {

					int i, count;
					String[] values;

					count = attributes.getLength();

					values = new String[count];

					for (i = 0; i < count; i++) {
						values[i] = attributes.getValue(i);
						Log.v(TAG, "Attribute is " + attributes.getQName(i)
								+ " Value is " + values[i]);

					}

					if (inTableCategories) {

						m_Categories.add(values);

					} else if (inTableReminders) {

						m_Reminders.add(values);

					} else if (inTableSpending) {
						m_Spending.add(values);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());

			}

			// super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
		}

		public ArrayList<String[]> getCategories() {
			//
			return m_Categories;

		}

		public ArrayList<String[]> getReminders() {
			//
			return m_Reminders;
		}

		public ArrayList<String[]> getSpending() {
			//
			return m_Spending;
		}

	}

	public SpendingTrackerDbEngine(Context i_Context) {
		ourContext = i_Context;
	}

	public SpendingTrackerDbEngine open() throws SQLException {

		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();

		return this;
	}

	public void initDefaultCategories() {
		// Function create the default categories
		String[] categories = new String[] { "Transport To Work", "Lunch",
				"Tip At Lunch" };
		ContentValues cv = new ContentValues();

		this.open();

		for (String category : categories) {
			cv.put(KEY_CATEGORY, category);
			ourDatabase.insert(TABLE_CATEGORIES, null, cv);

		}

		this.close();

	}

	public void close() {
		ourHelper.close();
	}

	public long insertNewSpending(String i_Amount, String i_Category,
			String i_Comments, String i_Date) {

		ContentValues cv = new ContentValues();
		Time currentTime = new Time();
		long ret;

		currentTime.setToNow();

		cv.put(KEY_AMOUNT, i_Amount);
		// checking if to insert entry with date given
		// or with the current date
		if (i_Date != null) {
			cv.put(KEY_DATE, i_Date);
		} else {
			cv.put(KEY_DATE, currentTime.toString());
		}
		cv.put(KEY_CATEGORY, i_Category);
		cv.put(KEY_COMMENT, i_Comments);
		this.open();

		ret = ourDatabase.insert(TABLE_SPENDING, null, cv);
		this.close();

		return ret;

	}

	public long insertNewCategory(String i_Category) {
		ContentValues cv = new ContentValues();
		Time currentTime = new Time();
		long ret;

		currentTime.setToNow();

		cv.put(KEY_CATEGORY, i_Category);

		this.open();

		ret = ourDatabase.insert(TABLE_CATEGORIES, null, cv);
		this.close();

		return ret;
	}

	public long insertNewReminder(String i_Type, String i_Hour,
			String i_Minute, String i_Day, String i_Amount, String i_Category) {

		ContentValues cv = new ContentValues();
		Time currentTime = new Time();
		long ret;

		currentTime.setToNow();

		cv.put(KEY_TYPE, i_Type);
		cv.put(KEY_HOUR, i_Hour);
		cv.put(KEY_MINUTE, i_Minute);
		cv.put(KEY_AMOUNT, i_Amount);
		cv.put(KEY_DAY, i_Day);
		cv.put(KEY_CATEGORY, i_Category);

		this.open();

		ret = ourDatabase.insert(TABLE_REMINDERS, null, cv);
		this.close();

		return ret;

	}

	public String getSpentToday() {
		Calendar now = Calendar.getInstance();
		int todayInMonth;
		String todayInMonthString;
		Cursor c;
		int iColDate, iColAmount;

		float ret = 0;

		now.setTimeInMillis(System.currentTimeMillis());

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE, KEY_COMMENT };
		// making sure digit 1 - 9 are 01 - 09
		todayInMonthString = (todayInMonth < 10 ? "0" : "") + todayInMonth;
		Log.v(TAG, "checking for day " + todayInMonthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_DATE
				+ " LIKE '%_____" + todayInMonthString + "T%'", null, null,
				null, null);

		iColDate = c.getColumnIndex(KEY_DATE);
		iColAmount = c.getColumnIndex(KEY_AMOUNT);

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			Log.v(TAG, "Found entry at date " + c.getString(iColDate));
			ret += c.getFloat(iColAmount);
		}

		c.close();

		this.close();

		return Float.toString(ret);
	}

	public String getSpentThisWeek(int i_FirstDayOfWeek) {
		// i_FirstDayOfWeek: 1 - Sunday, 2 - Monday

		Calendar now = Calendar.getInstance();
		int today, todayInMonth, thisMonth, thisYear;
		String todayInMonthString;
		String thisMonthString, thisYearString;

		int iColDate, iColAmount;
		StringBuilder filter = new StringBuilder();

		int i;
		Cursor c;

		float ret = 0;

		now.setTimeInMillis(System.currentTimeMillis());

		// getting this month and this year

		thisYear = now.get(Calendar.YEAR);
		thisMonth = now.get(Calendar.MONTH) + 1;

		thisMonthString = (thisMonth < 10 ? "0" : "") + thisMonth;
		thisYearString = Integer.toString(thisYear);
		Log.v(TAG, "Found Year " + thisYearString);
		Log.v(TAG, "Found Month" + thisMonthString);

		// getting today in the week
		today = now.get(Calendar.DAY_OF_WEEK);
		// getting how many days we need to go back to the start of the week
		i = today - i_FirstDayOfWeek;

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE, KEY_COMMENT };

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		while (i > 0) {

			today = todayInMonth - i;
			todayInMonthString = (today < 10 ? "0" : "") + today;
			Log.v(TAG, "Adding entry for day " + todayInMonthString);
			filter.append(KEY_DATE);
			filter.append(" LIKE '%" + thisYearString + thisMonthString
					+ todayInMonthString + "T%'");
			filter.append(" OR ");
			i--;

		}
		today = todayInMonth - i;
		todayInMonthString = (today < 10 ? "0" : "") + today;
		Log.v(TAG, "Adding entry for day " + todayInMonthString);
		filter.append(KEY_DATE);
		filter.append(" LIKE '%" + thisYearString + thisMonthString
				+ todayInMonthString + "T%'");

		Log.v(TAG, "using filter " + filter.toString());

		this.open();

		c = ourDatabase.query(TABLE_SPENDING, columns, filter.toString(), null,
				null, null, null);

		iColDate = c.getColumnIndex(KEY_DATE);
		iColAmount = c.getColumnIndex(KEY_AMOUNT);

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			Log.v(TAG, "Found entry at date " + c.getString(iColDate));
			ret += c.getFloat(iColAmount);

		}

		c.close();

		this.close();

		return Float.toString(ret);

	}

	public String getSpentThisMonth() {
		//
		Calendar now = Calendar.getInstance();
		int month;
		String monthString;

		int iColDate, iColAmount;

		Cursor c;

		float ret = 0;

		now.setTimeInMillis(System.currentTimeMillis());

		month = now.get(Calendar.MONTH) + 1;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE, KEY_COMMENT };

		// Making sure digit 1 - 9 are 01 - 09
		monthString = (month < 10 ? "0" : "") + month;
		Log.v(TAG, "checking for day " + monthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_DATE + " LIKE '%___"
				+ monthString + "__T%'", null, null, null, null);

		iColAmount = c.getColumnIndex(KEY_AMOUNT);
		iColDate = c.getColumnIndex(KEY_DATE);

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			Log.v(TAG, "Found entry from date " + c.getString(iColDate));
			ret += c.getFloat(iColAmount);
		}

		c.close();

		this.close();

		return Float.toString(ret);

	}

	public String[] getCategories() {

		String[] ret = null;
		int i = 0;

		this.open();
		Cursor cursor = ourDatabase.query(TABLE_CATEGORIES,
				new String[] { KEY_CATEGORY }, null, null, null, null, null);

		ret = new String[cursor.getCount()];

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			ret[i] = cursor.getString(0);
			i++;
		}

		cursor.close();

		this.close();

		return ret;

	}

	public void deleteAll() {

		this.open();
		ourDatabase.delete(TABLE_SPENDING, null, null);
		ourDatabase.delete(TABLE_CATEGORIES, null, null);
		ourDatabase.delete(TABLE_REMINDERS, null, null);
		this.close();

	}

	public void deleteCategory(String i_Category) {
		//
		this.open();

		Log.v(TAG, "Deleting category " + i_Category);
		ourDatabase.delete(TABLE_CATEGORIES, KEY_CATEGORY + "=\'" + i_Category
				+ "\'", null);

		this.close();

	}

	public String[][] getTableSpendingData() {
		//
		String[][] ret = null;
		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY };

		// opening database
		this.open();
		// selecting all
		//
		Cursor c = ourDatabase.query(TABLE_SPENDING, columns, null, null, null,
				null, null);

		// setting 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iAmount = c.getColumnIndex(KEY_AMOUNT);
		int iCategory = c.getColumnIndex(KEY_CATEGORY);

		int i = 0;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);

			i++;

		}

		c.close();

		this.close();

		return ret;
	}

	public String[][] getSpentTodayEntries() {

		Calendar now = Calendar.getInstance();
		int todayInMonth;
		String todayInMonthString;
		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory;

		String[][] ret = null;

		now.setTimeInMillis(System.currentTimeMillis());

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY };
		// making sure digit 1 - 9 are 01 - 09
		todayInMonthString = (todayInMonth < 10 ? "0" : "") + todayInMonth;
		Log.v(TAG, "getting all entries for day " + todayInMonthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_DATE
				+ " LIKE '%_____" + todayInMonthString + "T%'", null, null,
				null, null);

		// setting the 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		iRowID = c.getColumnIndex(KEY_ROWID);
		iAmount = c.getColumnIndex(KEY_AMOUNT);
		iCategory = c.getColumnIndex(KEY_CATEGORY);

		i = 0;

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);

			i++;

		}

		c.close();

		this.close();

		return ret;

	}

	public String[][] getSpentThisWeekEnteries(int i_FirstDayOfWeek) {

		Calendar now = Calendar.getInstance();
		int todayInMonth, today, thisYear, thisMonth;
		String todayInMonthString, thisYearString, thisMonthString;

		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory, iDate;
		StringBuilder filter = new StringBuilder();

		String[][] ret = null;

		now.setTimeInMillis(System.currentTimeMillis());

		// getting current month and year
		thisYear = now.get(Calendar.YEAR);
		thisMonth = now.get(Calendar.MONTH) + 1;

		thisYearString = Integer.toString(thisYear);
		thisMonthString = (thisMonth < 10 ? "0" : "") + thisMonth;

		// Getting the current day
		todayInMonth = now.get(Calendar.DAY_OF_WEEK);
		// Checking how many days do we have from the first day of the week
		i = todayInMonth - i_FirstDayOfWeek;

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		while (i > 0) {

			today = todayInMonth - i;
			todayInMonthString = (today < 10 ? "0" : "") + today;
			Log.v(TAG, "adding entry for day " + todayInMonthString);
			filter.append(KEY_DATE);
			filter.append(" LIKE '%" + thisYearString + thisMonthString
					+ todayInMonthString + "T%'");
			filter.append(" OR ");
			i--;

		}

		today = todayInMonth - i;
		todayInMonthString = (today < 10 ? "0" : "") + today;
		Log.v(TAG, "adding entry for day " + todayInMonthString);
		filter.append(KEY_DATE);
		filter.append(" LIKE '%" + thisYearString + thisMonthString
				+ todayInMonthString + "T%'");

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE };
		// making sure digit 1 - 9 are 01 - 09
		todayInMonthString = (todayInMonth < 10 ? "0" : "") + todayInMonth;
		Log.v(TAG, "getting all entries for day " + todayInMonthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, filter.toString(), null,
				null, null, null);

		// setting the 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		iRowID = c.getColumnIndex(KEY_ROWID);
		iAmount = c.getColumnIndex(KEY_AMOUNT);
		iCategory = c.getColumnIndex(KEY_CATEGORY);
		iDate = c.getColumnIndex(KEY_DATE);

		i = 0;

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			Log.v(TAG, String.format("Row ID: %s", c.getShort(iRowID)));

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);
			ret[i][iDate] = c.getString(iDate).split("T")[0];

			i++;

		}

		c.close();

		this.close();

		return ret;

	}

	public String[][] getSpentThisMonthEnteries() {

		Calendar now = Calendar.getInstance();
		int month;
		String monthString;

		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory, iDate;

		String[][] ret = null;

		now.setTimeInMillis(System.currentTimeMillis());

		month = now.get(Calendar.MONTH) + 1;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE };

		// Making sure digit 1 - 9 are 01 - 09
		monthString = (month < 10 ? "0" : "") + month;
		Log.v(TAG, "checking for day " + monthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_DATE + " LIKE '%___"
				+ monthString + "__T%'", null, null, null, null);

		// Setting the 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		iRowID = c.getColumnIndex(KEY_ROWID);
		iAmount = c.getColumnIndex(KEY_AMOUNT);
		iCategory = c.getColumnIndex(KEY_CATEGORY);
		iDate = c.getColumnIndex(KEY_DATE);

		i = 0;

		for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			Log.v(TAG, String.format("Row ID: %s", c.getShort(iRowID)));

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);
			ret[i][iDate] = c.getString(iDate).split("T")[0];

			i++;

		}

		c.close();

		this.close();

		return ret;

	}

	public String[][] getReminders() {
		//
		String[][] ret = null;
		String[] columns = new String[] { KEY_ROWID, KEY_TYPE, KEY_HOUR,
				KEY_MINUTE, KEY_DAY, KEY_AMOUNT, KEY_CATEGORY };

		this.open();

		Cursor c = ourDatabase.query(TABLE_REMINDERS, columns, null, null,
				null, null, null);

		// setting 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iType = c.getColumnIndex(KEY_TYPE);
		int iHour = c.getColumnIndex(KEY_HOUR);
		int iMinute = c.getColumnIndex(KEY_MINUTE);
		int iDay = c.getColumnIndex(KEY_DAY);
		int iAmount = c.getColumnIndex(KEY_AMOUNT);
		int iCategory = c.getColumnIndex(KEY_CATEGORY);

		int i = 0;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iType] = c.getString(iType);
			ret[i][iHour] = c.getString(iHour);
			ret[i][iMinute] = c.getString(iMinute);
			ret[i][iDay] = c.getString(iDay);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);

			i++;

		}

		c.close();

		this.close();

		return ret;
	}

	public void deleteReminderById(String i_ReminderId) {
		//
		this.open();
		int ret;

		ret = ourDatabase.delete(TABLE_REMINDERS, KEY_ROWID + "='"
				+ i_ReminderId + "'", null);
		Log.v(TAG, "Number of rows affected is " + Integer.toString(ret));

		this.close();

	}

	public String[] getEntrySpentByRowId(String i_RowId) {
		//
		Cursor c;

		int iRowID, iAmount, iCategory, iDate;

		String[] ret = null;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE };
		//
		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_ROWID + "='"
				+ i_RowId + "'", null, null, null, null);

		if (c.getCount() > 0) {

			c.moveToFirst();
			ret = new String[columns.length];

			iRowID = c.getColumnIndex(KEY_ROWID);
			iAmount = c.getColumnIndex(KEY_AMOUNT);
			iCategory = c.getColumnIndex(KEY_CATEGORY);
			iDate = c.getColumnIndex(KEY_DATE);

			ret[iRowID] = c.getString(iRowID);
			ret[iAmount] = c.getString(iAmount);
			ret[iCategory] = c.getString(iCategory);
			ret[iDate] = c.getString(iDate);
		}

		c.close();

		this.close();

		return ret;

	}

	public void updateSpentByRowId(String m_RowId, String newAmount,
			String newDate) {
		//
		int ret;
		ContentValues cv = new ContentValues();
		cv.put(KEY_AMOUNT, newAmount);
		cv.put(KEY_DATE, newDate);

		this.open();

		ret = ourDatabase.update(TABLE_SPENDING, cv, KEY_ROWID + "=" + m_RowId,
				null);

		Log.v(TAG, "Updated rows: " + ret);

		this.close();

	}

	private void exportTableToXML(XmlSerializer i_XmlSerializer, String i_Table) {

		Cursor c;
		int columnCount, i;

		try {
			i_XmlSerializer.startTag(null, "TABLE");
			i_XmlSerializer.attribute(null, "NAME", i_Table);

			this.open();

			c = ourDatabase.query(i_Table, null, null, null, null, null, null);

			i_XmlSerializer.startTag(null, "COLUMNS");

			String[] columns = c.getColumnNames();
			columnCount = c.getColumnCount();

			for (i = 0; i < columnCount; i++) {
				i_XmlSerializer.startTag(null, "COLUMNNAME");
				i_XmlSerializer.text(columns[i]);
				i_XmlSerializer.endTag(null, "COLUMNNAME");
			}

			i_XmlSerializer.endTag(null, "COLUMNS");
			i_XmlSerializer.startTag(null, "ENTRIES");

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				i_XmlSerializer.startTag(null, "ENTRY");

				for (i = 0; i < columnCount; i++) {
					Log.v(TAG, "Column is " + columns[i]);
					Log.v(TAG,
							"Column index is " + c.getColumnIndex(columns[i]));
					Log.v(TAG,
							"Value is "
									+ c.getString(c.getColumnIndex(columns[i])));
					try {
						if (c.isNull(c.getColumnIndex(columns[i]))) {
							i_XmlSerializer.attribute(null, columns[i], "");
						} else {

							i_XmlSerializer.attribute(null, columns[i],
									c.getString(c.getColumnIndex(columns[i])));
						}
					} catch (NullPointerException e) {
						// value is null, thus changing to empty string
						// This causes a duplicate attribute name colComment
						// For example: <ENTRY _id="2" colCategory="train"
						// colComment= colComment="" />
						i_XmlSerializer.attribute(null, columns[i], "");
					}

				}

				i_XmlSerializer.endTag(null, "ENTRY");
			}

			i_XmlSerializer.endTag(null, "ENTRIES");

			i_XmlSerializer.endTag(null, "TABLE");

			this.close();

		} catch (IllegalArgumentException e) {
			//
			e.printStackTrace();
		} catch (IllegalStateException e) {
			//
			e.printStackTrace();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}

	}

	public String exportToXMLFile(String i_Filename) {
		String ret = "DB exported okay to " + i_Filename;

		File newXmlFile = new File(Environment.getExternalStorageDirectory()
				+ "/" + i_Filename);
		try {
			newXmlFile.createNewFile();

			FileOutputStream fileOutputStream = new FileOutputStream(newXmlFile);

			XmlSerializer xmlSerializer = Xml.newSerializer();

			xmlSerializer.setOutput(fileOutputStream, "UTF-8");
			xmlSerializer.startDocument(null, Boolean.valueOf(true));

			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);

			xmlSerializer.startTag(null, "ROOT");

			exportTableToXML(xmlSerializer, TABLE_CATEGORIES);
			exportTableToXML(xmlSerializer, TABLE_REMINDERS);
			exportTableToXML(xmlSerializer, TABLE_SPENDING);

			xmlSerializer.endTag(null, "ROOT");

			xmlSerializer.endDocument();
			xmlSerializer.flush();

			fileOutputStream.close();

		} catch (IOException e) {
			ret = e.getMessage();
			Log.e(TAG, e.getMessage());
		} catch (NullPointerException e) {
			//
			ret = e.getMessage();
			Log.e(TAG, e.getMessage());
		}

		return ret;
	}

	public String importFromXMLFile(String i_XmlFile) {
		String ret = "DB imported okay from " + i_XmlFile;

		File xmlFile = new File(Environment.getExternalStorageDirectory() + "/"
				+ i_XmlFile);
		try {
			if (xmlFile.canRead()) {
				SAXParserFactory saxParserFactory = SAXParserFactory
						.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				FileInputStream fileInputStream = new FileInputStream(xmlFile);
				Reader reader = new InputStreamReader(fileInputStream);
				InputSource inputSource = new InputSource(reader);
				inputSource.setEncoding("UTF-8");
				XmlImportDataHandler xmlImportDataHandler = new XmlImportDataHandler();

				saxParser.parse(inputSource, xmlImportDataHandler);

				createCategoresFromImport(xmlImportDataHandler.getCategories());
				createRemindersFromImport(xmlImportDataHandler.getReminders());
				createSpendingFromImport(xmlImportDataHandler.getSpending());

			} else {
				ret = "Could not read " + i_XmlFile;
			}

		} catch (Exception e) {
			//
			ret = e.getMessage();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return ret;

	}

	private void createSpendingFromImport(ArrayList<String[]> spending) {

		this.deleteTableAndResetId(TABLE_SPENDING);

		for (String[] values : spending) {
			this.insertNewSpending(values[1], values[3], values[4], values[2]);

		}

	}

	private void createRemindersFromImport(ArrayList<String[]> reminders) {
		this.deleteTableAndResetId(TABLE_REMINDERS);

		for (String[] values : reminders) {
			this.insertNewReminder(values[1], values[2], values[3], values[4],
					values[5], values[6]);

		}

	}

	private void createCategoresFromImport(ArrayList<String[]> categories) {
		//
		this.deleteTableAndResetId(TABLE_CATEGORIES);

		for (String[] values : categories) {
			this.insertNewCategory(values[1]);
			// ignoring the comment for now

		}

	}

	private void deleteTableAndResetId(String i_TableName) {
		// Deleting table
		this.open();

		this.ourDatabase.delete(i_TableName, null, null);
		// reseting the id number
		this.ourDatabase.delete("sqlite_sequence", "name = '" + i_TableName
				+ "'", null);

		this.close();

	}
}
