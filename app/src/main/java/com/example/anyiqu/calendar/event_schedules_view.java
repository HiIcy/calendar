package com.example.anyiqu.calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class event_schedules_view extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
//    private LoaderListView curListView;
    private static final int NOSELECT_STATE = -1;// 表示未选中任何CheckBox
    private Button bt_cancel, bt_delete;
    private TextView tv_sum;
    private LinearLayout linearLayout;
    private List<Event> list_delete = new ArrayList<Event>();// 需要删除的数据
    List<Integer> index = new ArrayList<Integer>();//需要删除位置的数据
    ListView listView;  //视图而已
    OrderDBHelper dbHelper;
    SQLiteDatabase db;
    List<Event> eventList;  //数据添加
    private boolean isMulChoice = false; //是否多选
    private MyAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedules_lists);

        //数据库初始化
        dbHelper = new OrderDBHelper(event_schedules_view.this);
        db = dbHelper.getReadableDatabase();

        //数据库数据直接提取显示
        eventList = new ArrayList<Event>();
        eventList = Query();

        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tv_sum = (TextView) findViewById(R.id.tv_sum);


        // listview 适配器 展示
        listView = (ListView) findViewById(R.id.schedules_list);
        adapter = new MyAdapter(eventList, event_schedules_view.this, NOSELECT_STATE);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        //searchview 过滤作用
        SearchView sv = (SearchView) findViewById(R.id.search_view);
        //为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(event_schedules_view.this);
        //设置该SearchView显示搜索按钮
        sv.setSubmitButtonEnabled(true);
        //设置该SearchView内默认显示的提示文本
        sv.setQueryHint("查找");
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bt_cancel:
                isMulChoice = false;
                index.clear();
