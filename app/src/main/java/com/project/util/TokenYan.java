package com.project.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guanghaoshao on 16/4/26.
 */
public class TokenYan {

    public static boolean token_yanzheng(String json){

        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String code= null;
        try {
            code = jsonObject.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(code.equals("0")){
                return false;
            }else if(code.equals("1")){
                return true;
            }


        return false;
    }
}
