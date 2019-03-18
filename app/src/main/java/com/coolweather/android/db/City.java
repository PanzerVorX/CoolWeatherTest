package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {//市级地区数据表映射类
        private int id;//市主键
        private String cityName;//市名
        private int cityCode;//市代号
        private int provinceId;//所属省主键 （形成所属关系）

        //获取/设置 成员的方法
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getCityCode() {
            return cityCode;
        }

        public void setCityCode(int cityCode) {
            this.cityCode = cityCode;
        }

        public int getProvinceId() {
            return provinceId;
        }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
