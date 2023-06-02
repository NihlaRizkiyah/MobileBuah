package com.project.pointofsaleproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static SharedPrefManager mSharedPrefManager;
    private static Context mContext;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences ;

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String TAG_STATUS = "status";
    public static final String TAG_ID = "id", TAG_NOMOR = "telepon",
            TAG_NAMA = "nama" ,TAG_USERNAME="username", TAG_PASSWORD="pass";;

    public SharedPrefManager(Context context){
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        editor =sharedPreferences.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if(mSharedPrefManager==null){
            mSharedPrefManager = new SharedPrefManager(context);
        }
        return mSharedPrefManager;
    }

    public boolean isLogin(){
        SharedPreferences sharedPreferences= mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(TAG_STATUS, 0) == 1){
            return true;
        }
        return false;
    }

    public String getValue(String key){
        SharedPreferences sharedPreferences= mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(key, null);
        return token;
    }

    public String getAkun(String key) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public int getStatus(String key) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public void setAkun(String key, String value){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int login(String id, String nomor, String nama, String username, String password
    ) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TAG_STATUS, 1);
        editor.putString(TAG_ID, id);
        editor.putString(TAG_NOMOR, nomor);
        editor.putString(TAG_NAMA, nama);
        editor.putString(TAG_USERNAME, username);
        editor.putString(TAG_PASSWORD, password);
        editor.apply();
        return 1;
    }

    public void reset(){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(my_shared_preferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
