package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {//选择地区的碎片

    //地区级别标识
    public static final int LEVEL_PROVINCE=0;//省级
    public static final int LEVEL_CITY=1;//市级
    public static final int LEVEL_COUNTY=2;//县级

    private ProgressDialog progressDialog;//进度对话框
    private TextView titleText;//头布局标题
    private Button backButton;//头布局按钮
    private Button goButton;//查询天气按钮
    private ListView listView;//地名列表
    private ArrayAdapter<String>adapter;//地名列表适配器
    private List<String>dataList=new ArrayList<>();//地名链表

    private List<Province> provinceList;//省链表
    private List<City> cityList;//市链表
    private List<County> countyList;//县链表
    private List<CountyItem>countyItemList=new ArrayList<>();//自定义县实体的链表

    private Province selectedProvince;//当前选中的省
    private City selectedCity;//当前选中的市

    private int currentLevel;//当前选中的地区级别

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.choose_area,container,false);//加载布局

        //获取控件
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        goButton=(Button)view.findViewById(R.id.go_button);
        listView=(ListView)view.findViewById(R.id.list_view);

        //适配地名列表
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//地名列表的点击事件

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){//若当前地区等级为省级
                    selectedProvince=provinceList.get(position);//根据省链表获取所选省并赋值当前选中省变量（selectedProvince）
                    queryCities();//查找当前省所含市（以市链表形式返回）并刷新地名列表
                }
                else if (currentLevel==LEVEL_CITY){//若当前地区等级为市级
                    selectedCity=cityList.get(position);//根据市链表获取所选市并赋值当前选中市变量（selectedCity）
                    queryCounties();//查找当前市所含县（以县链表形式返回）并刷新地名列表
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {//回退键的点击事件

            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){//若当前地区等级为县级
                    queryCities();//回退至市级并刷新列表
                }
                else if (currentLevel==LEVEL_CITY){//若当前地区等级为市级
                    queryProvinces();//回退至县级并刷新列表
                }
            }
        });
        queryProvinces();//首先查找所有省（以省链表形式返回）并刷新地名列表
    }

    private void queryProvinces(){//查找所有省
        titleText.setText("中国");//显示上级地区
        backButton.setVisibility(View.GONE);//省级没有设置可返回的上级地区，应将返回上级地区的按钮隐藏
        goButton.setVisibility(View.GONE);
        listView.invalidate();
        Log.d("AAA","001"+"");
        provinceList = DataSupport.findAll(Province.class);//在本地省级数据表查找所有省（以省链表形式返回）
        if (provinceList.size()>0){//当省数据表存在省记录时（本地有缓存）
            dataList.clear();//清空地名链表
            for (Province province:provinceList){//遍历省链表获取省名并添加到地名链表（高级for循环）
                dataList.add(province.getProvinceName());
            }
            adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
            listView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();//更新地名列表
            listView.setSelection(0);//重新定位ListView开始显示的子项：ListView的setSelection() 参数为子项角标
            currentLevel=LEVEL_PROVINCE;//更新地区级别
        }
        else {//当省数据表不存在省记录时（本地无缓存）
            //queryFromServer()访问地区服务器获取对应地区数据并存入数据表后重新执行queryProvinces()
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities(){//查找选中省所包含的市
        titleText.setText(selectedProvince.getProvinceName());//显示上级地区
        backButton.setVisibility(View.VISIBLE);//显示返回上级地区的按钮
        goButton.setVisibility(View.GONE);
        listView.invalidate();
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);//以选中省主键为约束条件查找其所包含的市（以市链表形式返回）
        if (cityList.size()>0){//当市数据表存在指定市的记录时（本地有缓存）
            dataList.clear();//清空地名链表
            for (City city:cityList){//遍历市链表获取市名并添加到地名链表
                dataList.add(city.getCityName());
            }
            adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
            listView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();//更新地名列表
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;//更新地区级别
        }
        else{//当市数据表不存在指定市记录时（本地无缓存）
            //向服务器获取指定市数据并存储
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;//根据选中省代号确定访问的URL
            queryFromServer(address,"city");
        }
    }

    private void queryCounties(){//查找选中市所包含的县
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        goButton.setVisibility(View.VISIBLE);
        listView.invalidate();//控件重新设置适配器（重新设置子项UI布局）前需让当前View无效：View的invalidate()
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);//以选中市主键为约束条件查找其所包含的县（以县链表形式返回）
        if (countyList.size()>0){//当县数据表存在指定县的记录时（本地有缓存）
            dataList.clear();
            countyItemList.clear();
            for (County county:countyList){
                CountyItem countyItem=new CountyItem(county.getCountyName(),false,county.getWeatherId());//遍历县列表将数据赋值给自定义实体类并加入实体类数据链表（自定义实体类增加选中状态属性）
                countyItemList.add(countyItem);
            }
            CountyItemAdapter adapter=new CountyItemAdapter(getContext(),R.layout.county_item,countyItemList);//创建新适配器（加载县级的子项布局）
            listView.setAdapter(adapter);//设置适配器
            currentLevel=LEVEL_COUNTY;//改变等级
            goButton.setOnClickListener(new View.OnClickListener() {//设置查询天气按钮的监听器（点击后根据选择的县查询并显示对应的天气）
                public void onClick(View v) {
                     if (currentLevel==LEVEL_COUNTY){//若当前地区等级为县级
                        //判断活动是否为指定类的实例：instanceof
                         List<CountyItem>countyItemListX=new ArrayList<>();
                        if (getActivity() instanceof MainActivity){//当为选择地区活动时
                            for (int i=0;i<countyItemList.size();i++){//遍历自定义县实体链表，将选中状态的县实体加入选中县链表
                                if (countyItemList.get(i).selected){
                                    countyItemListX.add(countyItemList.get(i));
                                }
                            }
                            if (countyItemListX.size()<4&&countyItemListX.size()>0){//当0<选中的县个数<4时
                                Intent intent=new Intent(getActivity(),WeatherActivity.class);//创建跳转到天气活动的Intenr
                                for (int i=0;i<countyItemListX.size();i++){//遍历选中县链表，将每个选中县的天气ID作为Intent传递的键值对（分别为weather_idx格式，x为选中县在县列表中的顺序）
                                    int x=i+1;
                                    intent.putExtra("weather_id"+x,countyItemListX.get(i).weatherId);
                                }
                                startActivity(intent);//跳转至天气活动并传递天气ID
                                getActivity().finish();//销毁当前活动
                            }
                            else if (countyItemListX.size()>=4) {
                                Toast.makeText(getContext(),"不能超过3个",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (getActivity() instanceof WeatherActivity){//当为显示天气活动时
                            WeatherActivity activity=(WeatherActivity)getActivity();//获取关联的天气活动
                            for (int i=0;i<countyItemList.size();i++){//遍历自定义县实体链表，将选中状态的县实体加入选中县链表
                                if (countyItemList.get(i).selected){
                                    countyItemListX.add(countyItemList.get(i));
                                }
                            }
                            if (countyItemListX.size()<4&&countyItemListX.size()>0){//遍历选中县链表，根据选中县的个数创建相应数量的碎片
                                activity.viewPager.invalidate();//使当前View无效，重新设置适配器
                                List<Fragment>fragmentList= new ArrayList<>();
                                for (int i=0;i<countyItemListX.size();i++){//遍历选中县链表，根据对应元素加入对应碎片
                                    if (i==0){
                                        fragmentList.add(new WeatherFragmentOne());
                                        activity.weatherId1=countyItemListX.get(i).weatherId;
                                    }
                                    else if (i==1) {
                                        fragmentList.add(new WeatherFragmentTwo());
                                        activity.weatherId2 = countyItemListX.get(i).weatherId;
                                    }
                                    else if (i==2){
                                        fragmentList.add(new WeatherFragmentThree());
                                        activity.weatherId3=countyItemListX.get(i).weatherId;
                                    }
                                }
                                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                switch (countyItemListX.size()){//移除本地文件中多余的键值对，方便天气活动销毁后重启程序时，主活动判断销毁前本地文件中的3个天气缓存值，传递对应天气ID给天气活动进行显示
                                    //SharedPreferences存储中对于不需要的键值对应考虑移除（妨碍判断本地缓存时）：SharedPreferences.Editor的remove() 参数为键名
                                    case 1:
                                        editor.remove("weather3");
                                        editor.remove("weather2");
                                        break;
                                    case 2:
                                        editor.remove("weather3");
                                        break;
                                }
                                editor.apply();//提交事务
                                activity.updateWeather=true;//改变更新天气判断变量，使已在天气活动中选择县后，碎片创建时从服务器获取天气信息更新天气
                                MyViewPagerAdapter myViewPagerAdapter=new MyViewPagerAdapter(activity.getSupportFragmentManager(),fragmentList);//创建适配器
                                activity.viewPager.setAdapter(myViewPagerAdapter);//适配ViewPager
                            }
                            else if (countyItemListX.size()>=4){
                                Toast.makeText(getContext(),"不能超过3个",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
        else{//当县数据表不存在指定县记录时（本地无缓存）
            //向服务器获取指定县数据并存储
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;//根据选中省代号与选中市代号确定访问URL
            queryFromServer(address,"county");
        }
    }

    private void queryFromServer(String address, final String type){//从服务器获取指定地区数据并存入本地数据表后再执行对应的查找方法
        showProgressDialog();//显示进度对话框
        HttpUtil.sendOkHttpRequest(address, new Callback() {//执行网络访问（在回调方法中处理服务器返回的数据）

            public void onResponse(Call call, Response response) throws IOException {//当网络访问成功时执行的回调方法
                Boolean result=false;//解析结果变量
                String responseText=response.body().string();//获取返回数据的字符串形式
                if ("province".equals(type)){//根据地区类型执行对应地区等级的 解析并存储 方法
                    result=Utility.handleProvinceResponse(responseText);//解析JSON格式的地区数据并组装成对应级别地区对象，通过映射存入对应级别地区数据表
                }
                else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){//当解析成功时
                    getActivity().runOnUiThread(new Runnable() {//根据地区类型执行对应地区等级的查找方法（OkHttp方式会自动开启子线程执行网络访问，而此时存在对UI（进度对话框与地名列表）的更新操作，需回到主线程中执行）

                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }
                            else if ("city".equals(type)){
                                queryCities();
                            }
                            else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            public void onFailure(Call call, IOException e) {//当网络访问异常时执行的回调方法
                getActivity().runOnUiThread(new Runnable() {

                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){//创建/显示进度条对话框
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){//隐藏进度对话框
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
