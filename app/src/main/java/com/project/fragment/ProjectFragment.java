package com.project.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.project.R;
import com.project.bean.project;
import com.project.ui.PersonDiscussActivity;
import com.project.util.AddressUtil;
import com.project.util.HttpURLClient;
import com.project.util.SharedPreferencesUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.M)
public class ProjectFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private int[] color={R.color.primaryBlue,R.color.colorBlue};
    private Gson gson;
    private RecyclerAdapter recyclerViewAdapter;
    private int positions=1;
    private List<project> list;
    private List<Bitmap> listBitmap;
    private DisplayImageOptions options;		// ÏÔÊ¾Í¼Æ¬µÄÉèÖÃ
    private boolean aBoolean;
    private Intent intent;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Toolbar toolbar;
    public ProjectFragment(Toolbar toolbar) {
        // Required empty public constructor
        this.toolbar=toolbar;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        positions=1;
    }
    // TODO: Rename and change types and number of parameters
    SharedPreferencesUtil sharedPreferencesUtil;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        option();
        super.onCreate(savedInstanceState);
        sharedPreferencesUtil=new SharedPreferencesUtil(this.getContext());
        list=new ArrayList();
        listBitmap=new ArrayList<>();
        recyclerViewAdapter=new RecyclerAdapter();
        data();
        recyclerViewAdapter.notifyDataSetChanged();
    }
    public void option(){
        imageLoader.init(ImageLoaderConfiguration.createDefault(this.getActivity()));
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void data(){

        final Request request = new Request.Builder()
                .header("token",sharedPreferencesUtil.ReaderToken())
                .url(AddressUtil.ADDRESS + "ProjectQuery/"+positions).get()
                .build();
        HttpURLClient.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                run(response.body().string());
            }
        });
    }
    public void run(final String string){
        Message message=new Message();
        message.obj=string;
        handlers.sendMessage(message);
    }
    Handler handlers=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String string=msg.obj.toString();
            try {
                Log.i("tag",string);
                JSONObject jsonObject=new JSONObject(string);
                String projects=jsonObject.getString("list");
                list=gson.fromJson(projects,new TypeToken<List<project>>() {
                }.getType());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(aBoolean){
                recyclerViewAdapter.notifyDataSetChanged();
                aBoolean=false;
            }else {
                recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount());
            }
        }
    };
    private RelativeLayout relativeLayout;
    public void init(View view){
//        data();
        if(list.isEmpty()){
            recyclerViewAdapter.notifyItemRemoved(0);
        }
        relativeLayout=(RelativeLayout)view.findViewById(R.id.relativeLayout1);

        gson=new Gson();
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(color);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewAdapter.onItemClick(new OnItemClick() {
            @Override
            public void SetOnItemClick(View view, project project) {

                intent=new Intent(view.getContext(), PersonDiscussActivity.class);
                intent.putExtra("position", project.getId());
                intent.putExtra("project", project);
                ProjectFragment.this.getActivity().startActivity(intent);
                ProjectFragment.this.getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                Toast.makeText(ProjectFragment.this.getContext(), String.valueOf(project.getId()), Toast.LENGTH_SHORT).show();
            }

        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int   lastVisibleItem;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("Tag", "tag是" + recyclerViewAdapter.getScrollPositions() + "positions" + positions);
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(lastVisibleItem==recyclerViewAdapter.getItemCount()-1&&newState==RecyclerView.SCROLL_STATE_IDLE ){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            positions=lastVisibleItem/8+1;
                            if(list.size()%8!=0){
                                Log.i("tag","dingwei");
                                recyclerViewAdapter.notifyItemRemoved(list.size()+1);
                            }else {
                                data();
                            }
                        }
                    },800);
                }
            }
        });
        handler.sendEmptyMessage(0);
        requested();
    }
    public void requested(){
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },1200);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onRefresh() {
        aBoolean=true;
        handler.sendEmptyMessage(0);
    }
    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            swipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    positions = 1;
                    data();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1500);
        }
    };
    public interface OnItemClick{
        public void SetOnItemClick(View view,project project);
    }
    class RecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener{

        public OnItemClick onItemClick;
        private int position;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==1){
                ViewHolds viewHold=new ViewHolds(LayoutInflater.from(ProjectFragment.this.getActivity()).inflate(R.layout.fragment_project_item1,parent,false));
                return viewHold;
            }else {
                ViewHold viewHold=new ViewHold(LayoutInflater.from(ProjectFragment.this.getActivity()).inflate(R.layout.fragment_project_item,parent,false));
                viewHold.itemView.setOnClickListener(this);
                return viewHold;
            }
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            this.position=position;
            if (position!=this.getItemCount()-1){
                com.project.bean.project project=list.get(position);
                        Log.i("tag", "tag_person" + position);
                        imageLoader.displayImage(AddressUtil.ADDRESS + "img_person/" + project.getImage_type(), ((ViewHold) holder).imageView, options);
                        ((ViewHold) holder).textView_content.setText(project.getContent());
                        ((ViewHold) holder).textView_title.setText(project.getTitle());
                        ((ViewHold) holder).textView.setText(project.getName());
                        ((ViewHold) holder).itemView.setTag(project);
            }
        }

        @Override
        public int getItemCount() {
            if (list.isEmpty()){
                return 0;
            }
            return list.size()+1;
        }

        @Override
        public void onClick(View v) {
            onItemClick.SetOnItemClick(v,(project)v.getTag());
        }

        public void onItemClick(OnItemClick onItemClick) {
            this.onItemClick=onItemClick;
        }

        class ViewHold extends RecyclerView.ViewHolder{
            private ImageView imageView;
            private TextView textView_title;
            private TextView textView_content;
            private TextView textView;
            public ViewHold(View itemView) {
                super(itemView);
                imageView=(ImageView)itemView.findViewById(R.id.imageView_header1);
                textView_title=(TextView)itemView.findViewById(R.id.title_project);
                textView_content=(TextView)itemView.findViewById(R.id.textView_content);
                textView=(TextView)itemView.findViewById(R.id.textView);
            }

        }
        class ViewHolds extends RecyclerView.ViewHolder{

            public ViewHolds(View itemView) {
                super(itemView);
            }

        }
        @Override
        public int getItemViewType(int position) {
            if(position==this.getItemCount()-1){
                    return 1;
            }else {
                return 0;
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        private synchronized int getScrollPositions() {
            return this.position;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
}
