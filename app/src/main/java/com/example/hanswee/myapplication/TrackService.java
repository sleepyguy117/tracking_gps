package com.example.hanswee.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hanswee on 4/7/17.
 */

public class TrackService extends Service {

    private static String TAG = TrackService.class.toString();

    static String makeKey(double lat, double lon)
    {

        return String.format("%.4f,%.4f", lat, lon);
    }

    private MyDbHelper dbHelper;
    private SQLiteDatabase mDb;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;    //10 minutes check gps
    private static final float LOCATION_DISTANCE = 0f;

    private class MyLocationListener implements LocationListener
    {
        Location mLastLocation;
        String mProvider;

        public MyLocationListener(String provider)
        {
            mProvider = provider;
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation.set(location);

            ContentValues insertValues = new ContentValues();

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            //this rounds the key by gps so it shouuld group gps coordinates in a .0001 gps radius together
            String key = makeKey(lat, lon);

            insertValues.put(MyDbHelper.COLUMN_KEY, key);
            insertValues.put(MyDbHelper.COLUMN_LAT, lat);
            insertValues.put(MyDbHelper.COLUMN_LONG, lon);

            int weight = 1;

            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            int hour = c.get(Calendar.HOUR_OF_DAY);

            if(hour < 4 || hour > 22 )
            {
                weight = 3;     //if it is at night, you are probably at home so weigh it more
            }

            insertValues.put(MyDbHelper.COLUMN_WEIGHT, weight);
            mDb.insert(MyDbHelper.TABLE_TRACK, null, insertValues);

            Log.d(TAG, "provider = " + mProvider + " key = " + key + " lat is " + location.getLatitude() + " lon is " + location.getLongitude() );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new MyLocationListener(LocationManager.GPS_PROVIDER),
            new MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        dbHelper = new MyDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);
            } catch (SecurityException e)
            {
                Log.d(TAG, "security exception");
            }
            catch (Exception e )
            {
                Log.d(TAG, "something else broke");
            }

            Log.d(TAG, "gps location manager init success");
        }

        if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            } catch (SecurityException e) {
                Log.d(TAG, "security exception");
            } catch (Exception e) {
                Log.d(TAG, "something else broke");
            }

            Log.d(TAG, "network location manager init success");
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);

        Notification notification=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("tracking you")
                .setContentTitle("tracking")
                .setContentIntent(pendingIntent)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                }
            }
        }
    }



}