//                list_delete.clear();
                adapter = new MyAdapter(eventList,this, NOSELECT_STATE);
                listView.setAdapter(adapter);
                linearLayout.setVisibility(View.GONE);
                break;
            case R.id.bt_delete:
                isMulChoice =false;
                for(int i=0;i<index.size();i++){
                    deleteData(eventList.get(index.get(i)));
                    eventList.remove(eventList.get(index.get(i)));
                }
                adapter = new MyAdapter(eventList,this,NOSELECT_STATE);
                listView.setAdapter(adapter);
                index.clear();
                adapter.notifyDataSetChanged();
                linearLayout.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }
    private void deleteData(Event event) {
        String title = event.getTitle();
        String location = event.getLocation();
        String SQL = "DELETE FROM SCHEDULE WHERE TITLE=? and LOCATION=?";
        db.execSQL(SQL,new String[]{title, location});
    }
    @Override//过滤实现 接口方法
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "你的选择: " + query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    final class ViewHolder {
        public TextView Date;
        public ImageView img_point;
        public EditText title;
        public EditText location;
        public EditText startime;
        public EditText endtime;
        CheckBox checkBox;
    }

    // 适配器重写
    public class MyAdapter extends BaseAdapter implements Filterable {
        private PersonFilter filter;
        private List<Event> list;//记录数据的
        private LayoutInflater mInflater;
        public HashMap<Integer, Integer> visiblecheck ;//用来记录是否显示checkBox
        public  HashMap<Integer, Boolean> ischeck;//选中与否

        public MyAdapter(List<Event> list, Context context, int position) {
            this.list = list;
            this.mInflater = LayoutInflater.from(context);
            // 对复选框的基本初始化
            visiblecheck = new HashMap<Integer, Integer> ();
            ischeck    = new HashMap<Integer, Boolean>();
            if(isMulChoice){//多选模式下
                for(int i=0;i<this.list.size();i++){
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.VISIBLE);
                }
            }else{
                for(int i=0;i<this.list.size();i++)
                {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.INVISIBLE);
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Event event = list.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.schedules_content, null);
                holder.Date = (TextView) convertView.findViewById(R.id.event_date);
                holder.img_point = (ImageView) convertView.findViewById(R.id.img_point);
                holder.title = (EditText) convertView.findViewById(R.id.schedules_event_title);
                holder.location = (EditText) convertView.findViewById(R.id.schedules_event_location);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.Date.setText(event.Date);
            holder.title.setText(event.Title);
            holder.location.setText(event.Location);
            holder.checkBox.setChecked(ischeck.get(position));
            holder.checkBox.setVisibility(visiblecheck.get(position));

            //基于每个item设置单击 长击 监听
            convertView.setOnLongClickListener(new onMyLongClickListener(position, list));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //处于多选模式
                    if (isMulChoice) {
                        // 已选中的操作 勾掉

                        if (holder.checkBox.isChecked()) {
                            holder.checkBox.setChecked(false);
//                            list_delete.remove(list.get(position));
                            index.remove(position);
                        }
                        //未选中的操作
                        else {
                            holder.checkBox.setChecked(true);
//                            list_delete.add(list.get(position));
                            index.add(position);
                        }
                        tv_sum.setText("共选择了" + +index.size() + "项");
                    }
                    // 非多选模式
                    else{
                        //针对列表项 点击事件 进入detail页面
                        Bundle b = new Bundle();
                        b.putSerializable("event", eventList.get(position));
                        Intent intent = new Intent();
                        intent.setClass(event_schedules_view.this, event_detail.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }
        class onMyLongClickListener implements View.OnLongClickListener{
            private int position;
            private List<Event> list;
            // 获取数据，与长按Item的position
            public onMyLongClickListener(int position, List<Event> evlist) {
                this.position = position;
                this.list = evlist;
            }
            public boolean onLongClick(View v) {
                isMulChoice = true;
//                list_delete.clear();
                index.clear();
                // 添加长按Item到删除数据list中
//                index.add(position);
//                list_delete.add(list.get(position));
                linearLayout.setVisibility(View.VISIBLE);
                tv_sum.setText("共选择了" + index.size()+ "项");
                for (int i = 0; i < list.size(); i++) {
                    adapter.visiblecheck.put(i, CheckBox.VISIBLE);
                }
                // 根据position，设置listview中对应的CheckBox为选中状态
                adapter = new MyAdapter(list, event_schedules_view.this, position);
                listView.setAdapter(adapter);
                return true;
            }
        }

        @Override //过滤器重写
        public Filter getFilter() {
            if (filter == null) {
                filter = new PersonFilter(list);
            }
            return filter;
        }
//过滤器
        private class PersonFilter extends Filter {
            private List<Event> original;

            public PersonFilter(List<Event> list) {
                this.original = list;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = original;
                    results.count = original.size();
                } else {
                    List<Event> mList = new ArrayList<Event>();
                    for (Event p : original) {
                        if (p.Title.toUpperCase().startsWith(constraint.toString().toUpperCase())
                                || p.Location.toUpperCase().startsWith(constraint.toString().toUpperCase())) {
                            mList.add(p);
                        }
                    }
                    results.values = mList;
                    results.count = mList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Event>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    //查询数据库 并添加到listview 显示
    public List<Event> Query() {
//        desc 从大到小 倒序  asc 顺序
        Cursor cursor = db.rawQuery("select * from schedule order by 2 asc ", null);
//        Cursor cursor = db.query("schedule", null, null, null, null, null, null,null);
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String date = cursor.getString(1);
            String title = cursor.getString(2);
            String location = cursor.getString(3);
            String stime = cursor.getString(4);
            String etime = cursor.getString(5);
            long rmtime = cursor.getLong(6);
            Event person = new Event(date, title, location, stime, etime,rmtime);
            eventList.add(person);
        }
        return eventList;
    }
    public void onStart(Bundle savedInstanceState){
        super.onStart();
        onCreate(savedInstanceState);
    }
    //回调activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            finish();
        }
    }
}