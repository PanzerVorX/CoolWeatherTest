package com.coolweather.android.gson;

/*
    "aqi":{
        "city":{
            "aqi":"44",
            "pm25":"13"
        }
    }
    city中aqi代表当AQI指数，pm25代表pm2.5指数
 */
public class AQI {//当前天气质量

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
