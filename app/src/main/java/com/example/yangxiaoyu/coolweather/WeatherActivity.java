package com.example.yangxiaoyu.coolweather;

import android.content.SharedPreferences;
import android.filterfw.io.TextGraphReader;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yangxiaoyu.coolweather.gson.Forecast;
import com.example.yangxiaoyu.coolweather.gson.Weather;
import com.example.yangxiaoyu.coolweather.util.HttpUtil;
import com.example.yangxiaoyu.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG ="WeatherActivity";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text= (TextView)findViewById(R.id.pm25_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        Log.d(TAG, "onCreate: "+ weatherString);
        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            String weatherId = getIntent().getStringExtra("weather_id");
            Log.d(TAG, "onCreate: weatherId =" + weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });
    }

    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+ weatherId +"&key=55e3fae1368b42af8fed4102cbb894c2";
        Log.d(TAG, "requestWeather: weatherUrl= "+weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气消息失败",Toast.LENGTH_SHORT).show();
                    }
                });
                loadBingPic();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("WeatherActivity    ", "onResponse: "+ responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                Log.d(TAG, "onResponse: " + weather);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ( weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气消息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.data_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            Log.d(TAG, "showWeatherInfo: forecastList" + weather.forecastList);
            Log.d(TAG, "showWeatherInfo: forecast = "+forecast.date);
            Log.d(TAG, "showWeatherInfo: forecast.more.info= "+forecast.more.info);
            Log.d(TAG, "showWeatherInfo: forecast.temperature.max="+forecast.temperature.max);
            Log.d(TAG, "showWeatherInfo: forecast.temperature.min="+forecast.temperature.min);
            Log.d(TAG, "showWeatherInfo: View dengyu "+view);
            Log.d(TAG, "showWeatherInfo: "+dateText);
            Log.d(TAG, "showWeatherInfo: "+comfortText);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            Log.d(TAG, "showWeatherInfo: "+aqiText);
            Log.d(TAG, "showWeatherInfo: "+pm25Text);
            Log.d(TAG, "showWeatherInfo: aqiText is "+weather.aqi.city.aqi);
            Log.d(TAG, "showWeatherInfo: pm25Text is "+weather.aqi.city.pm25);
        }
        String comfort = "舒适度"+weather.suggestion.comfort.info;
        String carWash= "汽车指数"+weather.suggestion.carWash.info;
        String sport = "运动建议"+weather.suggestion.sport.info;
        Log.d(TAG, "showWeatherInfo: comfort is "+comfort);
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }


}
