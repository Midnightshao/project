package com.project.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.Application.CApplication;
import com.project.R;
import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class InsertActivity extends AppCompatActivity implements View.OnClickListener{
    private AppCompatEditText editText_name;
    private AppCompatEditText editText_content;
    private ImageView image_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        Listener();
    }
    public void init(){
        editText_name=(AppCompatEditText)findViewById(R.id.title);
        editText_content=(AppCompatEditText)findViewById(R.id.content);
        image_send=(ImageView)findViewById(R.id.image_send);
        image_send.setOnClickListener(this);
    }
    public void Listener(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_send:
                final String name[]=new SharedPreferencesUtil(InsertActivity.this).ReaderUserNamePassword();
                final String names=new SharedPreferencesUtil(InsertActivity.this).ReaderName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final RequestBody requestBody = new FormEncodingBuilder()
                                .add("name",names)
                                .add("title",
                                        editText_name.getText().toString())
                                .add("content",
                                        (editText_content.getText().toString()))
                                .add("img_type", name[0]+"_picture.jpg")
                                .build();

                        if (!CApplication.getUser_id().equals(null)) {
                            final Request request = new Request.Builder()
                                    .header("token", new SharedPreferencesUtil(InsertActivity.this).ReaderToken())
                                    .header("user_id", CApplication.getUser_id())
                                    .url(AddressUtil.ADDRESS + "ProjectInsert").post(requestBody).build();

                            HttpURLClient.enqueue(request, new Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {

                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(InsertActivity.this, "提交", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
                InsertActivity.this.finish();
                break;
        }
    }
}
