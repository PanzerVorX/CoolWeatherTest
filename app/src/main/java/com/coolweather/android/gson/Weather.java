package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {//JSON格式数组中一个元素含有多个JSON格式对象，可建立每个JSON格式对象的映射类作为的元素类的成员

    public String status;//请求状态
    public Basic basic;//本次天气网络请求的相关信息
    public AQI aqi;//当前天气质量
    public Now now;//当前天气信息
    public Suggestion suggestion;//当前天气相关的生活建议

    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;//未来几天的天气信息

}
