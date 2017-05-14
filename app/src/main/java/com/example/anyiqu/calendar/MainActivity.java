package com.example.anyiqu.calendar;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.anyiqu.calendar.module.OneDayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.anyiqu.calendar.R.id.calendarView;

public class MainActivity extends AppCompatActivity implements OnDateSelectedListener {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    ImageButton add_new_event, event_schedules_view, setting;
    ImageButton view_week, view_month;
    private String []items= new String[]{"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};
    private final String []ites= new String[]{"从相册读取"};
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static int FIRST_OF_DAY = Calendar.MONDAY;
    private LinearLayout main_layout;
    private static final String IMAGE_TYPE="image/*";
    private static String TEMP_IMAGE_PATH;
     /* 头像名称 */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加载日历组件，并添加附加功能
        final MaterialCalendarView widget = (MaterialCalendarView) findViewById(calendarView);
        widget.addDecorators(
                new OneDayDecorator()
        );
        main_layout =(LinearLayout)findViewById(R.id.Main_layout);

        // 初始化
        widget.setOnDateChangedListener(this);
        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());
        widget.state().edit().
                setFirstDayOfWeek(FIRST_OF_DAY).commit();

        add_new_event = (ImageButton) findViewById(R.id.add_new_event);
        event_schedules_view = (ImageButton) findViewById(R.id.event_schedules_view);
        setting = (ImageButton) findViewById(R.id.Setting);
        view_month = (ImageButton) findViewById(R.id.view_month);
        view_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widget.state().edit()
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit();
            }
        });
        view_week = (ImageButton) findViewById(R.id.view_week);
        view_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    widget.state().edit()
                            .setCalendarDisplayMode(CalendarMode.WEEKS)
                            .commit();

            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取popupmenu对象
                PopupMenu popup = new PopupMenu(MainActivity.this, setting);
                //实例化
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.special_topic:
                                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("周开始").setIcon(R.drawable.img_hint_today)
                                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
/*一周开始日的设置*/
                                                switch (which) {
                                                    case 0:
                                                        widget.state().
                                                                edit().
                                                                setFirstDayOfWeek(Calendar.SUNDAY).commit();
                                                        FIRST_OF_DAY = Calendar.SUNDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    case 1:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.MONDAY).commit();
                                                        FIRST_OF_DAY = Calendar.MONDAY;
                                                                dialog.dismiss();
                                                        break;
                                                    case 2:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.TUESDAY).commit();
                                                        FIRST_OF_DAY = Calendar.TUESDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    case 3:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.WEDNESDAY).commit();
                                                        FIRST_OF_DAY = Calendar.WEDNESDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    case 4:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.THURSDAY).commit();
                                                        FIRST_OF_DAY = Calendar.THURSDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    case 5:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.FRIDAY).commit();
                                                        FIRST_OF_DAY = Calendar.FRIDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    case 6:
                                                        widget.state().edit().
                                                                setFirstDayOfWeek(Calendar.SATURDAY).commit();
                                                        FIRST_OF_DAY = Calendar.SATURDAY;
                                                        dialog.dismiss();
                                                        break;
                                                    default:
                                                        dialog.dismiss();
                                                        break;
                                                }
                                            }
                                        }).create();
                                dialog.show();
                                return true;
                            case R.id.elite:
                                AlertDialog dia_log = new AlertDialog.Builder(MainActivity.this).setTitle("选择图片").
                                        setItems(ites, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        gallery();
                                                        break;
                                                    default:
                                                    break;

                                                }
                                            }
                                        }).create();
                                dia_log.show();
//                                Intent intent = new Intent(
//                                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        event_schedules_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, event_schedules_view.class);
                startActivity(intent);
            }
        });
        add_new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, add_new_event.class);
                startActivity(intent);
            }
        });
    }
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK,null);//Intent.ACTION_GET_CONTENT
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_TYPE);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (PHOTO_REQUEST_GALLERY == requestCode && RESULT_OK == resultCode && null != data) {
             Uri selectImageUri = data.getData();
             String[] filePathColumn = new String[]{MediaStore.Images.Media.DATA};//要查询的列
             Cursor cursor = getContentResolver().query(selectImageUri, filePathColumn, null, null, null);
             String picturePath = null;
             while (cursor.moveToNext()) {
                 picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));//所选择的图片路径
             }
             cursor.close();
             Drawable d=Drawable.createFromPath(picturePath);
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                 main_layout.setBackground(d);
             }
         }
    }
//    选定日期的
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int dayOfWeek = 0;
        int dayofMonth = 0;
        if (selected && date != null) {
            dayOfWeek = date.getCalendar().get(Calendar.DAY_OF_WEEK);
            dayofMonth = date.getCalendar().get(Calendar.DAY_OF_MONTH);
        }
        //打印日志 实验用
        String TAG = "DATE";
        Log.i(TAG, "date = " + date);
        Log.i(TAG, "dayOfWeek = " + dayOfWeek);
        Log.i(TAG, "dayofMonth = " + dayofMonth);
    }
}
