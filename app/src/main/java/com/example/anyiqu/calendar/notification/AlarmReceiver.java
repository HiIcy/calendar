package com.example.anyiqu.calendar.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Random;
import com.example.anyiqu.calendar.Event;
import com.example.anyiqu.calendar.R;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager manager;
    private Event event;
    private static final int NOTIFICATION_ID_1 = 0x00113;
    private String title,location,startime,endtime,date;
    protected Bundle bundle;
    protected Random rad = new Random();
    private String id;
    @Override
    public void onReceive(Context context, Intent intent) {
        //此处接收闹钟时间发送过来的广播信息，为了方便设置提醒内容
        title = intent.getStringExtra("title");
        location = intent.getStringExtra("location");
        startime = intent.getStringExtra("startime");
        endtime = intent.getStringExtra("endtime");
        id = intent.getStringExtra("_id");
//        init(intent);
        showNormal(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setClass(context, NotificationService.class);
        Log.i("fasheng","是否回调");
        context.startService(intent);//回调Service,同一个Service只会启动一个，所以直接再次启动Service，会重置开启新的提醒，
    }
    /**     * 发送通知     */
    private void showNormal(Context context) {

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)     //设置通知图标。
                .setTicker("岁墨")        //通知时在状态栏显示的通知内容
                .setContentInfo("爱")        //内容信息
                .setContentTitle(title)        //设置通知标题。
                .setContentText(location+"\n"+startime+"--"+endtime)        //设置通知内容。
                .setAutoCancel(true)                //点击通知后通知消失
                .setDefaults(Notification.DEFAULT_ALL)        //设置系统默认的通知音乐、振动、LED等。
//               .setContentIntent(pi)
                .build();
        manager.notify(rad.nextInt(100), notification);
    }
}
