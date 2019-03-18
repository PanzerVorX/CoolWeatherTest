package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/*
    "suggestion":{
        "comf":{
            "txt":"......"
        },
        "cw":{
            "txt":"......"
        },
        "sport":{
            "txt":"......"
        }
    }
    comf中的txt代表舒适度，cw中的txt代表洗车建议，sport中的txt代表运动建议
 */

public class Suggestion {//当前天气相关的生活建议

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;
    }

    public class CarWash{

        @SerializedName("txt")
        public String info;
    }

    public class Sport{

        @SerializedName("txt")
        public String info;
    }
}
