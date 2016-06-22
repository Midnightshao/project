package com.project.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.project.Application.CApplication;
import com.project.R;
import com.project.bean.ProjectDiscuss;
import com.project.bean.project;
import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonDiscussActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private CApplication cApplication;
    private int position;
    private int positions=1;
    private project projects;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DisplayImageOptions options;
    private int[] color={R.color.primaryBlue,R.color.colorBlue};
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private Gson gson;
    private List list;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapters recyclerViewAdapter;
    private FloatingActionButton fab;
    private volatile boolean aBoolean;
    private volatile boolean aBoolean1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_discuss);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         fab = (FloatingActionButton) findViewById(R.id.fab_discuss);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CApplication.user_id!=null) {
                    Toast.makeText(PersonDiscussActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PersonDiscussActivity.this.getApplication(), PersonDiscussInsertActivity.class);
                    intent.putExtra("position", position);
                    PersonDiscussActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(PersonDiscussActivity.this, "没有登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh2);
        swipeRefreshLayout.setColorSchemeResources(color);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        Listener();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Log.i("tag","tag----------------"+msg.obj.toString());
                    recyclerViewAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    public void init(){
        gson=new Gson();
        list=new ArrayList();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerViewD);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter=this.new RecyclerViewAdapters();
        recyclerView.setAdapter(recyclerViewAdapter);
        cApplication= (CApplication) getApplication();
        Intent intent=getIntent();
        position=intent.getIntExtra("position",0);
        projects= (project) intent.getSerializableExtra("project");
        Log.i("tag", "position" + position);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

    }
    private Animation animation;
    public void Listener() {
        animation= AnimationUtils.loadAnimation(PersonDiscussActivity.this, R.anim.translate);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.clearAnimation();
                animation.cancel();
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener(){
                    int   lastVisibleItem;
                    int   dy;
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(lastVisibleItem==recyclerViewAdapter.getItemCount()-1 && newState==RecyclerView.SCROLL_STATE_IDLE){

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        if(list.size()%8==0){
                                            positions=lastVisibleItem/8+1;
                                            data();
                                            recyclerViewAdapter.notifyDataSetChanged();
                                        }else {
                                            recyclerViewAdapter.notifyItemRemoved(recyclerViewAdapter.getItemCount());
                                        }
                                }
                            },800);
                        }
                        if(aBoolean && newState==RecyclerView.SCROLL_STATE_SETTLING){
                            if(fab.getVisibility()==View.VISIBLE){
                                fabs(dy);
                            }
                        }else if(newState==RecyclerView.SCROLL_STATE_SETTLING){
                                fab.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();

                        this.dy=dy;
                        if(dy>0){
                            aBoolean=true;
                        }else if(dy<0){
                            aBoolean=false;
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        positions= 1;
        data();
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1300);
    }

    public synchronized void fabs(int dy){
        Log.i("tag", "tag++++++++++++++ dy" + dy);
        fab.setAnimation(animation);
        animation.start();
    }
    public void data(){

        Log.i("Tag","tag"+positions);

        final RequestBody requestBody = new FormEncodingBuilder()
                .add("id", String.valueOf(position))
                .build();
        final Request request = new Request.Builder().header("token",new SharedPreferencesUtil(PersonDiscussActivity.this)
                .ReaderToken()).url(AddressUtil.ADDRESS + "ProjectDiscussQuery/"+positions)
                .post(requestBody).build();

        HttpURLClient.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                runHttp(response.body().string());
            }
        });

    }
    public void runHttp(final String string){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("tag", "tag" + string);
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        String projects = jsonObject.getString("listquery");
                        Log.i("listquery", projects);
                        list = gson.fromJson(projects, new TypeToken<List<ProjectDiscuss>>() {
                        }.getType());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                positions=1;
                data();
                recyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }
    class RecyclerViewAdapters extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==1){
                ViewHold1 viewHold=new ViewHold1(LayoutInflater.from(PersonDiscussActivity.this).inflate(R.layout.recyclera_item, parent, false));
                viewHold.textView.setText(projects.getTitle());
                viewHold.content.setText(projects.getContent());
                imageLoader.displayImage(AddressUtil.ADDRESS + "img_person/" + projects.getImage_type(),viewHold.imageView, options);
                return viewHold;
            }else if(viewType==2){
                ViewHold viewHold=new ViewHold(LayoutInflater.from(PersonDiscussActivity.this).inflate(R.layout.fragment_project_item1, parent, false));
                return viewHold;
            }else if(viewType==3){
                ViewHold viewHold=new ViewHold(LayoutInflater.from(PersonDiscussActivity.this).inflate(R.layout.recyclerc_item, parent, false));
                return viewHold;
            } else {
                ViewHold viewHold=new ViewHold(LayoutInflater.from(PersonDiscussActivity.this).inflate(R.layout.recyclerd_item, parent, false));
                return viewHold;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(position!=0&&position!=this.getItemCount()-1){
            ProjectDiscuss projectDiscuss= (ProjectDiscuss) list.get(position-1);
            Log.i("tag", "1111111111" + projectDiscuss.toString());
            imageLoader.displayImage(AddressUtil.ADDRESS + "img_person/" + projectDiscuss.getImage_type(), ((ViewHold) holder).getImageView(), options);
            ((ViewHold) holder).getTextView().setText(projectDiscuss.getName());
            ((ViewHold) holder).getContent().setText(projectDiscuss.getContent());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date= new Date();
                date.setTime(Long.valueOf(projectDiscuss.getTime()));
            ((ViewHold) holder).getTextView_time().setText(sdf.format(date));
            }
        }

        public int getItemViewType(int position) {
            if(position==0){
                return 1;
            }else if(position>2&&position==this.getItemCount()-1){
                return 2;
            }else if(position<2&&position==this.getItemCount()-1){
                return 3;
            }else {
                return 0;
            }
        }
        @Override
        public int getItemCount() {
            return list.size()+2;
        }
        class ViewHold extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView;
            public TextView content;
            public TextView textView_time;
            public ViewHold(View itemView) {
                super(itemView);
                imageView=(ImageView)itemView.findViewById(R.id.imageView_hear1);
                textView=(TextView)itemView.findViewById(R.id.name);
                content=(TextView)itemView.findViewById(R.id.content_textView1);
                textView_time=(TextView)itemView.findViewById(R.id.text_tiem);
            }

            public ImageView getImageView() {
                return imageView;
            }

            public TextView getTextView() {
                return textView;
            }

            public TextView getContent() {
                return content;
            }

            public TextView getTextView_time() {
                return textView_time;
            }

            public void setTextView_time(TextView textView_time) {
                this.textView_time = textView_time;
            }
        }
        class ViewHold1 extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView content;
            public TextView textView;
            public ViewHold1(View itemView) {
                super(itemView);
                imageView=(ImageView)itemView.findViewById(R.id.imageView_te);
                textView=(TextView)itemView.findViewById(R.id.textView_title1);
                content=(TextView)itemView.findViewById(R.id.textView_content1);
            }

        }
    }
}
