package com.example.anyiqu.calendar.notification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.anyiqu.calendar.Event;
import com.example.anyiqu.calendar.OrderDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class NotificationService extends Service {
    SQLiteDatabase db;
    Calendar cal;
    OrderDBHelper dbHelper;
    private AlarmManager am;//闹钟管理器
    private PendingIntent pi;
    private Long rtime,_id;
    private String title=null,location=null,startime=null,endtime=null,date=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
        Log.i("sf","sf");
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("sf","孙菲菲=======#$$$$$$$$$$$$$$$$$$$$$$$");
        getAlarmTime();
        return START_REDELIVER_INTENT;    } //这里为了提高优先级，选择START_REDELIVER_INTENT 没那么容易被内存清理时杀死
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAlarmTime() {
        dbHelper  = new OrderDBHelper(NotificationService.this);
        db = dbHelper.getReadableDatabase(); //  Sqlitedatabase对象
        Cursor cursor = db.query("schedule", null, null, null, null, null, null,null);
        if (cursor.moveToFirst()) { //遍历数据库的表，拿出一条，选择最近的时间赋值，作为第一条提醒数据。
            _id = cursor.getLong(0);
            rtime = cursor.getLong(cursor.getColumnIndex("remindTime"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            location = cursor.getString(cursor.getColumnIndex("location"));
            startime = cursor.getString(cursor.getColumnIndex("startime"));
            endtime = cursor.getString(cursor.getColumnIndex("endtime"));
            while (cursor.moveToNext()) {
                if (rtime > cursor.getLong(cursor.getColumnIndex("remindTime"))) {
                    _id = cursor.getLong(0);
                    rtime = cursor.getLong(cursor.getColumnIndex("remindTime"));
                    date = cursor.getString(cursor.getColumnIndex("date"));
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    location = cursor.getString(cursor.getColumnIndex("location"));
                    startime = cursor.getString(cursor.getColumnIndex("startime"));
                    endtime = cursor.getString(cursor.getColumnIndex("endtime"));
                }
            }
        } else {
            rtime = null;
        }
        db.delete("schedule", "remindTime=?", new String[]{String.valueOf(rtime)});      //删除已经发送提醒的时间
//         db.execSQL("update schedule set remindTime=? where _id=?", new String[]{String.valueOf(0),  String.valueOf(_id)});
        Log.i("SQL","数据库执行");
        cursor.close();     //记得关闭游标，防止内存泄漏
        Intent startNotification = new Intent(this, AlarmReceiver.class);   //这里启动的广播，下一步会教大家设置
        startNotification.putExtra("title", title);
        startNotification.putExtra("location", location);
        startNotification.putExtra("startime", startime);
        startNotification.putExtra("endtime", endtime);
        startNotification.putExtra("_id", _id);

        am = (AlarmManager) getSystemService(ALARM_SERVICE);   //这里是系统闹钟的对象
        int requestCode = (int) SystemClock.uptimeMillis();  // 避免点击失效
        pi = PendingIntent.getBroadcast(this, 0, startNotification, PendingIntent.FLAG_CANCEL_CURRENT);     //设置事件
        if (rtime != null){
            Log.i("Alarm","闹钟已上线");
            am.setExact(AlarmManager.RTC_WAKEUP, rtime.longValue(), pi);    //提交事件，发送给 广播接收器
        }
//        else {//当提醒时间为空的时候，关闭服务，下次添加提醒时再开启
//            stopService(new Intent(this, NotificationService.class));
//        }
    }
    public void onDestroy() {
        Intent sevice = new Intent(this,NotificationService.class);
        this.startService(sevice);
        super.onDestroy();
    }
}

