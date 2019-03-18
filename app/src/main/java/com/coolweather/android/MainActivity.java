package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.coolweather.android.util.Utility;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检测是否存在天气缓存，有则直接跳转至天气活动
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        //获取3个天气信息的缓存
        String weatherStr1=prefs.getString("weather1",null);
        String weatherStr2=prefs.getString("weather2",null);
        String weatherStr3=prefs.getString("weather3",null);
        if (weatherStr1!=null){//判断3个天气信息的缓存，若不为空则解析缓存数据加入该天气信息中的天气ID
            Intent intent=new Intent(this,WeatherActivity.class);
            String weatherId1=Utility.handleWeatherResponse(weatherStr1).basic.weatherId;//解析不为空的天气信息，并获取天气ID
            intent.putExtra("weather_id1",weatherId1);//将解析后得到的天气ID加入传递信息
            if (weatherStr2!=null){
                String weatherId2=Utility.handleWeatherResponse(weatherStr2).basic.weatherId;
                intent.putExtra("weather_id2",weatherId2);
                if (weatherStr3!=null){
                    String weatherId3=Utility.handleWeatherResponse(weatherStr3).basic.weatherId;
                    intent.putExtra("weather_id3",weatherId3);
                }
            }
            startActivity(intent);//跳转至天气活动
            finish();
        }
    }
}