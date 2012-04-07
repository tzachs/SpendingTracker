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

	public static final String KEY_ACCURACY = "colAccuracy";
	public static final String KEY_ALTITUDE = "colAltitude";
	public static final String KEY_BEARING = "colBearing";
	public static final String KEY_LATITUDE = "colLatitude";
	public static final String KEY_LONGITUDE = "colLongitude";
	public static final String KEY_PROVIDER = "colProvider";
	public static final String KEY_SPEED = "colSpeed";
	public static final String KEY_TIME = "colTime";
	public static final String KEY_LOCATION_NAME = "colLocationName";

	public static final String KEY_REMINDER_ID = "colReminderId";
	public static final String KEY_REMINDER_TYPE = "colReminderType";
	public static final String KEY_REMINDER_TYPE_LOCATION = "location";
	public static final String KEY_REMINDER_STATUS = "colReminderStatus";
	public static final String KEY_REMINDER_STATUS_PENDING = "pending";
	public static final String KEY_REMINDER_STATUS_PRESSED = "pressed";
	

	private static final String DATABASE_NAME = "SpendingTrackerDb";
	private static final String TABLE_SPENDING = "tblSpending";
	private static final String TABLE_CATEGORIES = "tblCategories";
	private static final String TABLE_REMINDERS = "tblReminders";
	private static final String TABLE_LOCATION_REMINDERS = "tblLocations";
	private static final String TABLE_REMINDERS_QUEUE = "tblRemindersQueue";

	private static final int DATABASE_VERSION = 2;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	private String m_SortByKey;

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		private void createTableSpending(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_SPENDING + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_AMOUNT
					+ " TEXT NOT NULL, " + KEY_DATE + " TEXT NOT NULL,"
					+ KEY_CATEGORY + " TEXT NOT NULL, " + KEY_COMMENT
					+ " TEXT " + ");");
		}

		private void createTableCategories(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CATEGORY
					+ " TEXT NOT NULL, " + KEY_COMMENT + " TEXT " + ");");

		}

		private void createTableTimeReminders(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + TABLE_REMINDERS + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TYPE
					+ " TEXT NOT NULL, " + KEY_HOUR + " TEXT NOT NULL, "
					+ KEY_MINUTE + " TEXT NOT NULL, " + KEY_DAY
					+ " TEXT NOT NULL, " + KEY_AMOUNT + " TEXT NOT NULL, "
					+ KEY_CATEGORY + " TEXT NOT NULL "

					+ ");");

		}

		private void createTableLocationReminders(SQLiteDatabase db) {

			String sqlQuery = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_LOCATION_REMINDERS + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ACCURACY
					+ " TEXT NOT NULL, " + KEY_ALTITUDE + " TEXT NOT NULL, "
					+ KEY_BEARING + " TEXT NOT NULL, " + KEY_LATITUDE
					+ " TEXT NOT NULL, " + KEY_LONGITUDE + " TEXT NOT NULL, "
					+ KEY_PROVIDER + " TEXT NOT NULL, " + KEY_SPEED
					+ " TEXT NOT NULL, " + KEY_TIME + " TEXT NOT NULL, "
					+ KEY_LOCATION_NAME + " TEXT NOT NULL, "
					+ KEY_AMOUNT + " TEXT NOT NULL, " + KEY_CATEGORY
					+ " TEXT NOT NULL " + ");";

			Log.d(TAG, "Executing the following queries: ");
			Log.d(TAG, sqlQuery);

			db.execSQL(sqlQuery);
			
		}

		private void createTableRemindersQueue(SQLiteDatabase db) {

			String sqlQuery = "CREATE TABLE IF NOT EXISTS "
					+ TABLE_REMINDERS_QUEUE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_REMINDER_TYPE + " TEXT NOT NULL, "
					+ KEY_REMINDER_STATUS + " TEXT NOT NULL, "
					+ KEY_REMINDER_ID + " TEXT NOT NULL " + ");";

			db.execSQL(sqlQuery);
			
			ContentValues cv = new ContentValues();
			
			cv.put(KEY_REMINDER_TYPE, "-1");
			cv.put(KEY_REMINDER_STATUS, "-1");
			cv.put(KEY_REMINDER_ID, "-1");
			
			db.insert(TABLE_REMINDERS_QUEUE, null, cv);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			createTableSpending(db);
			createTableCategories(db);
			createTableTimeReminders(db);
			createTableLocationReminders(db);
			createTableRemindersQueue(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.v(TAG, DATABASE_NAME + "is upgraded from version " + oldVersion
					+ " to version " + newVersion);
			
			createTableLocationReminders(db);

			db.execSQL("DROP TABLE " + TABLE_REMINDERS_QUEUE);
			createTableRemindersQueue(db);

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
			//
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
			//
			super.endElement(uri, localName, qName);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			//
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
		m_SortByKey = SpendingTrackerDbEngine.KEY_ROWID;
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

	public long insertNewTimeReminder(String i_Type, String i_Hour,
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

	/**
	 * Function calculate total spent today and return as string
	 * 
	 * @return Total spent today
	 */
	public String getSpentToday() {

		Calendar now = Calendar.getInstance();
		int todayInMonth, thisYear, thisMonth;
		String todayInMonthString, thisYearString, thisMonthString;
		Cursor c;
		int iColDate, iColAmount;

		float ret = 0;

		now.setTimeInMillis(System.currentTimeMillis());

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		// getting current month and year
		thisYear = now.get(Calendar.YEAR);
		thisMonth = now.get(Calendar.MONTH) + 1;

		thisYearString = Integer.toString(thisYear);
		thisMonthString = (thisMonth < 10 ? "0" : "") + thisMonth;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE, KEY_COMMENT };
		// making sure digit 1 - 9 are 01 - 09
		todayInMonthString = (todayInMonth < 10 ? "0" : "") + todayInMonth;
		Log.v(TAG, "checking for day " + todayInMonthString);

		c = ourDatabase.query(TABLE_SPENDING, columns,
				KEY_DATE + " LIKE '%" + thisYearString + thisMonthString
						+ todayInMonthString + "T%'", null, null, null, null);

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
				new String[] { KEY_CATEGORY }, null, null, null, null,
				KEY_CATEGORY);

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

	/**
	 * Function return the all the entries spent on specific day using
	 * i_Calendar as a point of reference
	 * 
	 * @param i_Calendar
	 *            - If null, function uses now as date reference, else uses the
	 *            i_Calendar as a reference point
	 * @return All the entries spent in a specific day
	 */
	public String[][] getSpentDailyEntries(Calendar i_Calendar) {

		Calendar now;
		int todayInMonth, thisYear, thisMonth;
		String todayInMonthString, thisYearString, thisMonthString;
		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory;

		String[][] ret = null;

		if (i_Calendar == null) {
			now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
		} else {
			now = i_Calendar;
		}

		todayInMonth = now.get(Calendar.DAY_OF_MONTH);

		// getting current month and year
		thisYear = now.get(Calendar.YEAR);
		thisMonth = now.get(Calendar.MONTH) + 1;

		thisYearString = Integer.toString(thisYear);
		thisMonthString = (thisMonth < 10 ? "0" : "") + thisMonth;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY };
		// making sure digit 1 - 9 are 01 - 09
		todayInMonthString = (todayInMonth < 10 ? "0" : "") + todayInMonth;
		Log.v(TAG, "getting all entries for day " + todayInMonthString);

		c = ourDatabase.query(TABLE_SPENDING, columns,
				KEY_DATE + " LIKE '%" + thisYearString + thisMonthString
						+ todayInMonthString + "T%'", null, null, null,
				m_SortByKey);

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

	/**
	 * Function return the all the entries spent on specific Week using
	 * i_Calendar as a point of reference
	 * 
	 * @param i_Calendar
	 *            - If null, function uses now as date reference, else uses the
	 *            i_Calendar as a reference point
	 * @return All the entries spent in a specific Week
	 */

	public String[][] getSpentThisWeekEnteries(int i_FirstDayOfWeek,
			Calendar i_Calendar) {

		Calendar now;
		int todayInMonth, today, thisYear, thisMonth;
		String todayInMonthString, thisYearString, thisMonthString;

		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory, iDate;
		StringBuilder filter = new StringBuilder();

		String[][] ret = null;

		if (i_Calendar == null) {
			now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
		} else {
			now = i_Calendar;
		}

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
				null, null, m_SortByKey);

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

	public String[][] getSpentThisMonthEnteries(Calendar i_Calendar) {

		Calendar now;
		int month;
		String monthString;

		Cursor c;
		int i = 0;
		int iRowID, iAmount, iCategory, iDate;

		String[][] ret = null;

		if (i_Calendar == null) {
			now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
		} else {
			now = i_Calendar;
		}

		Log.i(TAG, "Getting month entries using " + now.toString());

		month = now.get(Calendar.MONTH) + 1;

		this.open();

		String[] columns = new String[] { KEY_ROWID, KEY_AMOUNT, KEY_CATEGORY,
				KEY_DATE };

		// Making sure digit 1 - 9 are 01 - 09
		monthString = (month < 10 ? "0" : "") + month;
		Log.v(TAG, "checking for day " + monthString);

		c = ourDatabase.query(TABLE_SPENDING, columns, KEY_DATE + " LIKE '%___"
				+ monthString + "__T%'", null, null, null, m_SortByKey);

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

	public String[][] getTimeReminders() {
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

	public ContentValues[] getLocationReminders() {

		ContentValues[] ret = null;

		String[] columns = new String[] { KEY_ROWID, KEY_ACCURACY,
				KEY_ALTITUDE, KEY_BEARING, KEY_LATITUDE, KEY_LONGITUDE,
				KEY_PROVIDER, KEY_SPEED, KEY_TIME, KEY_AMOUNT, KEY_CATEGORY, KEY_LOCATION_NAME };

		this.open();

		Cursor c = ourDatabase.query(TABLE_LOCATION_REMINDERS, columns, null,
				null, null, null, null);

		// setting 2 dimensional array
		ret = new ContentValues[c.getCount()];

		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iKEY_ACCURACY = c.getColumnIndex(KEY_ACCURACY);
		int iKEY_ALTITUDE = c.getColumnIndex(KEY_ALTITUDE);
		int iKEY_BEARING = c.getColumnIndex(KEY_BEARING);
		int iKEY_LATITUDE = c.getColumnIndex(KEY_LATITUDE);
		int iKEY_LONGITUDE = c.getColumnIndex(KEY_LONGITUDE);
		int iKEY_PROVIDER = c.getColumnIndex(KEY_PROVIDER);
		int iKEY_SPEED = c.getColumnIndex(KEY_SPEED);
		int iKEY_TIME = c.getColumnIndex(KEY_TIME);
		int iKEY_LOCATION_NAME = c.getColumnIndex(KEY_LOCATION_NAME);

		int iAmount = c.getColumnIndex(KEY_AMOUNT);
		int iCategory = c.getColumnIndex(KEY_CATEGORY);

		int i = 0;

		try {

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				ret[i] = new ContentValues();

				ret[i].put(KEY_ROWID, c.getString(iRowID));
				ret[i].put(KEY_ACCURACY, c.getString(iKEY_ACCURACY));
				ret[i].put(KEY_ALTITUDE, c.getString(iKEY_ALTITUDE));
				ret[i].put(KEY_BEARING, c.getString(iKEY_BEARING));
				ret[i].put(KEY_LATITUDE, c.getString(iKEY_LATITUDE));
				ret[i].put(KEY_LONGITUDE, c.getString(iKEY_LONGITUDE));
				ret[i].put(KEY_PROVIDER, c.getString(iKEY_PROVIDER));
				ret[i].put(KEY_SPEED, c.getString(iKEY_SPEED));
				ret[i].put(KEY_TIME, c.getString(iKEY_TIME));
				ret[i].put(KEY_AMOUNT, c.getString(iAmount));
				ret[i].put(KEY_CATEGORY, c.getString(iCategory));
				ret[i].put(KEY_LOCATION_NAME, c.getString(iKEY_LOCATION_NAME));

				i++;

			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage().toString());
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

	public void updateSpentByRowId(String m_RowId, String i_NewAmount,
			String i_NewDate, String i_NewCategory) {
		//
		int ret;
		ContentValues cv = new ContentValues();
		cv.put(KEY_AMOUNT, i_NewAmount);
		cv.put(KEY_DATE, i_NewDate);
		cv.put(KEY_CATEGORY, i_NewCategory);

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
			this.insertNewTimeReminder(values[1], values[2], values[3],
					values[4], values[5], values[6]);

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

	public void deleteSpentEntryByRowId(String i_ReminderId) {
		//
		this.open();
		int ret;

		ret = ourDatabase.delete(TABLE_SPENDING, KEY_ROWID + "='"
				+ i_ReminderId + "'", null);
		Log.v(TAG, "Number of rows affected is " + Integer.toString(ret));

		this.close();

	}

	public void setSortBy(String i_Key) {
		//
		m_SortByKey = i_Key;

	}

	public long insertNewLocationReminder(String i_Accuracy, String i_Altitude,
			String i_Bearing, String i_Latitude, String i_Longitude,
			String i_Provider, String i_Speed, String i_Time, String i_Amount,
			String i_Category, String i_LocationName) {
		//
		long ret;
		ContentValues cv = new ContentValues();

		cv.put(KEY_ACCURACY, i_Accuracy);
		cv.put(KEY_ALTITUDE, i_Altitude);
		cv.put(KEY_BEARING, i_Bearing);
		cv.put(KEY_LATITUDE, i_Latitude);
		cv.put(KEY_LONGITUDE, i_Longitude);
		cv.put(KEY_PROVIDER, i_Provider);
		cv.put(KEY_SPEED, i_Speed);
		cv.put(KEY_TIME, i_Time);
		cv.put(KEY_AMOUNT, i_Amount);
		cv.put(KEY_CATEGORY, i_Category);
		cv.put(KEY_LOCATION_NAME, i_LocationName);

		this.open();

		ret = ourDatabase.insert(TABLE_LOCATION_REMINDERS, null, cv);

		this.close();

		return ret;

	}

	public boolean isLocationChanged(String rowId) {

		// Checking if the current location is last one found
		// If the location is new the first id in TABLE_REMINDERS_QUERE will not be equal to rowId
		boolean ret = false;
		Cursor c;
		StringBuilder filter = new StringBuilder();
		
		filter.append(KEY_ROWID);
		filter.append("='1'");
		filter.append(" AND ");
		filter.append(KEY_REMINDER_ID);
		filter.append("='");
		filter.append(rowId);
		filter.append("'");
		

		this.open();
		// getting the last row and checking if it is our rowId
		c = ourDatabase.query(TABLE_REMINDERS_QUEUE, null, filter.toString(),
				null, null, null, null,null);

		try {
			// If no row was found than this is the first time we are at this location
			if ( c.getCount() == 0 ){
				// updating last location 
				ContentValues cv = new ContentValues();
				
				cv.put(KEY_REMINDER_ID, rowId);
				
				ourDatabase.update(TABLE_REMINDERS_QUEUE, cv,KEY_ROWID + "='1'", null);
				ret = true;
			}
 
		} catch (Exception e) {
			//
			Log.d(TAG, e.getMessage().toString());
			
		}
		
		c.close();

		this.close();

		return ret;

	}

	public int changeLocationReminderToPressed(String rowId) {

		this.open();
		int ret;

		ContentValues cv = new ContentValues();

		cv.put(KEY_REMINDER_STATUS, KEY_REMINDER_STATUS_PRESSED);

		ret = ourDatabase.update(TABLE_REMINDERS_QUEUE, cv, KEY_REMINDER_ID
				+ "='" + rowId + "'", null);

		this.close();

		return ret;

	}

	public String[][] getLocationRemindersAsStrings() {
		
		
		String[][] ret = null;
		String[] columns = new String[] { KEY_ROWID, KEY_LOCATION_NAME, KEY_AMOUNT, KEY_CATEGORY };

		this.open();

		Cursor c = ourDatabase.query(TABLE_LOCATION_REMINDERS, columns, null, null,
				null, null, null);

		// setting 2 dimensional array
		ret = new String[c.getCount()][columns.length];

		int iRowID = c.getColumnIndex(KEY_ROWID);
		int iLocationName = c.getColumnIndex(KEY_LOCATION_NAME);
		int iAmount = c.getColumnIndex(KEY_AMOUNT);
		int iCategory = c.getColumnIndex(KEY_CATEGORY);

		int i = 0;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			ret[i][iRowID] = c.getString(iRowID);
			ret[i][iLocationName] = c.getString(iLocationName);
			ret[i][iAmount] = c.getString(iAmount);
			ret[i][iCategory] = c.getString(iCategory);

			i++;

		}

		c.close();

		this.close();

		return ret;
		
	}

	public void deleteLocationReminderById(String i_ReminderId) {
		// 
		this.open();
		int ret;

		ret = ourDatabase.delete(TABLE_LOCATION_REMINDERS, KEY_ROWID + "='"
				+ i_ReminderId + "'", null);
		Log.v(TAG, "Number of rows affected is " + Integer.toString(ret));

		this.close();
		
	}

	public void deleteAllSentNotifications() {
		// 
		deleteTableAndResetId(TABLE_REMINDERS_QUEUE);
		
		// Resetting last known location
		ContentValues cv = new ContentValues();
		
		cv.put(KEY_REMINDER_TYPE, "-1");
		cv.put(KEY_REMINDER_STATUS, "-1");
		cv.put(KEY_REMINDER_ID, "-1");
		
		this.open();
		
		ourDatabase.insert(TABLE_REMINDERS_QUEUE, null, cv);
		
		this.close();
		
		
	}

}
