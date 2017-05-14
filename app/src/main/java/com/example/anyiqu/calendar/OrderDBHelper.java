package com.example.anyiqu.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class OrderDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "myTest.db";
    public static final String TABLE_NAME = "schedule";

    public OrderDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE schedule(_id integer primary key autoincrement, " +
                "date text,title text, location text, startime text,endtime text, remindTime Long)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeVersion  = oldVersion;
        if(upgradeVersion == 2) {
            db.execSQL("ALTER TABLE schedule RENAME TO schedule_temp");
            db.execSQL("CREATE TABLE schedule(_id integer primary key autoincrement, " +
                    "date text,title text, location text, startime text,endtime text, remindTime Long)");
            db.execSQL("insert into schedule(_id, date,title,location,startime,endtime,remindTime) "
                    + "select _id, date,title,location,startime,endtime, 11111 from schedule_temp");
            db.execSQL("DROP TABLE schedule_temp");
            upgradeVersion = 3;
        }
        if (upgradeVersion != newVersion) {
            // Drop tables
            db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            // Create tables
            onCreate(db);
        }
    }
}
