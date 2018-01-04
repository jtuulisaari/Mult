package com.example.jtuul.mult;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.Random;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jtuul.mult.MESSAGE";

    //private int x0; private int x1; private int y0; private int y1;
    // game; // Tama "aloitus" naytolta
    int x0 = 2; int x1 = 3; int y0 = 2; int y1 = 3;
    public MultiplicationGame game = new MultiplicationGame(x0, x1, y0, y1); // Tama "aloitus" naytolta
    int xLen = this.game.answerTimeMatrix.length;
    int yLen = this.game.answerTimeMatrix[0].length;

    private Button[] buttons = new Button[4];
    private CustomGridAdapter gridAdapter;
    float greenToRedPortion;
    private TextView xTimesy;
    private TextView hint;
    private boolean firstRound = true;
    public final String[] items = getMultiplicationIntegers();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gameEnded(); // Poista tämä jos haluat pelata, tämä tehty testauksen takia (high score)

        new Random().setSeed(1);
        game.setSoundPlayer(this);

        this.xTimesy = findViewById(R.id.xTimesy);
        this.hint = findViewById(R.id.hint);

        // x0 = 1; x1 = 10; y0 = 1; y1 = 10; xLen = x1-x0+1; x1 = y1-y0+1;

        if(firstRound) {
            this.firstRound();
        } else {
            firstRound = false;
        }

        GridView gridView;
        gridView = this.findViewById(R.id.myGridView);
        gridView.setNumColumns(this.game.yLen);
        gridAdapter = new CustomGridAdapter(MainActivity.this, items, this);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //text.setText((String) (gridView.getItemAtPosition(position)));
                // Log.i("ITEM_CLICKED", "" + (String) (gridView.getItemAtPosition(position)));
            }
        });

        // SharedPreferences.Editor editor = getSharedPreferences("gameState", MODE_PRIVATE).edit();
    }

    @Override
    public void onStart(){
        super.onStart();
        // this.game.answerTimeMatrix = this.loadArray(this.game.answerTimeMatrix);
        // Ladataan edellinen pelitilanne muistista. <--- null tilannetta ei ole käsitelty, mutta siihen ei periaatteessa pitäisi joutua
    }

    @Override
    public void onResume(){
        super.onResume();
        this.gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop(){
        super.onStop();
        this.saveArray(game.answerTimeMatrix);
    }

    public void firstRound() {
        this.setAnswerButtonsText();
        this.game.newRound(false);
        this.setxTimesyText();
        this.setAnswerButtonsText();
    }

    public void newRound(int answer) {
        game.answer = answer;
        game.evaluateAnswer();
        if(game.checkEndCondition()) this.gameEnded();
        this.setHintText();
        this.game.newRound(true);
        // this.checkGameEnded();

        this.gridAdapter.notifyDataSetChanged();
        this.setxTimesyText();
        this.setAnswerButtonsText();
    }

    public void gameEnded() {
        // if(this.game.gameEnded()) { // Jos loppu niin sisään
            int highScore = this.game.getHighScore();
            Intent intent = new Intent(this, HighScoreActivity.class);
            String message = String.valueOf(highScore);
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        // }
    }

    public void onClickBtn1(View v) {
        // Toast.makeText(this, "Clicked on Button1", Toast.LENGTH_LONG).show();
        this.newRound(Integer.parseInt(this.buttons[0].getText().toString()));
    }

    public void onClickBtn2(View v) {
        // Toast.makeText(this, "Clicked on Button2", Toast.LENGTH_LONG).show();
        this.newRound(Integer.parseInt(this.buttons[1].getText().toString()));
    }
    public void onClickBtn3(View v) {
        // Toast.makeText(this, "Clicked on Button3", Toast.LENGTH_LONG).show();
        this.newRound(Integer.parseInt(this.buttons[2].getText().toString()));
    }
    public void onClickBtn4(View v) {
        // Toast.makeText(this, "Clicked on Button4", Toast.LENGTH_LONG).show();
        this.newRound(Integer.parseInt(this.buttons[3].getText().toString()));
    }

    public Integer getColorTest(int position) {
        int x = this.getxAndyFromPosition(position, "x");
        int y = this.getxAndyFromPosition(position, "y");

        double tmp = (1 - ((double)this.game.answerTimeMatrix[y][x] - game.answerTargetTime) /  ((double) game.initialAnswerTime - game.answerTargetTime));
        greenToRedPortion = (float) Math.max(Math.min(tmp, 1), 0);
        int color = this.getTrafficlightColor(greenToRedPortion);

        // int color = Color.RED;*/
        return color;
    }

    int getTrafficlightColor(double value){
        return android.graphics.Color.HSVToColor(new float[]{(float)value*120f,1f,1f});
    }

    public int getPositionFromxAndy(){
        int xLen = this.game.xLen;
        int ret = (game.x - 1) + (game.y - 1) * xLen;
        if(ret < 0 | ret > 99) this.catchFun();
        return ret;
    }

    public int getxAndyFromPosition(int position, String direction){
        if(position > 99 | position < 0) this.catchFun();
        int xLen = this.game.xLen;
        int xInd = (int) Math.floor(position/xLen);
        int yInd = position - xInd * xLen;

        int ret = 0;
        if(direction.equals("x")) { ret = xInd; }
        if(direction.equals("y")) { ret = yInd; }

        if(ret < 0 | ret > 9) this.catchFun();

        return ret;
    }

    private void setText(String str, TextView textView) {
        textView.setText(str);
        textView.setTextSize(40);
    }

    public int getIntFromColor(float Red, float Green, float Blue){
        // https://stackoverflow.com/questions/18022364/how-to-convert-rgb-color-to-int-in-java
        int R = Math.round(255 * Red);
        int G = Math.round(255 * Green);
        int B = Math.round(255 * Blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;
        return 0xFF000000 | R | G | B;
    }

    private void setxTimesyText() {
        String str = game.getxTimesYStr() + " = ?";
        this.xTimesy.setText(str);
        this.xTimesy.setTextSize(40);
    }

    private void setHintText() {

        hint.setTextColor(Color.GRAY);
        hint.setText(String.valueOf( (int) this.game.points));
        hint.setTextSize(40);

        Animation animation=new ScaleAnimation(5,1,5,1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // https://stackoverflow.com/questions/7677250/animation-of-textviews-text-size & https://stackoverflow.com/questions/7414065/android-scale-animation-on-view
        animation.setDuration(1000);
        // animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(0);
        hint.startAnimation(animation);
    }

    private void setAnswerButtonsText() {
        Integer[] wa = game.getAnswerOptions();
        for (int i = 0; i <= 3; ++i) {
            String buttonStr = "button" + (i + 1);
            int resIDmt = getResources().getIdentifier(buttonStr, "id", getPackageName());
            this.buttons[i] = findViewById(resIDmt);
            String showAnswer = String.valueOf(wa[i]);
            this.buttons[i].setText(showAnswer);
        }
    }

    private void catchFun() {
        int sxxx = 1; // Tähän myöhemmin error catch
    }

    public void saveArray(int[][] array) { // https://stackoverflow.com/questions/3876680/is-it-possible-to-add-an-array-or-object-to-sharedpreferences-on-android
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < this.game.answerTimeMatrix.length; ++i) {
            for (int k = 0; k < this.game.answerTimeMatrix[0].length; ++k) {
                jsonArray.put(this.game.answerTimeMatrix[i][k]);
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", jsonArray.toString());
        System.out.println(jsonArray.toString());
        editor.apply();
    }

    public int[][] loadArray(int[][] gameAnswerTimeMatrix) {
        JSONArray jsonArray2 = null;
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            jsonArray2 = new JSONArray(prefs.getString("key", "[]"));
            for (int i = 0; i < jsonArray2.length(); i++) {
                Log.d("your JSON Array", jsonArray2.getInt(i) + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // https://stackoverflow.com/questions/20068852/how-to-cast-jsonarray-to-int-array
        // Create an int array to accomodate the numbers.
        int[][] matrix = gameAnswerTimeMatrix; // new int[this.game.answerTimeMatrix.length][this.game.answerTimeMatrix[0].length];

        // Extract numbers from JSON array.
        int n = 0;
        for (int i = 0; i < this.game.answerTimeMatrix.length; ++i) {
            for (int k = 0; k < this.game.answerTimeMatrix[0].length; ++k) {
                int xInd = this.getxAndyFromPosition(n, "x");
                int yInd = this.getxAndyFromPosition(n, "y");
                matrix[xInd][yInd] = jsonArray2.optInt(n);
                n = n + 1;
            }
        }
        if(matrix.length == this.game.answerTimeMatrix.length & matrix[0].length == game.answerTimeMatrix[0].length)
            this.game.answerTimeMatrix = matrix;

        return matrix;
    }
}