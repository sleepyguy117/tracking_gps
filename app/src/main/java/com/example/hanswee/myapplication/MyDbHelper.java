package com.example.hanswee.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hanswee on 4/5/17.
 */

public class MyDbHelper extends SQLiteOpenHelper {


    public static final String TABLE_TRACK = "track";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAT = "LAT";
    public static final String COLUMN_LONG = "LONG";
    public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
    public static final String COLUMN_KEY = "KEY";
    public static final String COLUMN_WEIGHT = "WEIGHT";

    private static final String DATABASE_NAME = "track.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TRACK + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_KEY + " text not null, "
            + COLUMN_LAT + " REAL not null, "
            + COLUMN_LONG + " REAL not null, "
            + COLUMN_WEIGHT + " integer not null, "
            + COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";


    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
        onCreate(db);
    }
}
