package com.example.anyiqu.calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class add_new_event extends AppCompatActivity {
    SQLiteDatabase db;
    Calendar cal;
    OrderDBHelper dbHelper;
    private Long whichremind = null;
    private String remindText = null;
    private ImageButton img_event_confirm, img_event_cancle;
    private View popupWindowView, popupsusWindowView;
    private PopupWindow popupWindow,popupsusWindow;
    private Button btnsure, btncancle;
    private EditText start_time,end_time;
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
    MyThread mythread;
    MyHandler handler;// 处理popwindow
    Thread thread;
    EditText remindtime;
    Button  cancle_remind;
    RadioGroup radiogroup;
    RadioButton radio1,radio2,radio3,radio4;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);
        TimeZone.setDefault(TimeZone.getDefault());//默认时区

        start_time = (EditText) findViewById(R.id.start_event_time);
        end_time = (EditText) findViewById(R.id.end_event_time);
        remindtime = (EditText) findViewById(R.id.Event_remind);
        remindtime.setOnClickListener(new EditTextOnclickListener());
        img_event_cancle = (ImageButton)findViewById(R.id.img_event_cancle);
        img_event_confirm = (ImageButton)findViewById(R.id.img_event_confirm);
        //数据库
        dbHelper  = new OrderDBHelper(add_new_event.this);
        db = dbHelper.getWritableDatabase(); //  Sqlitedatabase对象

        img_event_confirm.setOnClickListener(new SaveToDatabase());
        img_event_cancle.setOnClickListener(new EditTextOnclickListener());
        start_time.setOnClickListener(new EditTextOnclickListener());
        end_time.setOnClickListener(new EditTextOnclickListener());

        handler=new MyHandler();//消息处理
    }
//    public
    public void OpenView(){
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
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);

//    获取文本 展示时间 在popupwindow上
        show = (EditText)popupWindowView.findViewById(R.id.editText_date);
        datePicker = (DatePicker)popupWindowView.findViewById(R.id.date_picker);
        timePicker = (TimePicker)popupWindowView.findViewById(R.id.time_picker);
        cal = Calendar.getInstance();

        //初始时间
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);

        //日期改变事件
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                add_new_event.this.year = year;
                add_new_event.this.month= monthOfYear;
                add_new_event.this.day = dayOfMonth;
                sstime = getdate();
                showdate(sstime);
            }
        });
        //时间改变处理
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                add_new_event.this.hour = hourOfDay;
                add_new_event.this.minute = minute;
                sstime = getdate();
                showdate(sstime);
            }
        });
