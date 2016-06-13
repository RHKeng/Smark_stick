package com.example.keng.smark_stick;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


public class MainActivity2 extends Activity implements View.OnClickListener {
    //声明所有控件
    private Button bt_username_clear;
    private Button bt_pwd_clear;
    private Button bt_pwd_eye;
    private Button mLoginButton,mLoginError,mRegister;
    private EditText et_name, et_pass;
    private TextView sms_body;
    private TextWatcher username_watcher;
    private TextWatcher password_watcher;
    private EditText name;//注册时用户名
    private EditText pw;//注册时密码
    //用于存储密码
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //这里的只能接收到新发过来的短信
    private BroadcastReceiver sms_Receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           //获取数据
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if (null != bundle) {
                Object[] smsObj = (Object[]) bundle.get("pdus");
                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    System.out.println("number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis());
                    //在这里写自己的逻辑
                    if (msg.getOriginatingAddress().equals("+8618819477449")) {
                        //TODO
                       sms_body.setText(msg.getDisplayMessageBody());
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //获取toolbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        //获取所有空间对象以及设置监听事件
        et_name = (EditText) findViewById(R.id.username);//登录名
        et_pass = (EditText) findViewById(R.id.password);//密码
        bt_username_clear = (Button)findViewById(R.id.bt_username_clear);//清除登录名
        bt_pwd_clear = (Button)findViewById(R.id.bt_pwd_clear);//清除密码
        bt_pwd_eye = (Button)findViewById(R.id.bt_pwd_eye);//显示密码
        mLoginButton = (Button) findViewById(R.id.login);//登录
        mLoginError  = (Button) findViewById(R.id.login_error);//忘记密码
        mRegister    = (Button) findViewById(R.id.register);//注册
        name= (EditText) findViewById(R.id.dl_name);//注册用户名
        pw= (EditText) findViewById(R.id.dl_pw);//注册密码
        //注册事件
        bt_username_clear.setOnClickListener(this);
        bt_pwd_clear.setOnClickListener(this);
        bt_pwd_eye.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginError.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        initWatcher();
        //注册监视器
        et_name.addTextChangedListener(username_watcher);
        et_pass.addTextChangedListener(password_watcher);
        //ONLYTEST     = (Button) findViewById(R.id.registfer);
        //ONLYTEST.setOnClickListener(this);
        //ONLYTEST.setOnLongClickListener((OnLongClickListener) this);

        sms_body=(TextView)findViewById(R.id.sms_body);
        //动态注册广播
        IntentFilter intent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(sms_Receiver, intent);
        //利用contentprovider直接读取系统中存储的信息
        //获取处理的对象
        //利用sharepereference存储数据
        sharedPreferences=this.getSharedPreferences("password",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        //预先存储一组用户，用户名123，密码aaaa
        editor.putString("123","aaaa");
        editor.commit();
    }

    private void initWatcher() {
        username_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            public void afterTextChanged(Editable s) {
                et_pass.setText("");
                if(s.toString().length()>0){
                    bt_username_clear.setVisibility(View.VISIBLE);
                }else{
                    bt_username_clear.setVisibility(View.INVISIBLE);
                }
            }
        };

        password_watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                }else{
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
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

//实现单击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:  //登陆
             login();
                Log.v("msg","登录！");
                break;
            case R.id.login_error: //无法登陆(忘记密码了吧)
                Log.v("msg","忘记密码！");
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_layout, (ViewGroup) findViewById(R.id.dialog));
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setTitle("用户注册").setView(layout);
                //确定按钮点击事件
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        editor.putString(name.getText().toString(),pw.getText().toString());
                        editor.commit();
                        Toast.makeText(MainActivity2.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity2.this, "已取消注册 " , Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                //确定按钮点击事件
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        editor.putString(name.getText().toString(),pw.getText().toString());
                        editor.commit();
                        Toast.makeText(MainActivity2.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                });
                //    设置一个NegativeButton
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity2.this, "已取消注册 " , Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
//   Intent login_error_intent=new Intent();
//   login_error_intent.setClass(LoginActivity.this, ForgetCodeActivity.class);
//   startActivity(login_error_intent);
                break;
            case R.id.register:    //注册新的用户
                Log.v("msg","注册新用户！");
                Intent intent=new Intent(this,register_Activity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//   Intent intent=new Intent();
//   intent.setClass(LoginActivity.this, ValidatePhoneNumActivity.class);
//   startActivity(intent);
                break;
            case R.id.registfer:
                //if(SERVER_FLAG>10){
                    //Toast.makeText(this, "[内部测试--谨慎操作]", Toast.LENGTH_SHORT).show();
                //}
                //SERVER_FLAG++;
                Log.v("msg","底部测试按钮");
                break;
            case R.id.bt_username_clear:
                et_name.setText("");
                et_pass.setText("");
                break;
            case R.id.bt_pwd_clear:
                et_pass.setText("");
                break;
            case R.id.bt_pwd_eye:
                if(et_pass.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    bt_pwd_eye.setBackgroundResource(R.drawable.see);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
                }else{
                    bt_pwd_eye.setBackgroundResource(R.drawable.see);
                    et_pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                et_pass.setSelection(et_pass.getText().toString().length());
                break;
        }

    }

    private void login() {
        String password=sharedPreferences.getString(et_name.getText().toString(),null);
        if(password==null){
            Log.v("msg","没有该用户，请重新输入");
            et_name.setText("");
            et_pass.setText("");
        }
       else if(password.equals(et_pass.getText().toString())){
        Intent intent=new Intent(this,MainActivity_mainlayout.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }else{
            Log.v("msg","密码错误，请重新输入密码");
            et_pass.setText("");
        }
    }
}
