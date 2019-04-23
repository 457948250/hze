package com.healthy.com.healing.ui.BaseMine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.fragment.BaseMine;
import com.healthy.com.healing.litepal.SportRecordList;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.ui.MainActivity;
import com.healthy.com.healing.util.StatusBarUtil;
import com.healthy.com.healing.util.UserUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends BaseActivity {

    private View Login_LinearLayout;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText ET_useraccount;
    private EditText ET_userpassword;
    private CheckBox checkBox;
    private Button BT_login;
    private Button BT_register;
    private static SportRecordList sportRecordList;
    private static User user;
    private static boolean isLogin = false;
    private static Tencent mTencent;
    private ImageView QQlogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Login_LinearLayout = findViewById(R.id.Login_LinearLayout);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ET_useraccount = (EditText) findViewById(R.id.ET_useraccount);
        ET_userpassword = (EditText) findViewById(R.id.ET_userpassword);
        checkBox = (CheckBox) findViewById(R.id.CB_ischeck);
        BT_login = (Button) findViewById(R.id.BT_login);
        BT_register = (Button) findViewById(R.id.BT_register);
        mTencent = Tencent.createInstance("101559681",this.getApplicationContext());//101559681为自己的AppID
        QQlogin = (ImageView) findViewById(R.id.qqLogin);

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Login_LinearLayout.setPadding(0,statusBarHeight,0,0);

        findViewById(R.id.tv_backto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        if(pref.getString("account", "") != "" && pref.getString("password", "") != "" && !Setting.getisIsLogout()){
//            String account = pref.getString("account", "");
//            String password = pref.getString("password", "");
//            loginevent(account,password);
//        }
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            ET_useraccount.setText(account);
            ET_userpassword.setText(password);
            checkBox.setChecked(true);
            if (!Setting.getisIsLogout()){
                loginevent(account,password);
            }
        }
        BT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = ET_useraccount.getText().toString().trim();
                final String password = ET_userpassword.getText().toString().trim();
                loginevent(account,password);
            }
        });

        BT_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.healthy.com.healing.REGISTER");
                startActivity(intent);
            }
        });

        QQlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get_simple_userinfo
                mTencent.login(Login.this,"all",new BaseUiListener());
            }
        });
    }

    private void loginevent(final String account,final String password) {
        User user = UserUtil.queryUser(account);
        //SportRecordList sportRecordList = SportRecordUtil.querySportRecordList(account);
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "账号或密码不能为空", Toast.LENGTH_LONG).show();
        } else if (user == null) {
            Toast.makeText(Login.this, "用户不存在！", Toast.LENGTH_SHORT).show();
        } else if (!user.getUserpassword().equals(password)) {
            Toast.makeText(Login.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
        } else {
            //登录成功
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path="http://10.169.87.114:8080/AndroidTest/mustLogin?useraccount="+account+"&userpassword="+password;
                    try {
                        try{
                            URL url = new URL(path); //新建url并实例化
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");//获取服务器数据
                            connection.setReadTimeout(8000);//设置读取超时的毫秒数
                            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
                            InputStream in = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String result = reader.readLine();//读取服务器进行逻辑处理后页面显示的数据
                            Log.d("Login","run: "+result);
                            if (result.equals("login successfully!")){
                                Log.d("Login","run2: "+result);
                                Looper.prepare();
                                Log.d("Login","run3: "+result);
                                Toast.makeText(Login.this,"You logined successfully!",Toast.LENGTH_SHORT).show();
                                Log.d("Login","run4: "+result);
                                Looper.loop();
                            }
                        }catch (MalformedURLException e){}
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            setIsLogin(true);
            setUser(user);
            //setSportRecordList(sportRecordList);
            EventBus.getDefault().post(new BaseMine.updataName());
            ProgressDialog progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("正在登录，请稍等");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Timer timer=new Timer();
            TimerTask task=new TimerTask(){
                public void run(){
                }
            };
            timer.schedule(task, 5000);
            progressDialog.cancel();
            editor = pref.edit();
            if (checkBox.isChecked()) {
                editor.putBoolean("remember_password", true);
                editor.putString("account", account);
                editor.putString("password", password);
            } else {
                editor.clear();
            }
            editor.apply();
            Login.this.finish();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }
    }


    private class BaseUiListener implements IUiListener {
//这个类需要实现三个方法 onComplete（）：登录成功需要做的操作写在这里
// onError onCancel 方法具体内容自己搜索

        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------" + response.toString());
                String openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);
                //saveUser("44", "text", "text", 1);
                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));
                setTencent(mTencent);

                Log.v("TAG", "-------------" + openidString);
                //access_token= ((JSONObject) response).getString("access_token");
                //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像什么的，这个时候怎么办？
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？

             获取详细信息的UserInfo ，返回的信息参看下面地址：
             http://wiki.open.qq.com/wiki/%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF#1._Tencent.E7.B1.BB.E7.9A.84request.E6.88.96requestAsync.E6.8E.A5.E5.8F.A3.E7.AE.80.E4.BB.8B
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(Login.this, qqToken);

            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了
                    try {Log.v("----TAG--", "-------------" + o.toString());
                        Toast.makeText(Login.this, ((JSONObject)o).getString("nickname")+((JSONObject)o).getString("gender"), Toast.LENGTH_SHORT).show();
                        Log.v("UserInfo",o.toString());

                        Intent intent1 = new Intent(Login.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo","onError");
                }

                @Override
                public void onCancel() {
                    Log.v("UserInfo","onCancel");
                }
            });
        }


        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
        }
    }

    //获取登录标识符
    public static boolean getisLogin() {

        return isLogin;
    }
    //设置登录标识符
    public static void setIsLogin(boolean b) {
        isLogin = b;
    }
    //把当前已登录的对象传进来
    public static void setUser(User u) {
        user = u;
    }
    //获取当前登录的对象
    public static User getUser() {
        return user;
    }

    //把当前已登录的对象传进来
    public static void setSportRecordList(SportRecordList s) {
        sportRecordList = s;
    }
    //获取当前登录的对象
    public static SportRecordList getSportRecordList() {
        return sportRecordList;
    }
    public static void setTencent(Tencent t) {
        mTencent = t;
    }
    public static Tencent getTencent() {
        return mTencent;
    }

}
