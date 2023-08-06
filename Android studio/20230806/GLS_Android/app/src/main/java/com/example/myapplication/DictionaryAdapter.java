package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionaryAdapter extends BaseAdapter {
    private Context context;
    private List<String> keys;
    private Map<String, List<Double>> data;

    public DictionaryAdapter(Context context, Map<String, List<Double>> data) {
        this.context = context;
        this.keys = new ArrayList<>(data.keySet());
        this.data = data;
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(keys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        String key = keys.get(position);
        List<Double> value = data.get(key);
        String displayText = key + ": " + String.valueOf(value);
        textView.setText(displayText);

        return convertView;
    }
}
