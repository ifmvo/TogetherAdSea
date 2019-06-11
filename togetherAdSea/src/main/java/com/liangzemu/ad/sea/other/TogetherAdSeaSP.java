package com.liangzemu.ad.sea.other;

import android.content.Context;
import android.content.SharedPreferences;
import com.liangzemu.ad.sea.BuildConfig;

import java.util.Map;

public class TogetherAdSeaSP {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String SP_NAME = BuildConfig.APPLICATION_ID.replace(".", "_") + "_log";

    private static TogetherAdSeaSP instance;

    private TogetherAdSeaSP(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static TogetherAdSeaSP getInstance(Context context) {
        if (instance == null) {
            synchronized (TogetherAdSeaSP.class) {
                if (instance == null) {
                    instance = new TogetherAdSeaSP(context);
                    //直接使用的Application 的Context 避免内存泄漏
                }
            }
        }
        return instance;
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }


    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        editor.clear().apply();
    }


    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }


    public void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }


    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public Object get(String key) {
        return null;
    }

    public void put(String key, Object value) {

    }

    public Map<String, ?> getAllMap() {
        return sharedPreferences.getAll();
    }
}
