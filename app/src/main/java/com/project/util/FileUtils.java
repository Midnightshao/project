package com.project.util;

import android.os.Environment;

/**
 * Created by guanghaoshao on 16/4/29.
 */
public class FileUtils {
    public static final String FILE_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/project/";
    public static boolean isHasSD(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else {
            return false;
        }
    }
}
