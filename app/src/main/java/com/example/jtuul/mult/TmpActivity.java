package com.example.jtuul.mult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jtuul on 1/4/2018.
 */

public class TmpActivity extends AppCompatActivity {
    private static final String EXTRA_MESSAGE = "test extra";
    GameActivity gameActivity = new GameActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, HighScoreActivity.class);
        String message = String.valueOf(100);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
