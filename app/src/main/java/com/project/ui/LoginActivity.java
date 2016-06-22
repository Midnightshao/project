package com.project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.Application.CApplication;
import com.project.R;
import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.MD5Util;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private TextView textView_re;
    private TextView textView_fo;
    private EditText edit_user;
    private EditText edit_password;
    private Button button;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Views();
        Listener();
    }
    public void Views(){
        button=(Button)findViewById(R.id.login1);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        textView_re=(TextView)findViewById(R.id.Register_textView);
        textView_fo=(TextView)findViewById(R.id.forget_textView);
        edit_user=(EditText)findViewById(R.id.Edit_text);
        edit_password=(EditText)findViewById(R.id.editText);
    }
    public void Listener(){
        textView_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            }
        });
        textView_fo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setClickable(false);
                                swipeRefreshLayout.setRefreshing(true);
                                submit();
                            }
                        });
                    }
                });
            }
        });
    }

    public void submit(){

        final RequestBody requestBody=new FormEncodingBuilder()
                .add("name",
                        edit_user.getText().toString().trim())
                .add("password",
                        MD5Util.MD5(edit_password.getText().toString()).trim())
                .build();
        final Request request = new Request.Builder().header("token",new SharedPreferencesUtil(this).ReaderToken()).url(AddressUtil.ADDRESS + "hello").post(requestBody).build();

        HttpURLClient.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"网络不畅快",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        button.setClickable(true);
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                try {
                    json_submit(response.body().string().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void json_submit(String json) throws JSONException {

        Log.i("Log","log"+json);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);button.setClickable(true);
            }

        });

        JSONObject jsonObject=new JSONObject(json);

        String booleans=jsonObject.getString("username");

        final JSONObject json_yanzheng=jsonObject.getJSONObject("token");

        final String user_id=json_yanzheng.getString("id");
        if(booleans.equals("true")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(LoginActivity.this);

                    sharedPreferencesUtil.WriterUserNamePassword(edit_user.getText().toString().trim(),edit_password.getText().toString().trim());

                    CApplication.setUser_id(user_id);

                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(LoginActivity.this,PersonActivity.class);

                    LoginActivity.this.startActivity(intent);

                    finish();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
                }
            });
        }
        String code=json_yanzheng.getString("code");
        if(code.equals("1")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(LoginActivity.this,"token 认证",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(code.equals("0")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"token过期",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
