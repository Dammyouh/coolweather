package com.example.yangxiaoyu.coolweather.gson;

import android.provider.Settings;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yangxiaoyu on 17-6-14.
 */

public class Forecast {

    public String date ;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public  String info;
    }
}
