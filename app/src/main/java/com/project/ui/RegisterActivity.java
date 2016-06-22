package com.project.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    private EditText edtit_Phone;
    private EditText editText_password;
    private EditText editText_password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Views();
    }
    public void Views(){
        edtit_Phone=(EditText)findViewById(R.id.editText_phone);
        editText_password=(EditText)findViewById(R.id.password);
        editText_password1=(EditText)findViewById(R.id.password1);
    }
    public void Listener(){
        if(TextUtils.isEmpty(edtit_Phone.getText())) {
            Toast.makeText(this,"用户名不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editText_password.getText())){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editText_password1.getText())){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
        }else if(!editText_password.getText().toString().equals(editText_password1.getText().toString())){
                Toast.makeText(this,"两次输入不一致",Toast.LENGTH_SHORT).show();
        }else{
            //登陆注册信息
            final RequestBody requestBody=new FormEncodingBuilder()
                    .add("name",
                            edtit_Phone.getText().toString())
                    .add("password",
                            MD5Util.MD5(editText_password.getText().toString()))
                    .build();

            final Request request = new Request.Builder().header("token",new SharedPreferencesUtil(this).ReaderToken()).url(AddressUtil.ADDRESS + "add_Register").post(requestBody).build();


          HttpURLClient.enqueue(request, new Callback() {
              @Override
              public void onFailure(Request request, IOException e) {

              }

              @Override
              public void onResponse(Response response) throws IOException {
//                  Log.i("log","response"+response.body().string());

                  try {
                      Useranme(response.body().string());
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }
          });

        }
    }

    public boolean Useranme(String log) throws JSONException {

        JSONObject object=new JSONObject(log);

        JSONObject state= object.getJSONObject("state");

        String  code=object.getString("state");

        String code_state=state.getString("code");

        if(code.equals("0")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "token 没有", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(code_state.equals("0")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "注册失败已经有这个用户了", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(code_state.equals("1")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(RegisterActivity.this);
                    sharedPreferencesUtil.WriterUserNamePassword(edtit_Phone.getText().toString().trim(),editText_password.getText().toString().trim());
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                }
            });
        }


        return true;
    };
    public void onClick(View view){
        switch (view.getId()){
            case R.id.submit:
                Listener();
                break;
        }
    }
}
