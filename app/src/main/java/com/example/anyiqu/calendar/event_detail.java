package com.example.anyiqu.calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class event_detail extends AppCompatActivity implements View.OnClickListener {
    EditText title,location,startime,endtime,remindTime;
    private Event event ;// 当前事务
    ImageButton back_schedule;
    Button edit_icon, share_icon, delete_icon;
    OrderDBHelper dbHelper;
    SQLiteDatabase db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail);
        //获取传过来的数据
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.getSerializable("event");

        dbHelper = new OrderDBHelper(event_detail.this);
        db = dbHelper.getReadableDatabase();

        title = (EditText)findViewById(R.id.detail_Event_title);
        location = (EditText)findViewById(R.id.detail_Event_location);
        startime = (EditText)findViewById(R.id.detail_start_event_time);
        endtime = (EditText)findViewById(R.id.detail_end_event_time);
        remindTime = (EditText) findViewById(R.id.detail_remind);
        //回退按钮
        back_schedule = (ImageButton)findViewById(R.id.img_back_schedule);
        back_schedule.setOnClickListener(this);

        //三大主件icon
        edit_icon = (Button)findViewById(R.id.edit_icon);
        edit_icon.setOnClickListener(this);
        share_icon = (Button)findViewById(R.id.share_icon);
        share_icon.setOnClickListener(this);
        delete_icon = (Button)findViewById(R.id.delete_icon);
        delete_icon.setOnClickListener(this);

        showdetail();
    }
    private void showdetail(){
        title.setText(event.getTitle());
        location.setText(event.getLocation());
        startime.setText(event.getStartime());
        endtime.setText(event.getEndtime());
        remindTime.setText(Remindtime(event.getRemindtime()));
    }
    private String Remindtime(Long remind){
        String format = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        ParsePosition pos = new ParsePosition(0);
        Long stimestap = df.parse(event.getStartime(),pos).getTime();
        long time = stimestap.longValue() - remind.longValue();
        if(time == stimestap){
            format = "无";
        }else if(time == 0){
            format = "准时";
        }
        else if (time / 3600000 < 24 && time / 3600000 >= 0){
            // 如果时间间隔小于24小时则显示多少小时前
            int m = (int) (time / 3600000);// 得出的时间间隔的单位是小时
            format = m + "小时前";
        }
        else if (time / 60000 < 60 && time / 60000 > 0){
            // 如果时间间隔小于60分钟则显示多少分钟前
            int m = (int) ((time % 3600000) / 60000);// 得出的时间间隔的单位是分钟
            format = m + "分钟前";
        }else {
            format = df.format(time);
        }
        return format;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back_schedule:
                finish();
                break;
            case R.id.edit_icon:
                Bundle b = new Bundle();
                b.putSerializable("event",event);
                Intent inten = new Intent();
                inten.setClass(event_detail.this, Redit_event.class);
                inten.putExtras(b);
                inten.putExtra("remindTime",remindTime.getText().toString());
                startActivity(inten);
                break;
            case R.id.delete_icon:
                deleteData(event);
                Intent intent = new Intent();
                intent.setClass(this,event_schedules_view.class);
                //singletask模式
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除上一个activity，当前activity
                //启动intent对应的Activity
                startActivity(intent);
                finish();
                break;
            case R.id.share_icon:
            // 分享功能
                StringBuilder strinfo = null;
                strinfo=Shareinfo();
                Intent email=new Intent(Intent.ACTION_SEND);
                email.setType("text/plain"); //分享数据类型
                email.putExtra(android.content.Intent.EXTRA_SUBJECT, "事件opening");//标题
                email.putExtra(Intent.EXTRA_TEXT,String.valueOf(strinfo));//内容
                startActivityForResult(Intent.createChooser(email, "请选择邮件发送软件"),1001);
                break;
            default:
                break;
        }
    }
    private StringBuilder Shareinfo(){
       StringBuilder strinfo = new StringBuilder().append("事件标题： ").append(title.getText().toString())
                .append("\n"+"事件位置:  ").append(location.getText().toString())
                .append("\n"+"开始时间:  ").append(startime.getText().toString())
                .append("\n"+"结束时间：").append(endtime.getText().toString());
        return strinfo;
    }
    private void deleteData(Event event) {
        String title = event.getTitle();
        String location = event.getLocation();
        String SQL = "DELETE FROM SCHEDULE WHERE TITLE=? and LOCATION=?";
        db.execSQL(SQL,new String[]{title, location});
        db.close();
    }
}


    /*ActionBar actionBar = getActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);*/