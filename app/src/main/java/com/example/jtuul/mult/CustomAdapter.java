package com.example.jtuul.mult;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collection;

/**
 * Created by jtuul on 12/9/2017.
 * Täältä voisi ehkä kopioida myöhemmin metodit, joilla saadaan myös kuvat "nappuloihin", jos näin halutaan:
 * https://www.linux.com/learn/android-app-layouts-how-set-list-clickable-images-gridview
 */

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String[] items;
    LayoutInflater inflater;
    final int XLEN = 10;
    MainActivity ma;
    boolean clicked = false;

    public CustomAdapter(Context context, String[] items, MainActivity ma) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ma = ma;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cell, null);
        }

        TextView gridTextView = (TextView) convertView.findViewById(R.id.grid_item);
        gridTextView.setBackgroundColor(Color.BLUE);
        gridTextView.setText(items[position]);

        this.changeColor(position, gridTextView);

        if(clicked) {
            this.changeRowsAndColsValues(position);
        }

        //convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 80));
        return convertView;
    }

    public void setClickedTrue() {
        this.clicked = true;
    }

    private void changeRowsAndColsValues(int position) {
        int value = ma.chooseMatrix[this.getxAndyFromPosition(position, "x")][this.getxAndyFromPosition(position, "y")]; // value nykyisestä napista
        int changeValue = -value + 1;
        ma.chooseMatrix[this.getxAndyFromPosition(position, "x") ][this.getxAndyFromPosition(position, "y") ] = changeValue;
        for (int i = 0; i < XLEN; i++) {
            ma.chooseMatrix[i][getxAndyFromPosition(position, "x")] = changeValue;
            ma.chooseMatrix[getxAndyFromPosition(position, "y")][i] = changeValue;
        }
        this.clicked = false; // Tämä estää rekursion
        this.notifyDataSetChanged();
    }

    private void changeColor(int position, TextView gridTextView) {
        if(ma.chooseMatrix[getxAndyFromPosition(position, "x")][getxAndyFromPosition(position, "y")] == 1) {
            gridTextView.setBackgroundColor(Color.RED);
        }
        if(ma.chooseMatrix[getxAndyFromPosition(position, "x")][getxAndyFromPosition(position, "y")] == 0) {
            gridTextView.setBackgroundColor(Color.YELLOW);
        }
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

    public final int getxAndyFromPosition(int position, String direction){

        int xInd = (int) Math.floor(position/XLEN);
        int yInd = position - xInd * XLEN;

        int ret = 0;
        if(direction.equals("x")) { ret = xInd; }
        if(direction.equals("y")) { ret = yInd; }
        return ret;
    }

    public int getPositionFromxAndy(int x, int y) {
        int xLen = 10;
        int ret = (x - 1) + (y - 1) * xLen;
        return ret;
    }


}