package com.example.anyiqu.calendar.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.anyiqu.calendar.MainActivity;
import com.example.anyiqu.calendar.R;
import com.example.anyiqu.calendar.notification.BootReceiver;
import com.example.anyiqu.calendar.notification.NotificationService;

//animation启动界面
public class launch_face extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGHT = 2000; //延迟三秒
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_interface);

        Intent intentsive = new Intent(this,NotificationService.class);
        startService(intentsive);

        Intent intentive= new Intent(this,BootReceiver.class);
        startService(intentive);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(launch_face.this,MainActivity.class);
                launch_face.this.startActivity(mainIntent);
                launch_face.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
