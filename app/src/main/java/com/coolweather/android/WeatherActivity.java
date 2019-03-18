package com.coolweather.android;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import java.util.ArrayList;
import java.util.List;


public class WeatherActivity extends AppCompatActivity {

    ViewPager viewPager;
    //同时改变活动中ViewPager所装载碎片中的处理模式：ViewPager所装载碎片执行指定操作前都判断活动中的判断变量
    Boolean updateWeather=false;//更新天气判断变量（用于碎片被创建时判断）
    String weatherId1,weatherId2,weatherId3;
    List<Fragment> fragmentList= new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        viewPager=findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);//设置ViewPager保存缓存页卡的数量：ViewPager的setOffscreenPageLimit()
        Intent intent=getIntent();//获取主活动传递的Intent
        weatherId1=intent.getStringExtra("weather_id1");//获取主活动Intent中传递的天气ID
        weatherId2=intent.getStringExtra("weather_id2");
        weatherId3=intent.getStringExtra("weather_id3");
        if (weatherId1!=null){//根据获取天气ID情况加入对应碎片
            fragmentList.add(new WeatherFragmentOne());
            if (weatherId2!=null){
                fragmentList.add(new WeatherFragmentTwo());
                if (weatherId3!=null)
                    fragmentList.add(new WeatherFragmentThree());
            }
            MyViewPagerAdapter myViewPagerAdapter=new MyViewPagerAdapter(getSupportFragmentManager(),fragmentList);
            viewPager.setAdapter(myViewPagerAdapter);//ViewPager设置适配器
        }
    }
}
