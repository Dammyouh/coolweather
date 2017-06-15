package com.example.yangxiaoyu.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yangxiaoyu on 17-6-14.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
