package com.muvi.www.xmpe.activity;

import android.app.Application;
import android.util.Log;


import com.home.apisdk.APIUrlConstant;
import com.home.apisdk.BuildConfig;
import com.home.apisdk.apiController.SDKInitializer;




/**
 * Created by muvi on 12/6/17.
 */

public class VodApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        APIUrlConstant.BASE_URl="";


    }
}
