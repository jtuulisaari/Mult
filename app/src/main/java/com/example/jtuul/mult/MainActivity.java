package com.example.jtuul.mult;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jtuul.mult.MESSAGE";

    int x0 = 1; int x1 = 10; int y0 = 1; int y1 = 10;    // Täysikenttä tässä vaiheessa!
    public MultiplicationGame game = new MultiplicationGame(x0, x1, y0, y1); // Tama "aloitus" naytolta
    public final String[] items = getMultiplicationIntegers();
    private CustomAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final GridView gridView;
        final TextView tv = (TextView) findViewById(R.id.tv);

        gridView = this.findViewById(R.id.mainGridView);
        gridView.setNumColumns(this.game.yLen);
        gridAdapter = new CustomAdapter(this, items);   // (this, items, this.game);
        gridView.setAdapter(gridAdapter);

        // Set an item click listener for GridView widget
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Display the selected/clicked item text and position on TextView
                tv.setText("GridView item clicked : " +selectedItem
                        + "\nAt index position : " + position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


            }
        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        String message = String.valueOf(111);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
    }



    // private MediaPlayer FXPlayer;

    public String[] getMultiplicationIntegers() {
        int n = 0;
        String[] items = new String[game.xLen*game.yLen];

        for (int i = x0; i <= x1; ++i) {
            for (int j = y0; j <= y1; ++j) {
                items[n] = String.valueOf(i*j);
                n = ++n;
            }
        }
        return items;
    }



}
