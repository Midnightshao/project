package com.project.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.Application.CApplication;
import com.project.R;
import com.project.util.AddressUtil;
import com.project.util.FileUtils;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class PersonActivity extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private Bitmap bit=null;
    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageView2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Toolbar toolbar;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Listener();

        init();
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animator animator = ViewAnimationUtils.createCircularReveal(
                            relativeLayout,
                            fab.getWidth() / 2,
                            fab.getHeight() / 2,
                            relativeLayout.getWidth(),
                            0);
                    animator.addPauseListener(new Animator.AnimatorPauseListener() {
                        @Override
                        public void onAnimationPause(Animator animation) {

                        }

                        @Override
                        public void onAnimationResume(Animator animation) {

                        }
                    });
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(600);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                    PersonActivity.this.startActivityForResult(intent, 0);
                                }
                            }).start();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {


                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(800);
                    animator.start();

                }
            });

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(1);
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        imageView1,
                        imageView1.getWidth() / 2,
                        imageView1.getHeight() / 2,
                        imageView1.getWidth(),
                        0);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        imageView1.setVisibility(View.GONE);
                        imageView2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.setDuration(800);
                animator.start();

            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }
    public void init(){
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 0);

        Bitmap bitmap= BitmapFactory.decodeFile(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }
    }
    public void data(){
        if(CApplication.getUser_id()!=null){
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("user_id",CApplication.user_id)
                    .build();

            final Request request = new Request.Builder()
                    .header("token",new SharedPreferencesUtil(this).ReaderToken())
                    .url(AddressUtil.ADDRESS + "PersonQuery")
                    .post(requestBody)
                    .build();
            HttpURLClient.enqueue(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        initData(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public void initData(String data) throws JSONException {
        Log.i("Tag","tag"+data);
        JSONObject jsonObject=new  JSONObject(data);
        String string=jsonObject.getString("login");
        Log.i("Tag",string);
        if(string.equals("false")){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }else {
            if(jsonObject.getString("person").equals("")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText_name.setText("");
                        editText_clock.setText("");
                        editText_address.setText("");
                        editText_positive.setText("");
                        editText_github.setText("");
                        HttpURLClient.getInstance();
                        imageView.setImageResource(R.drawable.ic_shape);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }else {
                JSONArray jsonPerson1 = jsonObject.getJSONArray("person");

                JSONObject jsonObject1 = jsonPerson1.getJSONObject(0);
                final String image_url = jsonObject1.getString("image_type");
                final String name = jsonObject1.getString("name");
                final String age = jsonObject1.getString("age");
                final String email = jsonObject1.getString("email");
                final String address = jsonObject1.getString("address");
                final String github = jsonObject1.getString("github");
                Log.i("name", name);
                Log.i("age", age);
                Log.i("email", email);
                Log.i("address", address);
                Log.i("github", github);
                Log.i("image_type", image_url);
                sharedPreferencesUtil.WriterName(name);
                sharedPreferencesUtil.WriterEmail(email);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText_name.setText(name);
                        editText_clock.setText(age);
                        editText_address.setText(email);
                        editText_positive.setText(address);
                        editText_github.setText(github);
                        HttpURLClient.getInstance();
                        HttpURLClient.displayImage(imageView, AddressUtil.ADDRESS + "img_person/" + image_url);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode,Intent data){
        if (requestCode == 0) {
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bm = null;
                ContentResolver resolver = getContentResolver();
                try {
                    bm = MediaStore.Images.Media.getBitmap(resolver, uri);
                    bit = small(bm);
                    if (FileUtils.isHasSD()) {
                        try {
                            File cache = new File(FileUtils.FILE_CACHE);
                            if (cache.isFile()) {
                                cache.delete();
                            }
                            if (!cache.exists()) {
                                cache.mkdir();
                            }
                            OutputStream stream = new FileOutputStream(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");
                            bit.compress(Bitmap.CompressFormat.JPEG,20, stream);
                            Log.e("String", "bit" + bit.toString());

                            Message msg = new Message();
                            h.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private EditText editText_name;
    private EditText editText_clock;
    private EditText editText_address;
    private EditText editText_positive;
    private EditText editText_github;
    private RelativeLayout relativeLayout;
    public void Listener(){

        sharedPreferencesUtil=new SharedPreferencesUtil(this);

        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout_person);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setFocusable(true);
                relativeLayout.setFocusableInTouchMode(true);
            }
        });

        imageView=(ImageView)findViewById(R.id.imageView2);
        imageView1=(ImageView)findViewById(R.id.image_add);
        imageView2=(ImageView)findViewById(R.id.image_duigou);


        editText_name=(EditText)findViewById(R.id.editText_name);
        editText_clock=(EditText)findViewById(R.id.editText_clock);
        editText_address=(EditText)findViewById(R.id.editText_address);
        editText_positive=(EditText)findViewById(R.id.editText_postive);
        editText_github=(EditText)findViewById(R.id.editText_github);
    }
    public  void inited(){
        final OkHttpClient mOkHttpClient = new OkHttpClient();
    }
    Handler h=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(PersonActivity.this,"添加成功",Toast.LENGTH_SHORT).show();

            Bitmap bitmap= BitmapFactory.decodeFile(FileUtils.FILE_CACHE+"/sdtCard_cache.jpg");

            imageView.setImageBitmap(bitmap);
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    data();
                    break;
                case 1:
                    try {
                        ImageUrl();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private static Bitmap small(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_duigou:
                    submit();
                break;
        }
    }
    public void submit(){

        Toast.makeText(PersonActivity.this,"点击",Toast.LENGTH_SHORT).show();

        sharedPreferencesUtil.WriterName(editText_name.getText().toString().trim());
        sharedPreferencesUtil.WriterName(editText_address.getText().toString().trim());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.GONE);
            }
        });

        final RequestBody requestBody = new FormEncodingBuilder()
                .add("name",
                        editText_name.getText().toString().trim())
                .add("age",
                        (editText_clock.getText().toString().trim()))
                .add("email",
                        editText_address.getText().toString().trim())
                .add("address",
                        editText_positive.getText().toString().trim())
                .add("github", editText_github.getText().toString().trim())

                .add("user_id", CApplication.user_id)

                .build();

        final Request request = new Request.Builder()
                .header("token",new SharedPreferencesUtil(this).ReaderToken())
                .url(AddressUtil.ADDRESS + "PersonSave")
                .post(requestBody)
                .build();

        HttpURLClient.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(PersonActivity.this,request.body().toString()+"没有添加成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                submitUpdate();
            }
        });
    }
    public void ImageUrl() throws FileNotFoundException {
        Log.i("Tag", "image提交");
        File file= new File(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");
        Log.i("Tag","file"+file);
        RequestBody fileBody =RequestBody.create(MediaType.parse("application/octet-stream"),file);
        String name=sharedPreferencesUtil.ReaderUserName();
        RequestBody requestBody1 = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"name\""),
                        RequestBody.create(null, name))
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"sdCard_cache.jpg\""),fileBody)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"rid_id\""), RequestBody.create(null, CApplication.user_id))
                .build();
        Request request1 = new Request.Builder()
                .header("token", new SharedPreferencesUtil(this).ReaderToken())
                .url(AddressUtil.ADDRESS + "PersonImage")
                .post(requestBody1)
                .build();

        HttpURLClient.enqueue(request1, new Callback() {
            @Override
            public void onFailure(final Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonActivity.this, request.body().toString() + "没有添加成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PersonActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void submitUpdate(){
        handler.sendEmptyMessage(0);
    }
    @Override
    public void onRefresh() {
//        swipeRefreshLayout.isRefreshing();
//        swipeRefreshLayout.setRefreshing(true);
    }
}
