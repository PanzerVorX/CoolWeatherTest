package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherFragmentTwo extends Fragment {//天气碎片2

    private ScrollView weatherLayout;//天气总视图
    private TextView titleCity;//头布局的县名视图
    private TextView titleUpdateTime;//头布局的更新天气时间视图
    private TextView degreeText;//当前温度视图
    private TextView weatherInfoText;//当前天气概况视图
    private LinearLayout forecastLayout;//未来天气视图
    private TextView aqiText;//AQI指数视图
    private TextView pm25Text;//PM2.5视图
    private TextView comfortText;//舒适度视图
    private TextView carWashText;//洗车建议视图
    private TextView sportText;//运动建议视图
    private ImageView bingPicImg;//背景视图
    public SwipeRefreshLayout swipeRefresh;//下拉刷新布局
    private String mWeatherId;//当前天气ID变量 （记录天气ID，避免下拉刷新时重写解析缓存中天气信息的JSON格式数据来获取天气ID）

    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment2_layout,container,false);
        //获取控件
        weatherLayout=(ScrollView)view.findViewById(R.id.weather_layout);
        titleCity=(TextView)view.findViewById(R.id.title_city);
        titleUpdateTime=(TextView)view.findViewById(R.id.title_update_time);
        degreeText=(TextView)view.findViewById(R.id.degree_text);
        weatherInfoText=(TextView)view.findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)view.findViewById(R.id.forecast_layout);
        aqiText=(TextView)view.findViewById(R.id.aqi_text);
        pm25Text=(TextView)view.findViewById(R.id.pm25_text);
        comfortText=(TextView)view.findViewById(R.id.comfort_text);
        carWashText=(TextView)view.findViewById(R.id.car_wash_text);
        sportText=(TextView)view.findViewById(R.id.sport_text);
        bingPicImg=(ImageView)view.findViewById(R.id.bing_pic_img);
        swipeRefresh=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条颜色
        swipeRefresh.setRefreshing(true);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());//SharedPreferences存储（存储天气信息的JSON格式数据作为天气信息缓存，存储图片的URL作为图片缓存）
        String weatherString=prefs.getString("weather2",null);//获取天气信息缓存
        if (((WeatherActivity)getActivity()).updateWeather){
            swipeRefresh.setRefreshing(true);
            requestWeather(((WeatherActivity)getActivity()).weatherId2);
        }
        else if (weatherString!=null){//本地存在天气信息缓存时
            Weather weather= Utility.handleWeatherResponse(weatherString);//解析缓存的天气信息的JSON格式数据并映射成Weather类
            mWeatherId=weather.basic.weatherId;//赋值当前天气ID变量
            showWeatherInfo(weather);//视图显示解析后的天气数据
            swipeRefresh.setRefreshing(false);
        }
        else {//本地无天气信息缓存时
            swipeRefresh.setRefreshing(true);
            mWeatherId=getActivity().getIntent().getStringExtra("weather_id2");//获取天气ID并赋值当前天气ID变量
            weatherLayout.setVisibility(View.INVISIBLE);//无缓存时天气界面为空数据界面，为了用户体验应将其隐藏
            requestWeather(mWeatherId);//根据天气ID向服务器获取指定地区天气信息的JSON格式数据并存入缓存，之后解析再通过视图显示
        }
        String bingPic=prefs.getString("bing_pic2",null);//获取图片的URL缓存
        if (bingPic!=null){//若本地存在图片URL缓存
            Glide.with(this).load(bingPic).into(bingPicImg);//Glide通过URL加载图片后会将图片缓存到本地，若URL不变则再次加载会直接读取图片缓存
        }
        else {
            loadBingPic();//访问图片接口（http://guolin.tech/api/bing_pic）将返回的图片URL存入本地，并通过Glide加载到背景视图来显示
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//下拉刷新的监听器

            public void onRefresh() {//下拉时会自动弹出下拉刷新进度条
                requestWeather(mWeatherId);//根据当前天气ID更新天气
            }
        });
    }

    public void requestWeather(final String weatherId){//根据天气ID向服务器获取指定地区天气信息的JSON格式数据并存入缓存，之后解析再通过视图显示

        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=6d2bc04327d9499696966e2d2e92ce96";//根据天气ID设置天气URL
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {//对天气服务器执行网络访问

            public void onResponse(Call call, Response response) throws IOException {//获取数据成功的回调方法
                final String responseText=response.body().string();//获取数据的字符串形式
                final Weather weather=Utility.handleWeatherResponse(responseText);//解析天气信息的JSON格式数据并映射为Weather后返回
                getActivity().runOnUiThread(new Runnable() {//回到主线程进行UI操作

                    public void run() {
                        if (weather!=null && "ok".equals(weather.status)){//当解析天气信息成功时

                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("weather2",responseText);//将天气信息的JSON格式数据存入本地作为天气信息缓存
                            editor.apply();//提交事务
                            mWeatherId=weather.basic.weatherId;//每次更新赋值当前天气ID变量
                            showWeatherInfo(weather);//视图显示天气信息
                        }
                        else {//当解析天气信息失败时
                            Toast.makeText(getContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//关闭下拉刷新进度条
                    }
                });
            }

            public void onFailure(Call call, IOException e) {//获取数据失败的回调方法
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {//回到主线程进行UI操作

                    public void run() {
                        Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//关闭下拉刷新进度条
                    }
                });
            }
        });

        loadBingPic();//每次更新天气信息时也应更新背景图
    }

    public void loadBingPic(){//访问图片接口（http://guolin.tech/api/bing_pic）将返回的图片URL存入本地，并通过Glide加载到背景视图来显示
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {//对图片接口执行网络访问

            public void onResponse(Call call, Response response) throws IOException {//获取数据成功时的回调方法
                final String bingPic=response.body().string();//获取图片URL
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.putString("bing_pic2",bingPic);//将图片URL存入本地
                editor.apply();//提交事务
                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        Glide.with(getActivity()).load(bingPic).into(bingPicImg);//通过Glide加载图片到背景视图来显示
                    }
                });
            }

            public void onFailure(Call call, IOException e) {//获取数据失败时的回调方法
                e.printStackTrace();
            }
        });
    }

    private void showWeatherInfo(Weather weather){//将Weather对象中的数据显示在视图上
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];//分割字符串：String的split() 将分割符两侧分开成为String[]的元素，返回值为String[] 参数①分割符（转移字符需加上//），[参数②分割份数]
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        //移除未来天布局中的之前的子项视图并重新添加更新数据后的子项视图
        forecastLayout.removeAllViews();//移除布局中的所有视图：ViewGroup的removeAllViews()
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,forecastLayout,false);//加载未来天气子项布局
            //获取子项布局的控件
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            //子项布局控件显示对应数据
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);//向布局中添加子项视图：ViewGroup的addView()
        }
        if (weather.aqi!=null){
            //天气质量视图
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.info;
        //当前天气相关生活建议视图
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//设置完视图显示的数据后显示天气总布局
        Intent intent=new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);//每次更新天气视图时都会重新开启Alarm定时任务
    }
}




