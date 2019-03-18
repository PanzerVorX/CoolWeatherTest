package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*
    "now":{
        "tmp":"29",
        "cond":{
        "txt":"阵雨"
        }
    }
    tmp代表当前温度，cond中的txt代表当前天气概况
 */
public class Now {//当前天气信息

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;
    }
}
