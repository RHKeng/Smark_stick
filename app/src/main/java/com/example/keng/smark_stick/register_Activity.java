package com.example.keng.smark_stick;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;


public class register_Activity extends Activity implements View.OnClickListener {
    //声明所有控件
    private Button rbt_username_clear;
    private Button rbt_pwd_clear;
    private Button rbt_pwd_clear1;
    private Button rbt_pwd_eye;
    private Button rbt_pwd_eye1;
    private Button mRegister;
    private EditText ret_name, ret_pass,ret_pass1;
    private TextWatcher username_watcher;
    private TextWatcher password_watcher;
    private TextWatcher password_watcher1;
    //用于存储密码
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //用于显示短信内容
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        //获取toolbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.rtoolbar);
        setActionBar(toolbar);
        //获取所有空间对象以及设置监听事件
        ret_name = (EditText) findViewById(R.id.rusername);//登录名
        ret_pass = (EditText) findViewById(R.id.rpassword);//密码
        ret_pass1 = (EditText) findViewById(R.id.rpassword1);//密码
        rbt_username_clear = (Button)findViewById(R.id.rbt_username_clear);//清除登录名
        rbt_pwd_clear = (Button)findViewById(R.id.rbt_pwd_clear);//清除密码
        rbt_pwd_clear1 = (Button)findViewById(R.id.rbt_pwd_clear1);//清除密码1
        rbt_pwd_eye = (Button)findViewById(R.id.rbt_pwd_eye);//显示密码
        rbt_pwd_eye1 = (Button)findViewById(R.id.rbt_pwd_eye1);//显示密码1
        mRegister    = (Button) findViewById(R.id.mregister);//注册
        //注册事件
        rbt_username_clear.setOnClickListener(this);
        rbt_pwd_clear.setOnClickListener(this);
        rbt_pwd_eye.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        rbt_pwd_clear1.setOnClickListener(this);
        rbt_pwd_eye1.setOnClickListener(this);
        initWatcher();
        //注册监视器
        ret_name.addTextChangedListener(username_watcher);
        ret_pass.addTextChangedListener(password_watcher);
        ret_pass1.addTextChangedListener(password_watcher1);
        //利用contentprovider直接读取系统中存储的信息
        //获取处理的对象
        //利用sharepereference存储数据
        sharedPreferences=this.getSharedPreferences("password", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    private void initWatcher() {
        username_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            public void afterTextChanged(Editable s) {
                ret_pass.setText("");
                ret_pass1.setText("");
                if(s.toString().length()>0){
                    rbt_username_clear.setVisibility(View.VISIBLE);
                }else{
                    rbt_username_clear.setVisibility(View.INVISIBLE);
                }
            }
        };

        password_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    rbt_pwd_clear.setVisibility(View.VISIBLE);
                }else{
                    rbt_pwd_clear.setVisibility(View.INVISIBLE);
                }
            }
        };
        password_watcher1 = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    rbt_pwd_clear1.setVisibility(View.VISIBLE);
                }else{
                    rbt_pwd_clear1.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.titlebar_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //实现单击事件,将可能用到的按钮事件都实现
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mregister:  //注册成功
                Log.v("msg","注册中！");
                String temp_name=sharedPreferences.getString(ret_name.getText().toString(),null);
                String name=ret_name.getText().toString();
                String temp_pw=ret_pass.getText().toString();
                if(temp_name==null){
                    if(ret_pass.getText().toString().equals(ret_pass1.getText().toString())){
                    Toast.makeText(register_Activity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        editor.putString(name,temp_pw);
                        editor.commit();
                        Intent intent=new Intent(register_Activity.this,MainActivity2.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        }
                    else{
                        Toast.makeText(register_Activity.this, "密码不一致！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(register_Activity.this, "注册失败，用户名已存在", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rbt_username_clear:
                ret_name.setText("");
                ret_pass.setText("");
                ret_pass1.setText("");
                break;
            case R.id.rbt_pwd_clear:
                ret_pass.setText("");
                ret_pass1.setText("");
                break;
            case R.id.rbt_pwd_clear1:
                ret_pass1.setText("");
                break;
            case R.id.rbt_pwd_eye:
                if(ret_pass.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    rbt_pwd_eye.setBackgroundResource(R.drawable.see);
                    ret_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }else{
                    rbt_pwd_eye.setBackgroundResource(R.drawable.see);
                    ret_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                ret_pass.setSelection(ret_pass.getText().toString().length());
                break;
            case R.id.rbt_pwd_eye1:
                if(ret_pass1.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    rbt_pwd_eye1.setBackgroundResource(R.drawable.see);
                    ret_pass1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }else{
                    rbt_pwd_eye1.setBackgroundResource(R.drawable.see);
                    ret_pass1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                ret_pass1.setSelection(ret_pass.getText().toString().length());
                break;
        }
    }
}
