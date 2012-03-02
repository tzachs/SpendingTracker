package com.tzachsolomon.spendingtracker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;

import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEntriesSpent extends Activity implements OnGestureListener,
		OnDoubleTapListener {

	private final static String TAG = ViewEntriesSpent.class.getSimpleName();
	public final static int TYPE_MONTH = 0;
	public final static int TYPE_WEEK = 1;
	public final static int TYPE_TODAY = 2;

	private GestureDetector m_Detector;

	private final static String XMLFILE = "spendingTracker.xml";

	private TableLayout tlEntries;

	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	private int m_Type;
	private Calendar m_Calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//
		super.onCreate(savedInstanceState);

		setContentView(R.layout.spending_entries);

		m_Calendar = Calendar.getInstance();
		m_Calendar.setTimeInMillis(System.currentTimeMillis());
		initializeVariables();

		try {

			Bundle extras = getIntent().getExtras();
			m_Type = extras.getInt("TYPE");

			//
		} catch (SQLException e) {
			Toast.makeText(this,
					"Could not populate rows due to " + e.toString(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();

		}

		m_Detector = new GestureDetector(this, this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//
		this.m_Detector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onResume() {
		//

		super.onResume();

		updateTableLayout();
	}

	private void updateTableLayout() {

		String[][] data = null;

		switch (m_Type) {

		case TYPE_TODAY:
			data = m_SpendingTrackerDbEngine.getSpentDailyEntries(m_Calendar);
			break;

		case TYPE_WEEK:
			data = m_SpendingTrackerDbEngine.getSpentThisWeekEnteries(1, m_Calendar);
			break;
		case TYPE_MONTH:
			data = m_SpendingTrackerDbEngine.getSpentThisMonthEnteries(m_Calendar);
			break;

		default:
			break;
		}

		PopulateRows(data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//
		super.onCreateOptionsMenu(menu);

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_spent, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//
		MenuItem menuItemSpentExport = menu.findItem(R.id.menuItemSpentExport);
		MenuItem menuItemSpentImport = menu.findItem(R.id.menuItemSpentImport);

		if (m_Type == TYPE_MONTH) {
			menuItemSpentExport.setVisible(true);
			menuItemSpentImport.setVisible(true);
		} else {
			menuItemSpentExport.setVisible(false);
			menuItemSpentImport.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//
		boolean ret = false;
		switch (item.getItemId()) {

		case R.id.menuItemSpentDelete:
			menuItemSpentDelete_Clicked();
			ret = true;
			break;

		case R.id.menuItemSpentEdit:
			menuItemSpentEdit_Clicked();
			ret = true;
			break;

		case R.id.menuItemSpentExport:
			menuItemSpentExport_Clicked();
			ret = true;
			break;

		case R.id.menuItemSpentImport:
			menuItemSpentImport_Clicked();
			ret = true;
			break;

		default:
			ret = super.onOptionsItemSelected(item);
			break;

		}
		return ret;
	}

	private void menuItemSpentImport_Clicked() {
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog
				.setTitle(getString(R.string.alertDialogImportDatabaseTitle));
		alertDialog
				.setMessage(getString(R.string.alertDialogImportDatabaseMessage));
		alertDialog.setPositiveButton(
				getString(R.string.alertDialogImportDatabasePositive),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						importDatabase();

					}

				});
		alertDialog.setNegativeButton(
				getString(R.string.alertDialogImportDatabaseNegative),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//

					}
				});

		alertDialog.show();

	}

	private void importDatabase() {
		String result = "";

		try {
			result = m_SpendingTrackerDbEngine.importFromXMLFile(XMLFILE);
			updateTableLayout();

		} catch (Exception e) {
			//
			result = e.getMessage();
			e.printStackTrace();
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}

	private void exportDatabase() {

		String result = "";
		try {
			result = m_SpendingTrackerDbEngine.exportToXMLFile(XMLFILE);
		} catch (Exception e) {
			//
			result = e.getMessage();
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();

	}

	private void menuItemSpentExport_Clicked() {
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog
				.setTitle(getString(R.string.alertDialogExportDatabaseTitle));

		alertDialog
				.setMessage(getString(R.string.alertDialogExportDatabaseMessage));

		alertDialog.setPositiveButton(
				getString(R.string.alertDialogExportDatabasePositive),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						exportDatabase();

					}

				});
		alertDialog.setNegativeButton(
				getString(R.string.alertDialogExportDatabaseNegative),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//

					}
				});

		alertDialog.show();

	}

	private void menuItemSpentDelete_Clicked() {
		//

	}

	private void menuItemSpentEdit_Clicked() {
		//
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		final EditText editTextRowId = new EditText(this);

		editTextRowId.setInputType(InputType.TYPE_CLASS_NUMBER);
		editTextRowId.setHint(getString(R.string.editTextRowIdHint));

		alertDialog.setView(editTextRowId);

		alertDialog
				.setTitle(getString(R.string.alertDialogViewEntriesSpentTitle));

		alertDialog.setPositiveButton(
				getString(R.string.alertDialogViewEntriesSpentPositive),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						Intent intent = new Intent(ViewEntriesSpent.this,
								EditEntrySpent.class);

						intent.putExtra("RowId", editTextRowId.getText()
								.toString());

						startActivity(intent);

					}
				});
		alertDialog.setNegativeButton(
				getString(R.string.alertDialogViewEntriesSpentNegative),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//

					}
				});

		alertDialog.show();

	}

	private void PopulateRows(String[][] i_Data) {
		int rows = i_Data.length;
		int i;

		try {
			int howMuchRowsToRemove = tlEntries.getChildCount() - 1;

			// removing all entries except the headers
			tlEntries.removeViews(1, howMuchRowsToRemove);

			for (i = 0; i < rows; i++) {
				AddRowToTable(i_Data[i]);

			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());

		}

	}

	private void AddRowToTable(String[] i_Row) {
		int columns = i_Row.length;
		int i;
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		TextView[] textViews = new TextView[columns];
		// taken from the todays_entries.xml
		int[] layout_weight = new int[] { 10, 20, 60, 10 };

		for (i = 0; i < columns; i++) {
			textViews[i] = new TextView(this);
			textViews[i].setText(i_Row[i]);
			textViews[i].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
					layout_weight[i]));

			tr.addView(textViews[i]);
		}

		tlEntries.addView(tr);

	}

	private void initializeVariables() {
		// initialize members
		tlEntries = (TableLayout) findViewById(R.id.tableLayoutEnteriesSpent);

		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);

	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//
		float direction = e1.getX() - e2.getX();
		if (direction > 0) {
			// move right
			screenSlide(1);
		} else {
			// move left
			screenSlide(-1);

		}

		return true;
	}

	/** Function will add according to m_Type it is currently displaying 
	 * 
	 * @param i_Add - 1 add 1 day / week / month, -1 subtract 1 day / week / month
	 */
	private void screenSlide(int i_Add) {
		//
		
		switch (m_Type) {
		case TYPE_TODAY:
			m_Calendar.add(Calendar.DAY_OF_YEAR, i_Add);
			break;
			
		case TYPE_WEEK:
			
			m_Calendar.add(Calendar.WEEK_OF_YEAR,i_Add);
			break;
			
		case TYPE_MONTH:
			
			m_Calendar.add(Calendar.MONTH,i_Add);
			
			break;

		default:
			break;

		}
		
		showRefrenceDate();
		updateTableLayout();
		

	}

	private void showRefrenceDate() {
		// 
		StringBuilder sb = new StringBuilder();
		
		sb.append("Current refrence date is (YYYY/MM/DD): \n");
		sb.append(m_Calendar.get(Calendar.YEAR));
		sb.append("/");
		sb.append(m_Calendar.get(Calendar.MONTH) + 1);
		sb.append("/");
		sb.append(m_Calendar.get(Calendar.DAY_OF_MONTH));
		
		Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLongPress(MotionEvent e) {
		//

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		//
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
