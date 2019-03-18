package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {//后台定时更新缓存服务

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather("weather1");//更新天气缓存
        updateWeather("weather2");
        updateWeather("weather3");
        updateBingPic("bing_pic1");
        updateBingPic("bing_pic2");
        updateBingPic("bing_pic3");
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;//延时时间（8小时的毫秒数）
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;//触发任务时间=开始计时时间+延时时间
        Intent i=new Intent(this,AutoUpdateService.class);//任务跳转的Intent
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);//防止触发重复开启多次Alarm任务：每次开启Alarm任务前取消指定PendingIntent的Alarm定时服务：AlarmManager的cancel() 参数为PendingIntent
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather(final String weatherX){//更新天气缓存
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString(weatherX,null);//获取天气信息缓存
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);//解析天气信息缓存获取天气ID
            final String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=6d2bc04327d9499696966e2d2e92ce96";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {

                public void onResponse(Call call, Response response) throws IOException {//从天气服务器获取数据成功时的回调方法
                    String responseText=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(responseText);
                    if (weather!=null&&"ok".equals(weather.status)){//当获取的天气信息解析成功时（天气数据无异常时）
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString(weatherX,responseText);//将获取的JSON格式天气信息存入本地天气信息缓存
                        editor.apply();//提交事务
                    }
                }

                public void onFailure(Call call, IOException e) {//从天气服务器获取数据失败时的回调方法
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateBingPic(final String bingPicX){//更新图片URL缓存
        String requestBingPic="http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                public void onResponse(Call call, Response response) throws IOException {//当获取图片URL成功时的回调方法
                    String bingPic=response.body().string();
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString(bingPicX,bingPic);//将获取的图片URL存入本地图片URL缓存
                    editor.apply();
                }

                public void onFailure(Call call, IOException e) {//当获取图片URL失败时的回调方法
                    e.printStackTrace();
                }
            });
    }
}
