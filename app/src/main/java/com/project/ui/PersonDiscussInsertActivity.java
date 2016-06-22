package com.project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.project.Application.CApplication;
import com.project.R;
import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class PersonDiscussInsertActivity extends AppCompatActivity {
    private ImageView imageView;
    private int position;
    private AppCompatEditText appCompatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_discuss_insert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
        setSupportActionBar(toolbar);
        Listener();
    }

    public void init() {
        imageView = (ImageView) findViewById(R.id.image_send);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        appCompatEditText=(AppCompatEditText)findViewById(R.id.content_textView);
    }

    public void Listener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
                finish();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String[] name=new SharedPreferencesUtil(PersonDiscussInsertActivity.this).ReaderUserNamePassword();
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("rid_id", String.valueOf(CApplication.user_id))
                    .add("project_id",
                            String.valueOf(position))
                    .add("content",
                            (appCompatEditText.getText().toString()))
                    .add("img_type", name[0] + "_picture.jpg")
                    .add("name", new SharedPreferencesUtil(PersonDiscussInsertActivity.this).ReaderName())
                    .build();
            final Request request = new Request.Builder()
                    .header("token", new SharedPreferencesUtil(PersonDiscussInsertActivity.this).ReaderToken())
                    .url(AddressUtil.ADDRESS + "ProjectDiscussInsert").post(requestBody)
                    .build();
            HttpURLClient.enqueue(request, new com.squareup.okhttp.Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                }
            });

        }


    };
}
