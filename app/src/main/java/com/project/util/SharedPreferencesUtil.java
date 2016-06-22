package com.project.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by guanghaoshao on 16/4/11.
 */
public class SharedPreferencesUtil
{
    private Context context;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public SharedPreferencesUtil(Context context){
        this.context=context;
        preferences=context.getSharedPreferences("shareInfo",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public void tokenWriter(String token){
        editor.putString("token",token);
        editor.commit();
    }
    public String ReaderToken(){
        return preferences.getString("token","");
    }

    public void WriterUserNamePassword(String name,String password){
        editor.putString("username",name);
        editor.putString("password",MD5Util.MD5(password));
        editor.commit();
    }
    public String[] ReaderUserNamePassword(){
        String name=preferences.getString("username","");
        String password=preferences.getString("password","");
        String st[]={name,password};
        return  st;
    }
    public String ReaderUserName(){
        String name=preferences.getString("username","");

        return  name;
    }
    public void WriterId(String id){
        editor.putString("id",id);
        editor.commit();
    }
    public String ReaderId(){
        String name=preferences.getString("id","");
        String st=name;
        return  st;
    }
    public void WriterName(String name){
        editor.putString("name",name);
        editor.commit();
    }
    public String ReaderName(){
        String name=preferences.getString("name","");
        String st=name;
        return  st;
    }
    public void WriterClock(String clock){
        editor.putString("clock",clock);
        editor.commit();
    }
    public String ReaderClock(){
        String name=preferences.getString("clock","");
        String st=name;
        return  st;
    }
    public void WriterEmail(String email){
        editor.putString("email",email);
        editor.commit();
    }
    public String ReaderEmail(){
        String name=preferences.getString("email","");
        String st=name;
        return  st;
    }
    public void WriterAddress(String address){
        editor.putString("address",address);
        editor.commit();
    }
    public String ReaderAddress(){
        String name=preferences.getString("address","");
        String st=name;
        return  st;
    }
    public void WriterGithub(String github){
        editor.putString("github",github);
        editor.commit();
    }
    public String ReaderGithub(){
        String name=preferences.getString("github","");
        String st=name;
        return  st;
    }
}
