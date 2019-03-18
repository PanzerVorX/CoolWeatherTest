package com.coolweather.android;

public class CountyItem {//自定义县实体（加入选中状态成员）
    String countyName;
    String weatherId;
    Boolean selected=false;//选中状态成员
    CountyItem(String countyName,boolean selected,String weatherId){
        this.countyName=countyName;
        this.selected=selected;
        this.weatherId=weatherId;
    }
}
