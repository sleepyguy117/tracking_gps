package com.example.hanswee.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private  static String TAG = MainActivity.class.toString();

    private Button testButton;
    private Button startService;
    private Button stopService;
    private Button resultsButton;
    private MyDbHelper dbHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testButton = (Button) findViewById(R.id.test_button);
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);


        resultsButton = (Button) findViewById(R.id.results_button);



        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    1 );
        }

        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    1 );
        }


        dbHelper = new MyDbHelper(this);

        mDb = dbHelper.getWritableDatabase();


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("com.example.hanswee.restartservice"));
            }
        });


        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(MainActivity.this, TrackService.class);
                startService(serviceIntent);
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(MainActivity.this, TrackService.class);
                stopService(serviceIntent);
            }
        });

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = mDb.rawQuery("select " + MyDbHelper.COLUMN_KEY +
                        ", sum(" + MyDbHelper.COLUMN_WEIGHT + ") AS WEIGHTSUM, count(*) AS RECORDCOUNT"
                        + ", sum(" +  MyDbHelper.COLUMN_LONG + ") AS LONSUM, sum(" +  MyDbHelper.COLUMN_LAT + ") AS LATSUM FROM "
                        + MyDbHelper.TABLE_TRACK + " GROUP BY " + MyDbHelper.COLUMN_KEY + " ORDER BY  sum(" + MyDbHelper.COLUMN_WEIGHT + ") desc LIMIT 1" , new String[]{});


                int count = c.getCount();

                if(count > 0) {

                    c.moveToFirst();

                    double lonsum = c.getDouble(3);
                    double latsum = c.getDouble(4);
                    int recordcount = c.getInt(2);

                    Log.d(TAG, "key = " + c.getString(0) + " weightsum = " + c.getLong(1) + " count = "
                                + c.getInt(2) + " longsum = " + c.getDouble(3) + " latsum = " + c.getDouble(4));


                    String toast = "guess of your home due to your most highly weighted location - lat: " + (latsum / (double) recordcount) +
                            " lon: " + (lonsum / (double) recordcount);

                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();

                }

                c.close();
            }
        });


    }



}
