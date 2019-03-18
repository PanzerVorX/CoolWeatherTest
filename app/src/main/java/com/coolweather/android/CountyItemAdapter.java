package com.coolweather.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class CountyItemAdapter extends ArrayAdapter<CountyItem> {//县列表子项的适配器

    public CountyItemAdapter(Context context, int resource,List<CountyItem> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final CountyItem countyItem=getItem(position);
        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.county_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.countyName=convertView.findViewById(R.id.county_name);
            viewHolder.checkBox=convertView.findViewById(R.id.check_box);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.countyName.setText(countyItem.countyName);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                countyItem.selected=isChecked;
            }
        });
        viewHolder.checkBox.setChecked(countyItem.selected);
        return convertView;
    }

    class ViewHolder{
        TextView countyName;
        CheckBox checkBox;
    }
}
