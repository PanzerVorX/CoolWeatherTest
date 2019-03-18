package com.coolweather.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {//ViewPager的碎片专属适配器

    WeatherFragmentOne weatherFragmentOne;
    List<Fragment>fragmentList;

    public MyViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList){
        super(fragmentManager);
        this.fragmentList=fragmentList;
    }

    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public int getCount() {
        return fragmentList.size();
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (position==0){
            weatherFragmentOne = (WeatherFragmentOne)object;
        }
    }
}


















