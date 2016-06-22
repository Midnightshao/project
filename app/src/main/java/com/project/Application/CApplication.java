package com.project.Application;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by guanghaoshao on 16/3/21.
 */
public class CApplication extends Application{

    OkHttpClient client;
    public static String user_id;

    public static String getUser_id() {
        return user_id;
    }

    public static void setUser_id(String user_id) {
        CApplication.user_id = user_id;
    }

    public void onCreate() {

        super.onCreate();
//        SMSSDK.initSDK(this, "118d94aa78400", "dd7bd927c81ecb6968b7f53a11492974");
        Log.i("TAG", "TTTTTTTTTTT");

        client=new OkHttpClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(CApplication.this);

                    String Jsontoken=runs(AddressUtil.ADDRESS+"token");

                    JSONObject jsonObject=new JSONObject(Jsontoken);

                    String json=jsonObject.getString("token");

                    sharedPreferencesUtil.tokenWriter(json);

                    Log.i("token","token1"+json);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //检测是否拥有登录的值
    public boolean val(){

        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(this);
        if(sharedPreferencesUtil.ReaderUserNamePassword().equals("")||sharedPreferencesUtil.ReaderUserNamePassword().equals(null)) {
            Log.i("Tag", "tag" + "没有什么东西");
            return false;
        }else {
            return login();
        }

    }

    boolean vals;
    public  boolean login(){
        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(this);
        String usernamepassword[] = sharedPreferencesUtil.ReaderUserNamePassword();
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("name",
                        usernamepassword[0])
                .add("password",
                        (usernamepassword[1])).build();

        final Request request = new Request.Builder().header("token", new SharedPreferencesUtil(this).ReaderToken()).url(AddressUtil.ADDRESS + "hello").post(requestBody).build();

        HttpURLClient.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    vals =json_submit(response.body().string().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return vals;
    }
    public boolean json_submit(String json) throws JSONException {

        Log.i("Log","log"+json);

        JSONObject jsonObject=new JSONObject(json);

        String booleans=jsonObject.getString("username");
        JSONObject json_id=jsonObject.getJSONObject("token");
        final String user_id=json_id.getString("id");
        this.user_id=user_id;
        if(booleans.equals("true")){
                    return true;
        }else {
                 return false;
        }
    }

    public String runs(String url) throws IOException {
//        RequestBody body=new FormEncodingBuilder().build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else if (!response.isSuccessful()){

            Toast.makeText(this,"网络没有", Toast.LENGTH_SHORT).show();
            return "";
        }
        return url;
    };

}
