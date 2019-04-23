package com.healthy.com.healing.ui.BaseMine;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.util.StatusBarUtil;
import com.healthy.com.healing.util.UserUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends BaseActivity {

    private View Register_LinearLayout;
    private EditText ET_useraccount;
    private EditText ET_userpassword_1;
    private EditText ET_userpassword_2;
    private Button BT_register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register_LinearLayout = findViewById(R.id.Register_LinearLayout);
        ET_useraccount = (EditText)findViewById(R.id.ET_Registeruseraccount);
        ET_userpassword_1 = (EditText) findViewById(R.id.ET_userpassword_1);
        ET_userpassword_2 = (EditText)findViewById(R.id.ET_userpassword_2);
        BT_register = (Button)findViewById(R.id.BT_register);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Register_LinearLayout.setPadding(0,statusBarHeight,0,0);

        findViewById(R.id.IV_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register.this.finish();
            }
        });
        BT_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = ET_useraccount.getText().toString().trim();
                final String password_1 = ET_userpassword_1.getText().toString().trim();
                String password_2 = ET_userpassword_2.getText().toString().trim();
                User user = UserUtil.queryUser(account);
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password_1)|| TextUtils.isEmpty(password_2)) {
                    Toast.makeText(Register.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }else if (!password_1.equals(password_2)){
                    Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                }else if(user == null){//注册事件
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String path="http://10.169.87.114:8080/AndroidTest/mustregister";
                            String data="useraccount="+account+"&userpassword="+password_1+"";
                            try {
                                try{
                                    URL url = new URL(path); //新建url并实例化
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setRequestMethod("POST");//获取服务器数据
                                    connection.setReadTimeout(8000);//设置读取超时的毫秒数
                                    connection.setConnectTimeout(8000);//设置连接超时的毫秒数
                                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                    connection.setRequestProperty("Content-Length", data.length()+"");
                                    connection.setDoOutput(true);
                                    connection.setDoInput(true);
                                    DataOutputStream out =new DataOutputStream(connection.getOutputStream());
                                    out.write(data.getBytes());

                                    InputStream in = connection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                    String result = reader.readLine();//读取服务器进行逻辑处理后页面显示的数据
                                    Log.d("Register","run: "+result);
                                    if (result.equals("register successfully!")){
                                        Log.d("Register","run2: "+result);
                                        Looper.prepare();
                                        Log.d("Register","run3: "+result);
                                        Toast.makeText(Register.this,"You register successfully!",Toast.LENGTH_SHORT).show();
                                        Log.d("Register","run4: "+result);
                                        Looper.loop();
                                    }else if (result.equals("user is exist!")){
                                        Toast.makeText(Register.this,"user is exist!",Toast.LENGTH_SHORT).show();
                                    }reader.close();
                                }catch (MalformedURLException e){}
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    UserUtil.addUser(account,password_1);
                    //SportRecordUtil.addSportRecordList(account);
                    Toast.makeText(Register.this,"注册成功！",Toast.LENGTH_SHORT).show();
                    Register.this.finish();
                }else{
                    Toast.makeText(Register.this,"用户已存在！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
