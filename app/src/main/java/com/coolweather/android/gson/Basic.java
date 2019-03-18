package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*  天气服务器返回的JSON总数据大致如下
    {
        "HeWeather": [
            {
                "status": "ok",
                "basic": {},
                "aqi": {},
                "now": {},
                "suggestion": {},
                "daily_forecast": []
            }
        ]
    }

    其中天气信息相关的5部分basic、aqi、now、suggestion、daily_forecast有各自的具体内容
 */
/*
    "basic":{
        "city":"苏州",
        "id":"CN101190401",
        "update":{
            "loc":"2018-06-10 16:38"
        }
    }
    city表示县名，id表示县对应的天气id，update中的loc表示更新天气的时间
 */

public class Basic {//本次天气网络请求的相关信息

    @SerializedName("city")//GSON方式解析JSON数据时指定不同名变量间产生映射关系：通过@SerializedName注释
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{//GSON方式解析JSON数据时映射类中成员所处层次应与JSON数据中成员所处层次相对应

        @SerializedName("loc")
        public String updateTime;
    }
}
