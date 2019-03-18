package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {//网络访问工具类

    public static void sendOkHttpRequest(String adress,okhttp3.Callback callback){//Callback为OkHttp访问方式的自带回调接口（在回调方法中处理服务器返回的数据）
        OkHttpClient client=new OkHttpClient();//创建OkHttp执行类
        Request request=new Request.Builder().url(adress).build();//创建请求对象
        client.newCall(request).enqueue(callback);//封装请求对象Request为可发送对象Call，发送并等待获取数据
    }
}
