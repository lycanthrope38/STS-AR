package com.company.sts_ar.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.company.sts_ar.di.ApplicationScope;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thuongle on 7/17/16.
 */
@Singleton
public class SharedVariables {

    public static final String PREF_KEY_CONFIG = "stsar_cfg";
    public static final String KEY_BASE_URL = "key_base_url";


    private final SharedPreferences mSharedPreferences;

    @Inject
    public SharedVariables(@ApplicationScope Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_KEY_CONFIG, Context.MODE_PRIVATE);
    }

    @NonNull
    public final String getBaseUrl() {
        return mSharedPreferences.getString(KEY_BASE_URL, "");
    }

    public void putBaseUrl(String url){
        mSharedPreferences.edit().putString(KEY_BASE_URL, url).apply();
    }

}

