package com.example.keng.smark_stick;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity_mainlayout extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //声明一个数据库用于存储信息
    private SQLiteDatabase sms_db;
    //用于显示短信内容
    private Uri SMS_INBOX=Uri.parse("content://sms/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "暂时未开发该功能，敬请期待", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //更新数据库
        //recieve_sms();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map) {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.sport_dt) {
            Intent intent=new Intent(this,Mpchart_Activtity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.stick_setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        // String delTable="drop table test1";
        // sms_db.execSQL(delTable);
        //创建两个表
        String sql="CREATE TABLE IF NOT EXISTS test1 (_id integer primary key ,body TEXT NOT NULL,date integer NOT NULL)";
        String sql2="CREATE TABLE IF NOT EXISTS test2 (_id integer primary key ,body TEXT NOT NULL,date integer NOT NULL)";
        //String delTable="drop table test1";
        sms_db.execSQL(sql);
        sms_db.execSQL(sql2);
        //更新第一个表，存储日常数据
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
                    //判断是哪一种类型的短信
                    String temp=cusor.getString(smsbodyColumn);
                    char[] chars=temp.toCharArray();
                    int num=0;
                    for(int i=0;i<chars.length;i++){
                        if(chars[i]=='-'){
                            num++;
                        }
                    }

                    if (cusor.getLong(smsDatecolumn) > date &&num==2) {
                        ContentValues sms1 = new ContentValues();
                        sms1.put("date",cusor.getLong(smsDatecolumn));
                        sms1.put("body", cusor.getString(smsbodyColumn));
                        sms1.put("_id", ++min_id);
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
                    //判断是哪一种类型的短信
                    String temp=cusor.getString(smsbodyColumn);
                    char[] chars=temp.toCharArray();
                    int num=0;
                    for(int j=0;i<chars.length;j++){
                        if(chars[j]=='-'){
                            num++;
                        }
                    }
                    if(num==2){
                    ContentValues sms1 = new ContentValues();
                    sms1.put("body", cusor.getString(smsbodyColumn));
                    sms1.put("_id", i++);
                    sms_db.insert("test1", null, sms1);
                    Log.i("MainActivity", "全部数据存入");
                    Log.i("MainActivity", cusor.getString(phoneNumberColumn));
                    Log.i("MainActivity", cusor.getString(smsbodyColumn));
                    Log.i("MainActivity", String.valueOf(cusor.getLong(smsDatecolumn)));}
                }
            }
            cusor.close();
            sms_db.close();
        }
    }
}
