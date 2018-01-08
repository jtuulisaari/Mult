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
        gridTextView.setBackgroundColor(Color.GRAY);
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
        int xInd = getxAndyFromPosition(position, "x");
        int yInd = getxAndyFromPosition(position, "y");
        int value = ma.chooseMatrix[xInd][yInd]; // value nykyisestä napista
        int changeValue = -value + 1;
        // ma.chooseMatrix[xInd][yInd] = changeValue;

        for (int i = 0; i < XLEN; i++) {
            ma.chooseMatrix[i][yInd] = ma.chooseMatrix[i][yInd] + changeValue;
            ma.chooseMatrix[xInd][i] = ma.chooseMatrix[xInd][i] + changeValue;
        }
        this.clicked = false; // Tämä estää rekursion
        this.notifyDataSetChanged();
    }

    private void changeColor(int position, TextView gridTextView) {
        int xInd = getxAndyFromPosition(position, "x");
        int yInd = getxAndyFromPosition(position, "y");

        if(ma.chooseMatrix[xInd][yInd] == 0) {
            gridTextView.setBackgroundColor(Color.LTGRAY);
        }
        if(ma.chooseMatrix[xInd][yInd] == 1) {
            gridTextView.setBackgroundColor(Color.GRAY);
        }
        if(ma.chooseMatrix[xInd][yInd] >= 2) {
            gridTextView.setBackgroundColor(Color.GREEN);
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
        int xxx = 1;
        return ret; // Indeksi lähtee nollasta eikä ykkösestä kuten MultiplicationGame-luokassa.
    }

    public int getPositionFromxAndy(int x, int y) {
        int xLen = 10;
        int ret = (x - 1) + (y - 1) * xLen;
        return ret;
    }


}