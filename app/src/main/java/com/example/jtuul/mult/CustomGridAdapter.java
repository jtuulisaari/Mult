package com.example.jtuul.mult;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by jtuul on 12/5/2017.
 *
 * https://stackoverflow.com/questions/20052631/android-gridview-with-custom-baseadapter-create-onclicklistener
 */

public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    private String[] items;
    LayoutInflater inflater;
    MainActivity ma;

    public CustomGridAdapter(Context context, String[] items, MainActivity ma) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ma = ma;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        int test = 1;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }

        TextView gridTextView = (TextView) convertView.findViewById(R.id.grid_item);
        gridTextView.setBackgroundColor(ma.getColorTest(position));
        gridTextView.setText(items[position]);

        if( ma.getPositionFromxAndy() == position) {
            gridTextView.setText("O");
        }
        int answerTime = ma.game.answerTimeMatrix[ma.getxAndyFromPosition(position, "y")][ma.getxAndyFromPosition(position, "x")];
        if(answerTime <= ma.game.answerTargetTime) {
            gridTextView.setText("X");
        }
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