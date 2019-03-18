package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*
    "daily_forecast":[
        {
            "data":"2018-06-11",
            "cond":{
                "txt_d":"阵雨"
            },
            "temp":{
                "max":"34"
                "min":"27"
            }
        },
        {
            "data":"2018-06-12",
            "cond":{
                "txt_d":"多云"
            },
            "temp":{
                "max":"35"
                "min":"29"
            }
        },
     ......
    ]
    daily_forecast为一个数组，每个数组存储对应未来日期的天气，data代表日期，cond代表天气概况，temp中的max与min分别代表最高温与最低温
 */
public class Forecast {//未来某天的天气信息

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More{

        @SerializedName("txt_d")
        public String info;
    }
}
