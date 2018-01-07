package com.example.jtuul.mult;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jtuul.mult.MESSAGE";

    int x0 = 1; int x1 = 10; int y0 = 1; int y1 = 10;    // Täysikenttä tässä vaiheessa!
    int xLen = x1-x0+1; int yLen = y1-y0+1;
    public int[][] chooseMatrix = new int[xLen][xLen];

    public MultiplicationGame gmGrid = new MultiplicationGame(x0, x1, y0, y1); // Tama "aloitus" nayttöä varten
    public final String[] items = this.getMultiplicationIntegers();
    private CustomAdapter gridAdapter;

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent object holds X-Y values
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            String text = "You click at x = " + event.getX() + " and y = " + event.getY();
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
        return super.onTouchEvent(event);
    } */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        gmGrid.initializeMatrix(this.chooseMatrix, 0); // alkuarvo on nolla, ei valintaa.

        final GridView gridView;
        final TextView tv = (TextView) findViewById(R.id.tv);
        gridView = this.findViewById(R.id.mainGridView);
        gridView.setNumColumns(this.gmGrid.yLen);
        gridAdapter = new CustomAdapter(this, items, this);   // (this, items, this.game);
        gridView.setAdapter(gridAdapter);

        // Set an item click listener for GridView widget
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private int xAndyFromPosition;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the GridView selected/clicked item text
                String selectedItem = parent.getItemAtPosition(position).toString();

                // int value = chooseMatrix[this.getxAndyFromPosition(position, "x") ][this.getxAndyFromPosition(position, "y") ];
                // chooseMatrix[this.getxAndyFromPosition(position, "x") ][this.getxAndyFromPosition(position, "y") ]; = value - Math.abs(value) + 1;

                // chooseMatrix[this.getxAndyFromPosition(position, "x") ][this.getxAndyFromPosition(position, "y") ] = 1;
                // Display the selected/clicked item text and position on TextView
                tv.setText("GridView item clicked : " + selectedItem
                        + "\nAt index position : " + position);

                gridAdapter.setClickedTrue();
                gridAdapter.getView(position, view, parent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
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

    public String[] getMultiplicationIntegers() {
        int n = 0;
        String[] items = new String[gmGrid.xLen*gmGrid.yLen];

        for (int i = x0; i <= x1; ++i) {
            for (int j = y0; j <= y1; ++j) {
                items[n] = String.valueOf(i*j);
                n = ++n;
            }
        }
        return items;
    }

}
