package com.example.yangxiaoyu.coolweather.gson;

import android.text.style.SuggestionRangeSpan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yangxiaoyu on 17-6-14.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
