package com.project.ui;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.Application.CApplication;
import com.project.R;
import com.project.fragment.MusicFragment;
import com.project.fragment.ProjectFragment;
import com.project.fragment.TopicFragment;
import com.project.util.FileUtils;
import com.project.util.SharedPreferencesUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Bitmap bit=null;

    private ViewPager viewPager;
    private List<String> list;
    private List<View> mList;
    private ImageView imageView;

    private SharedPreferencesUtil sharedPreferencesUtil;
    private Fragment fragment[]=new Fragment[5];
    private CApplication app;
    private Bitmap bitmap;
    private TextView textViewName;
    private TextView textViewEmail;
    private Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        bitmap= BitmapFactory.decodeFile(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");
        navBitmap();
        sharedPreferencesUtil=new SharedPreferencesUtil(this);
        nav();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        app=(CApplication) getApplication();
        sharedPreferencesUtil=new SharedPreferencesUtil(this);

        bitmap= BitmapFactory.decodeFile(FileUtils.FILE_CACHE + "/sdtCard_cache.jpg");

        dataTabs();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);

        viewPager=(ViewPager)findViewById(R.id.ViewPager);

        this.getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageView=(ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView_header);
        textViewName=(TextView)navigationView.getHeaderView(0).findViewById(R.id.name_header);
        textViewEmail=(TextView)navigationView.getHeaderView(0).findViewById(R.id.email_header);

//
        viewPager.setAdapter(new PagerAdapters(this.getSupportFragmentManager()));

        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.sliding_tabs);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.addTab(tabLayout.newTab().setText(list.get(0).toString()));

        tabLayout.addTab(tabLayout.newTab().setText(list.get(1).toString()));

        tabLayout.addTab(tabLayout.newTab().setText(list.get(2).toString()));

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setTabsFromPagerAdapter(new PagerAdaptersTab());

        navBitmap();

        nav();
    }
    //
    public void navBitmap(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }
    public void nav(){
        if(!sharedPreferencesUtil.ReaderName().equals("")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewName.setText(sharedPreferencesUtil.ReaderName());
                    textViewEmail.setText(sharedPreferencesUtil.ReaderEmail());
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewName.setText("未知姓名");
                    textViewEmail.setText("android@email.com");
                }
            });
        }
    }
    public void dataTabs(){
        list=new ArrayList();
        mList=new ArrayList();

        list.add("程序员");

        list.add("视频");

        list.add("音乐");

        for(int i=0;i<mList.size();i++){
            View view=new View(this);
            mList.add(view);
        }

    }
    class PagerAdaptersTab extends PagerAdapter{

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView();
            container.addView(mList.get(position));//添加页卡
            return mList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));//删除页卡
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position);//页卡标题
        }

    }
    class PagerAdapters extends FragmentPagerAdapter{


    public PagerAdapters(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=new ProjectFragment(toolbar);
                break;
            case 1:
                fragment=new TopicFragment();
                break;
            case 2:
                fragment=new MusicFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //登录注册
        if(id==R.id.nav_camera){
            //打开图片库
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            MainActivity.this.startActivityForResult(intent, 0);
        } else if (id == R.id.touxiang) {
            boolean logina=app.val();
            if(CApplication.user_id!=null){
                Log.i("Tag", "tag11111" + app.val());
                Intent in=new Intent(MainActivity.this,PersonActivity.class);
                MainActivity.this.startActivity(in);
                MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }else {
                Log.i("Tag", "tag11111" + app.val());
                Intent in=new Intent(MainActivity.this,LoginActivity.class);
                MainActivity.this.startActivity(in);
                MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            Log.i("Tag", "tag2222222" + app.val());
        } else if (id == R.id.nav_gallery) {
            if(CApplication.user_id!=null){
                Intent intent=new Intent(this,InsertActivity.class);
                this.startActivity(intent);
                MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(this,"发表消息",Toast.LENGTH_SHORT).show();
            }else {
                Intent in=new Intent(MainActivity.this,LoginActivity.class);
                MainActivity.this.startActivity(in);
                MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        } else if (id == R.id.nav_share){
            Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
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
                            bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            Log.e("String", "bit" + bit.toString());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bitmap= BitmapFactory.decodeFile(FileUtils.FILE_CACHE+"/sdtCard_cache.jpg");
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
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
    private static Bitmap small(Bitmap bitmap) {

        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
