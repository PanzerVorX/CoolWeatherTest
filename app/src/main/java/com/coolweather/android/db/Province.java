package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {//省级地区数据表映射类
    private  int id;//省主键
    private  String provinceName;//省名
    private int provinceCode;//省代号

    //获取/设置 成员的方法
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getProvinceName(){
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
