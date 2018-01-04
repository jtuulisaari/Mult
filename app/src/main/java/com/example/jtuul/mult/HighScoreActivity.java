package com.example.jtuul.mult;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.valueOf;

public class HighScoreActivity extends AppCompatActivity {
    int newScore;
    List<Integer> oldScores = new ArrayList<Integer>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);

        this.newScore = Integer.parseInt(message);
        this.loadOldScores(); // ---> this.oldScores
        this.addAndSortScores();

        this.saveScores();

        String[] numbers = new String[this.oldScores.size()*2];

        for(int i = 0; i < numbers.length/2; i++) {
            numbers[i*2] = valueOf(i+1) + ".";
            numbers[i*2+1] = valueOf(this.oldScores.get(i));
        }

        GridView scoresGrid = (GridView) findViewById(R.id.scores);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);
        scoresGrid.setAdapter(adapter);

        /* TextView tv = new TextView(this);
        tv.setText(valueOf("High Scores"));
        tv.setTextColor(Color.RED);
        tv.setTextSize(50);
        tv.setGravity(Gravity.RIGHT);
        // scoresLayout.addView(tv);

        List<TextView> textList = new ArrayList<TextView>(this.oldScores.size()); // https://stackoverflow.com/questions/7334751/how-to-add-multiple-textview-dynamicall-to-the-define-linearlayout-in-main-xml?rq=1
        for(int i = 0; i < this.oldScores.size(); i++)
        {

            TextView newTV = new TextView(this);
            newTV.setText(valueOf(this.oldScores.get(i)));
            newTV.setTextColor(Color.BLUE);
            newTV.setTextSize(30);
            newTV.setGravity(Gravity.RIGHT);
            TextView tvi = new TextView(this);

            tvi.setText(String.valueOf(i+1)+".");
            tvi.setTextColor(Color.BLUE);
            tvi.setTextSize(30);

            scoresLayout.addView(tvi);
            scoresLayout.addView(newTV);

            textList.add(newTV);
        } */



/*      TextView tv1 = findViewById(R.id.score1);
        TextView tv2 = findViewById(R.id.score2);
        TextView tv3 = findViewById(R.id.score3);
        tv1.setText((Integer) this.oldScores.get(0));
        tv1.setTextSize(40);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    public void saveScores() { // https://stackoverflow.com/questions/3876680/is-it-possible-to-add-an-array-or-object-to-sharedpreferences-on-android
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(this.newScore);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key2", jsonArray.toString()); // key käytössä mainissa.
        System.out.println(jsonArray.toString());
        editor.apply();
    }

    public void loadOldScores() {
        JSONArray jsonArray = null;
        // Hae tavara JSONArray:hin
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            jsonArray = new JSONArray(prefs.getString("key2", "[]"));

//            for (int i = 0; i < jsonArray.length(); i++) {
//                Log.d("your JSON Array", jsonArray.getInt(i) + "");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // https://stackoverflow.com/questions/20068852/how-to-cast-jsonarray-to-int-array
        // Extract numbers from JSON array.
        int[] test = new int[4];
        int len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            test[i] = jsonArray.optInt(i);
            this.oldScores.add(test[i]);
        }
    }


    private void addAndSortScores() {
        this.oldScores.add(this.newScore);
        Collections.reverse(this.oldScores);
    }

}
