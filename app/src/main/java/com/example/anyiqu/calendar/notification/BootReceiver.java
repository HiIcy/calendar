package com.example.anyiqu.calendar.notification;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
   public BootReceiver() {    }
    private final String ACTION = "android.intent.action.BOOT_COMPLETED"; 
   @Override 
   public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION)) { 
           Intent inten2 = new Intent(context, NotificationService.class);
          Log.i("hk", "Brodcast");
           context.startService(inten2);
        }  
            boolean isServiceRunning = false;
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) { 
           //检查Service状态   
         ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
          for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {  
              if("com.example.anyiqu.calendar.notification.NotificationService".equals(service.service.getClassName()))                {
              isServiceRunning = true;        
            }
          }
      if (!isServiceRunning) {    
            Intent i = new Intent(context, NotificationService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          // EDITED
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(i);
         }
        }
   }
}