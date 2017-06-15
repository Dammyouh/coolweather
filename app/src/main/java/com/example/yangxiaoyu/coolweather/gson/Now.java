package com.example.yangxiaoyu.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yangxiaoyu on 17-6-14.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
