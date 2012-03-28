package com.tzachsolomon.spendingtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class SpendingTrackerLocationService extends Service implements LocationListener {

	private static final String TAG = SpendingTrackerService.class
	.getSimpleName();
	
	private SpendingTrackerDbEngine m_SpendingTrackerDbEngine;
	
	NotificationManager m_NotificationManager;
	Notification m_Notification;
	LocationManager m_LocationManager;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// 
		return null;
	}
	
	@Override
	public void onDestroy() {
		// 
		Log.i(TAG, "Service Destroyed");
		
		m_LocationManager.removeUpdates(this);
		
		super.onDestroy();
	}
	
	@Override
	public void onCreate() {
		//
		super.onCreate();
		
		Log.i(TAG, "Service Created");
		
		initializeVariables();
		initLocalationManager();
		
	}
	
	private void initLocalationManager() {
 
		//
		m_LocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		m_LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0,0,this);
		
		
	}

	private void initializeVariables() {
		// 
		m_SpendingTrackerDbEngine = new SpendingTrackerDbEngine(this);
		
		
	}

	

	@Override
	public void onLocationChanged(Location location) {
		// 
		StringBuilder sb = new StringBuilder();
		
		sb.append("Accuracy " + location.getAccuracy() + "\n");
		sb.append("Altitude " + location.getAltitude() + "\n");
		sb.append("Bearing " + location.getBearing() + "\n");
		sb.append("Latitude " + location.getLatitude() + "\n");
		sb.append("Longitude " + location.getLongitude() + "\n");
		sb.append("Provider " + location.getProvider() + "\n");
		sb.append("Speed " + location.getSpeed() + "\n");
		sb.append("Time " + location.getTime() + "\n");
		sb.append("Time " + location.getTime() + "\n");
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// 
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// 
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// 
		
	}
	
	

}
