package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {//解析JSON数据工具类

    //解析JSON格式的地区数据并组装成对应级别地区对象，通过映射存入对应级别地区数据表
    public static boolean handleProvinceResponse(String response){//解析省级地区数据并存入省级地区数据表
        if (!TextUtils.isEmpty(response)){
            try
            {
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//将省对象映射成省级数据表记录并存储
                }
                return true;//返回解析结果
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){//解析市级地区数据并存入市级地区数据表
        if (!TextUtils.isEmpty(response)){
            try
            {
                JSONArray allCities=new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();//将市对象映射成市级数据表记录并存储
                }
                return true;//返回解析结果
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static boolean handleCountyResponse(String response,int cityId){//解析县级地区数据并存入县级地区数据表
        if (!TextUtils.isEmpty(response)){
            try
            {
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();//将县对象映射成县级数据表记录并存储
                }
                return true;//返回解析结果
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    //使用JSONObject方式将JSON格式天气数据解析成符合映射条件的天气数据，再通过GSON方式将天气数据映射成Weather实体类并返回
    public static Weather handleWeatherResponse(String response){//解析多层次的JSON格式数据：先使用JSONObject方式将其解析成符合映射条件的数据，再通过GSON方式将天气数据映射成对应实体类
        try
        {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");//获取JSON对象中的包含的JSON数组：JSONObject的getJSONArray() 参数为数组名 （用于JSON格式数据中，{}里含有[]的情况）
            String weatherContent=jsonArray.getJSONObject(0).toString();//获取JSON数组中的JSON对象元素：JSONArray的getJSONObject() 参数为元素角标
            //JSON数组/对象转化为字符串形式：JSONArray/JSONObject的toString()
            return new Gson().fromJson(weatherContent,Weather.class);//GSON方式将数据映射成对应实体类：Gson()的fromJson()参数①JSON格式数据 参数②映射类
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
