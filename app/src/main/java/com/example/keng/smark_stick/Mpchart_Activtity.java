package com.example.keng.smark_stick;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Mpchart_Activtity extends AppCompatActivity {
    private LineChart mLineChart;
    private Button readSma;
    private Button readsms_button;
    private BarChart mChart;
    //声明一个数据库用于存储信息
    private SQLiteDatabase sms_db;
    //用于显示短信内容
    private Uri SMS_INBOX=Uri.parse("content://sms/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollview_chart);
        readSma= (Button) findViewById(R.id.button);
        readsms_button=(Button)findViewById(R.id.readsms_button);
        mLineChart=(LineChart) findViewById(R.id.chart);
        mLineChart.setDescription("显示所有天数的运动步数");
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("");
        LineData data = getData(36, 100);
        mLineChart.setData(data);
        Barchart_setting();
        //setData(15);
        //设置数据
        recieve_sms();
        //按钮添加监听器以读取短信
        readSma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recieve_sms();
                Log.i("MainActivity","读取短信");
            }
        });
        readsms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readsmsfromdb();
                Log.i("MainActivity","从数据库中取数据设置表格数据");
            }
        });
    }
    //生成折线图数据集的函数，返回数据集合
    LineData getData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xVals.add("12/"+i);
        }
        // y轴的数据
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            yVals.add(new Entry(val, i));
        }
        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet set1 = new LineDataSet(yVals, "老人每天运动数据");
        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        set1.setLineWidth(2.75f); // 线宽
        set1.setColor(Color.YELLOW);// 显示颜色
        set1.setCircleColor(Color.RED);// 圆形的颜色
        set1.setHighLightColor(Color.WHITE); // 高亮的线的颜色
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        // create a data object with the datasets
        LineData data =new LineData(xVals,dataSets);
        return data;
    }
    //为柱状图设置数据的函数
    private void setData(int count) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();//y轴坐标
        ArrayList<String> xVals = new ArrayList<String>();//x轴坐标
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * count) + 15;
            yVals.add(new BarEntry((int) val, i));
            xVals.add((int) val + "");
        }

        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setDrawValues(false);
        BarData data = new BarData(xVals, set);
        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(800);
    }
    //设置柱状图的显示形式的函数
    private void Barchart_setting(){
        mChart.setDescription("老人运动每日运动数据表");
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴显示在底部
        xAxis.setLabelsToSkip(0);
        xAxis.setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getLegend().setEnabled(false);
    }
    //创建、操作数据库的函数
    public void createdb(){
        //下面进行对数据库的操作,创建或者打开数据库
        sms_db= SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my_sms",null);
        //创建一个表
        String sql="CREATE TABLE test1 if not exist(_id integer primary key auto_increment,body text NOT NULL)";
        String delTable="drop table test1";
        sms_db.execSQL(sql);
        //sms_db.execSQL(delTable);
        ContentValues sms1=new ContentValues();
        sms1.put("body","Hello world");
        sms_db.insert("test1",null,sms1);
    }
    //用于读取系统内部已经存储好的短信，并且将它存到数据库里面
    public void recieve_sms(){
        ContentResolver cr=getContentResolver();
        //查询的字段，这里只是查询两个
        String[] projection = new String[] {"address", "body","date"};
        //查询的条件
        String where = "address=18819477449";
        //查询的结果放在了一个Cusor中，按照日期升序
        Cursor cusor = cr.query(SMS_INBOX, projection,where, null,"date asc");
        //获取对应字段的标签值
        int phoneNumberColumn = cusor.getColumnIndex("address");
        int smsbodyColumn = cusor.getColumnIndex("body");
        int smsDatecolumn=cusor.getColumnIndex("date");
        //下面进行对数据库的操作,创建或者打开数据库
        sms_db= SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my_sms",null);
        //先删除原来的表
        String delTable="drop table test1";
        sms_db.execSQL(delTable);
        //创建一个表
        String sql="CREATE TABLE IF NOT EXISTS test1 (_id integer primary key ,body integer NOT NULL,date integer NOT NULL)";
        //String delTable="drop table test1";
        sms_db.execSQL(sql);
        String[] projection_db = new String[] {"_id", "body","date"};
        Cursor cursor_db=sms_db.query("test1",projection_db,null,null,null,null,null);
        if(cursor_db!=null) {
            //移动到最后一个
            int min_id=0;
            long date=0;
            int id_Column = cursor_db.getColumnIndex("_id");
            int date_db = cursor_db.getColumnIndex("date");
            if(cursor_db.moveToLast()){
                min_id = cursor_db.getInt(id_Column);
                date = cursor_db.getLong(date_db);}
            //sms_db.execSQL(delTable);
            //根据标签值一个个查询,只有日期大于当前最大日期的才能添加进表
            if (cusor != null) {
                while (cusor.moveToNext()) {
                   if (cusor.getLong(smsbodyColumn) > date) {
                        ContentValues sms1 = new ContentValues();
                        sms1.put("date",cusor.getLong(smsDatecolumn));
                        sms1.put("body", cusor.getInt(smsbodyColumn));
                        sms1.put("_id", min_id++);
                        sms_db.insert("test1", null, sms1);
                        Log.i("MainActivity", "日期大于的数据存入");
                        Log.i("MainActivity", cusor.getString(phoneNumberColumn));
                        Log.i("MainActivity", cusor.getString(smsbodyColumn));
                        Log.i("MainActivity", String.valueOf(cusor.getLong(smsDatecolumn)));
                   }
                }
                cusor.close();
                sms_db.close();
            }
        }else {
            if (cusor != null) {
                int i=0;
                while (cusor.moveToNext()) {
                        ContentValues sms1 = new ContentValues();
                        sms1.put("body", cusor.getInt(smsbodyColumn));
                        sms1.put("_id", i++);
                        sms_db.insert("test1", null, sms1);
                         Log.i("MainActivity", "全部数据存入");
                        Log.i("MainActivity", cusor.getString(phoneNumberColumn));
                        Log.i("MainActivity", cusor.getString(smsbodyColumn));
                        Log.i("MainActivity", String.valueOf(cusor.getLong(smsDatecolumn)));
                    }
                }
                cusor.close();
                sms_db.close();
        }
    }
    //从数据库中读取短信内容并且为柱状图设置数据
    private void readsmsfromdb(){
        //设置数据，两个数组分别存储数据
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();//y轴坐标
        ArrayList<String> xVals = new ArrayList<String>();//x轴坐标
        sms_db= SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my_sms",null);
        String[] projection = new String[] {"_id", "body","date"};
        Cursor cursor=sms_db.query("test1",projection,null,null,null,null,null);
        int id_Column = cursor.getColumnIndex("_id");
        int smsbodyColumn = cursor.getColumnIndex("body");
        Log.i("MainActivity", "查询数据");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int val =cursor.getInt(smsbodyColumn);
                yVals.add(new BarEntry(val, cursor.getInt(id_Column)));
                xVals.add( cursor.getInt(id_Column) + "");
                Log.i("MainActivity", "查询到数据");
                Log.i("MainActivity",cursor.getString(id_Column));
                Log.i("MainActivity",cursor.getString(smsbodyColumn));
            }
            cursor.close();
            sms_db.close();
        }
        //设置数据
        BarDataSet set = new BarDataSet(yVals, "Data Set");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setDrawValues(false);
        BarData data = new BarData(xVals, set);
        mChart.setData(data);
        mChart.invalidate();
        mChart.animateY(800);
    }
}