//  popupwindow 摆放位置
        popupWindow.showAtLocation(btnsure, Gravity.CENTER, 0, 50);
    }
    public void openView(){
//        对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入
//        对于一个已经载入的界面，就可以使用Activiyt.findViewById()方法来获得其中的界面元素
//        inflater 是用来找 res/layout下的 xml 布局文件，并且实例化
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        inflater 是用来找 res/layout下的 xml 布局文件，并且实例化
        popupsusWindowView = inflater.inflate(R.layout.add_remind, null);
        popupsusWindowView.setFocusable(true);
        popupsusWindowView.setFocusableInTouchMode(true);
        popupsusWindow = new PopupWindow(popupsusWindowView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupsusWindow.setOutsideTouchable(false);
        //        设置窗口动画
        popupsusWindow.setAnimationStyle(R.style.popupAnimation);
        cancle_remind = (Button) popupsusWindowView.findViewById(R.id.cancle_remind);
        radiogroup=(RadioGroup)popupsusWindowView.findViewById(R.id.radiogroup1);
        radio1=(RadioButton)popupsusWindowView.findViewById(R.id.radiobutton1);
        radio2=(RadioButton)popupsusWindowView.findViewById(R.id.radiobutton2);
        radio3=(RadioButton)popupsusWindowView.findViewById(R.id.radiobutton3);
        radio4=(RadioButton)popupsusWindowView.findViewById(R.id.radiobutton4);
        radiogroup.setOnCheckedChangeListener( new RadioGroupCheckListener());
        cancle_remind.setOnClickListener(new EditTextOnclickListener());
        //背景阴影设置
        popupsusWindow.setBackgroundDrawable(new ColorDrawable(0x90000000));
//        设置添加屏幕的背景透明度
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = 2f;
        getWindow().setAttributes(lp);
        popupsusWindow.showAtLocation(cancle_remind,Gravity.BOTTOM, 0, 0);
    }
    //格式化字符串
    public StringBuilder getdate(){
        StringBuilder strtime = new StringBuilder().append(year).append("-")

                .append(format(month + 1)).append("-")

                .append(format(day)).append(" ")

                .append(format(hour)).append(":")

                .append(format(minute));
        return strtime;
    }
    //格式-00
    private String format(int x)
    {
        String s=""+x;
        if(s.length()==1) s="0"+s;
        return s;
    }
    public void showdate(StringBuilder strtime){
            show.setText(strtime);
    }

    private class EditTextOnclickListener implements View.OnClickListener {
        public void onClick(View vid) {
            switch (vid.getId()) {
                case R.id.img_event_cancle:
                    Intent intent = new Intent(add_new_event.this
                            , MainActivity.class);
                    //启动intent对应的Activity
                    startActivity(intent);
                    //结束当前Activity
                    finish();
                    break;
                case R.id.start_event_time:
                    i = 0;
                    OpenView();
                    break;
                case R.id.Event_remind:
                    openView();
                    break;
                case R.id.cancle_remind:
                    OnBackPressed();
                    break;
                case R.id.end_event_time:
                    i = 1;
                    OpenView();
                    break;
                case R.id.select_date_confirm://确认
                    mythread = new MyThread();
                    thread=new Thread(mythread);
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
    //对于popwindow弹出处理
    public void onBackPressed(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }else{
            super.onBackPressed();
        }
    }
    public void OnBackPressed(){
        if(popupsusWindow!=null && popupsusWindow.isShowing()){
            popupsusWindow.dismiss();
        }else{
            super.onBackPressed();
        }
    }
    // 开启handler 处理pop window 与ui的传值
    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what==1) {
                if(i == 0) {
                    start_time.setText(msg.getData().getString("time"));
                }
                else{
                    end_time.setText(msg.getData().getString("time"));
                }
            }
        }
    }
    class MyThread implements Runnable
    {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true)
            {
                try {
                    Thread.sleep(1000);
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("time", String.valueOf(sstime));
                    message.setData(bundle);//bundle传值，耗时，效率低
                    handler.sendMessage(message);//发送message信息
                    message.what=1;//标志是哪个线程传数据
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
// 内部类 选择提醒时间
    private class  RadioGroupCheckListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            Date date = null;
            Calendar cale = Calendar.getInstance();
            if (checkedId == radio1.getId()) {
                remindText = radio1.getText().toString();
                date = changeTime(start_time.getText().toString());
                cale.setTime(date);
                cale.add(Calendar.HOUR, -1);
                whichremind = cale.getTimeInMillis();
            } else if (checkedId == radio2.getId()) {
                remindText = radio2.getText().toString();
                date = changeTime(start_time.getText().toString());
                cale.setTime(date);
                whichremind = cale.getTimeInMillis();
            } else if (checkedId == radio3.getId()) {
                remindText = radio3.getText().toString();
                date = changeTime(start_time.getText().toString());
                cale.setTime(date);
                cale.add(Calendar.MINUTE, -5);
                whichremind = cale.getTimeInMillis();
            } else if (checkedId == radio4.getId()) {
                remindText = radio4.getText().toString();
                date = changeTime(start_time.getText().toString());
                cale.setTime(date);
                cale.add(Calendar.MINUTE, -10);//提前多少时间提醒
                whichremind = cale.getTimeInMillis();
            }
            remindtime.setText(remindText);
            popupsusWindow.dismiss();
        }
    }
    //数据库存入值
    private class SaveToDatabase implements View.OnClickListener{
        public void onClick(View v){
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd ");
            forma.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String title = ((EditText) findViewById(R.id.Event_title)).getText().toString();
            String location = ((EditText) findViewById(R.id.Event_location)).getText().toString();
            String timestart = ((EditText) findViewById(R.id.start_event_time)).getText().toString();
            String timeend = ((EditText) findViewById(R.id.end_event_time)).getText().toString();

            Date date = format.parse(timestart,pos);
            String dat = forma.format(date);

            insertdata(db,dat,title, location, timestart, timeend,whichremind);
            Toast.makeText(add_new_event.this,"一项事务添加成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(add_new_event.this
                    , MainActivity.class);
            //启动intent对应的Activity
            startActivity(intent);
            //结束当前Activity
            finish();
        }
}
    public void insertdata(SQLiteDatabase db,String date,String title,String location,String timestart,String timeend, Long remindtime){
        db.execSQL("insert into schedule values(null,?,?,?,?,?,?)",new Object[] {date, title, location,timestart, timeend,remindtime});
    }
    public Date changeTime(String start_time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        ParsePosition pos = new ParsePosition(0);
        //解析字符串
        Date date = format.parse(start_time,pos);
        return date;
    }
}

/* 字符转date对象
simpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String time1 =  "2014-03-02 10:20:20";
		   String time2 =  "2014-03-10 08:20:20";
		   ParsePosition pos = new ParsePosition(0);
		   Date da = df.parse(time1,pos);
		   System.out.println(da); */
/*设置时间获取date对象
Calendar cal1 = Calendar.getInstance();
		  Date date = new Date(14，3，10);
		  cal1.setTime(date);
		  System.out.println(cal1.getTime().getClass());
		  //时间戳
		    System.out.println(cal1.getTimeInMillis());
		  System.out.println(date.getTime());
		  //时间戳转为字符
		  Long time=cal1.getTimeInMillis();
		    String d = format.format(time);
       Date dat=format.parse(d);
       //时区
       sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	}*/

