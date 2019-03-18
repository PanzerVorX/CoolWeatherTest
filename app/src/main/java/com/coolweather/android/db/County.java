package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {//县级地区数据表映射类

    private int id;//县主键
    private String countyName;//县名
    private String weatherId;//天气ID
    private int cityId;//所属市主键 （形成所属关系）

    //获取/设置 成员的方法
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
