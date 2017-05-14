package com.example.anyiqu.calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Redit_event extends AppCompatActivity {
    Long id;
    SQLiteDatabase db;
    Calendar cal;
    OrderDBHelper dbHelper;
    private ImageButton img_event_confirm, img_event_cancle;
    private View popupWindowView;
    private PopupWindow popupWindow;
    private Button btnsure, btncancle;
    private EditText start_time, end_time;
    private EditText timeword;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText show;
    private static StringBuilder sstime;
    static int i;
    Event event;
    MyThread mythread;
    MyHandler handler;
    Thread thread;
    EditText event_title_redit;
    EditText event_location_redit,event_remind_redit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redit_event);
        start_time = (EditText) findViewById(R.id.start_event_redit);
        end_time = (EditText) findViewById(R.id.end_event_redit);
        img_event_cancle = (ImageButton) findViewById(R.id.redit_event_cancle);
        img_event_confirm = (ImageButton) findViewById(R.id.redit_event_confirm);
        dbHelper = new OrderDBHelper(Redit_event.this);
        db = dbHelper.getReadableDatabase(); //  Sqlitedatabase对象
        event_title_redit = (EditText) findViewById(R.id.Event_title_redit);
        event_location_redit = (EditText) findViewById(R.id.Event_location_redit);
        event_remind_redit = (EditText) findViewById(R.id.Event_remind_redit);

        init();
        img_event_confirm.setOnClickListener(new UpdateToDatabase());
        img_event_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束当前Activity 返回到上一级activity
                finish();
            }
        });
        start_time.setOnClickListener(new EditTextOnclickListener());
        end_time.setOnClickListener(new EditTextOnclickListener());

        handler = new MyHandler();//消息处理
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        String remindtime = getIntent().getStringExtra("remindTime");
        event = (Event) bundle.getSerializable("event");
        event_title_redit.setText(event.getTitle());
        event_location_redit.setText(event.getLocation());
        start_time.setText(event.getStartime());
        end_time.setText(event.getEndtime());
        event_remind_redit.setText(remindtime);

        id = queryid(db, event.getTitle(), event.getLocation());
    }

    //    public
    public void OpenView() {
//        对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入
//        对于一个已经载入的界面，就可以使用Activiyt.findViewById()方法来获得其中的界面元素
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        inflater 是用来找 res/layout下的 xml 布局文件，并且实例化
        popupWindowView = inflater.inflate(R.layout.select_date, null);
        popupWindowView.setFocusable(true);
        popupWindowView.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(popupWindowView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(false);
        //        设置窗口动画
        popupWindow.setAnimationStyle(R.style.popupAnimation);
        btnsure = (Button) popupWindowView.findViewById(R.id.select_date_confirm);
        btnsure.setOnClickListener(new EditTextOnclickListener());
        btncancle = (Button) popupWindowView.findViewById(R.id.select_date_cancle);
        btncancle.setOnClickListener(new EditTextOnclickListener());
        timeword = (EditText) popupWindowView.findViewById(R.id.start_event_time);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x90000000));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);

//    获取文本 展示时间 在popupwindow上
        show = (EditText) popupWindowView.findViewById(R.id.editText_date);
        datePicker = (DatePicker) popupWindowView.findViewById(R.id.date_picker);
        timePicker = (TimePicker) popupWindowView.findViewById(R.id.time_picker);
        cal = Calendar.getInstance();

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Redit_event.this.year = year;
                Redit_event.this.month = monthOfYear;
                Redit_event.this.day = dayOfMonth;
                sstime = getdate();
                showdate(sstime);
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Redit_event.this.hour = hourOfDay;
                Redit_event.this.minute = minute;
                sstime = getdate();
                showdate(sstime);
            }
        });
//  popupwindow 摆放位置
        popupWindow.showAtLocation(btnsure, Gravity.CENTER, 0, 50);
    }

    public StringBuilder getdate() {
        StringBuilder strtime = new StringBuilder().append(year).append("-")

                .append(format(month + 1)).append("-")

                .append(format(day)).append(" ")

                .append(format(hour)).append(":")

                .append(format(minute));
        return strtime;
    }

    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1) s = "0" + s;
        return s;
    }

    public void showdate(StringBuilder strtime) {
        show.setText(strtime);
    }

    private class EditTextOnclickListener implements View.OnClickListener {
        public void onClick(View vid) {
            switch (vid.getId()) {
                case R.id.img_event_cancle:

                case R.id.start_event_redit:
                    i = 0;
                    OpenView();
                    break;
                case R.id.end_event_redit:
                    i = 1;
                    OpenView();
                    break;
                case R.id.select_date_confirm:
                    mythread = new MyThread();
                    thread = new Thread(mythread);
                    thread.start();
                    popupWindow.dismiss();
                    break;
                case R.id.select_date_cancle:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    }

    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    class MyHandler extends Handler {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (i == 0) {
                    start_time.setText(msg.getData().getString("time"));
                } else {
                    end_time.setText(msg.getData().getString("time"));
                }
            }
        }
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("time", String.valueOf(sstime));
                    message.setData(bundle);//bundle传值，耗时，效率低
                    handler.sendMessage(message);//发送message信息
                    message.what = 1;//标志是哪个线程传数据
                    //message有四个传值方法，
                    //两个传int整型数据的方法message.arg1，message.arg2
                    //一个传对象数据的方法message.obj
                    //一个bandle传值方法
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private class UpdateToDatabase implements View.OnClickListener {
        public void onClick(View v) {
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd ");
            String title = ((EditText) findViewById(R.id.Event_title_redit)).getText().toString();
            String location = ((EditText) findViewById(R.id.Event_location_redit)).getText().toString();
            String timestart = ((EditText) findViewById(R.id.start_event_redit)).getText().toString();
            String timeend = ((EditText) findViewById(R.id.end_event_redit)).getText().toString();

            Date date = format.parse(timestart, pos);
            String dat = forma.format(date);

            updatedata(db, dat, title, location, timestart, timeend,id);
            Toast.makeText(Redit_event.this, "一项事务添加成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Redit_event.this
                    , event_schedules_view.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除上一个activity，当前activity
            //启动intent对应的Activity
            startActivity(intent);
            //结束当前Activity
            finish();
        }
    }

    public void updatedata(SQLiteDatabase db, String date, String title, String location, String timestart, String timeend, Long id) {
        db.execSQL("update schedule set date=?,title=?,location=?,startime=?,endtime=? where _id=?", new String[]{date, title, location, timestart, timeend, String.valueOf(id)});
    }
    public Long queryid(SQLiteDatabase db, String title, String location) {
        Cursor cursor = db.rawQuery("select * from schedule where title= ? and location= ? ", new String[]{title, location});
        if (cursor.moveToNext()) {
            Toast.makeText(Redit_event.this, String.valueOf(cursor.getLong(0)), Toast.LENGTH_SHORT).show();
            return cursor.getLong(0);
        }
        else{
            return null;
        }
    }
}

/* 字符转date对象
simpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		   String time1 =  "2014-03-02 10:20:20";
		   String time2 =  "2014-03-10 08:20:20";
		   ParsePosition pos = new ParsePosition(0);
		   Date da = df.parse(time1,pos);
		   System.out.println(da); */
/*设置时间获取date对象
Calendar cal1 = Calendar.getInstance();
		  Date date = new Date(14，3，10);
		  cal1.setTime(date);

		  System.out.println(cal1.getTime().getClass());*/


