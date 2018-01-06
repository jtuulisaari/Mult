package com.example.jtuul.mult;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by jtuul on 12/9/2017.
 * Täältä voisi ehkä kopioida myöhemmin metodit, joilla saadaan myös kuvat "nappuloihin", jos näin halutaan:
 * https://www.linux.com/learn/android-app-layouts-how-set-list-clickable-images-gridview
 */

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String[] items;
    LayoutInflater inflater;

    public CustomAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView , ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }

        TextView gridTextView = (TextView) convertView.findViewById(R.id.grid_item);
        gridTextView.setBackgroundColor(Color.BLUE);
        gridTextView.setText(items[position]);

        //convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 80));
        return convertView;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}