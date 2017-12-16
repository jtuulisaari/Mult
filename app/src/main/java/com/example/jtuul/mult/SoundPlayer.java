package com.example.jtuul.mult;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.jtuul.mult.R;

public class SoundPlayer extends Activity {
    private static SoundPool soundPool;
    // private SoundPool soundPool;
    private int soundID;
    boolean loaded = false;
    static int[] sm;
    static AudioManager amg;

    public Activity activity; // https://stackoverflow.com/questions/5339941/android-how-to-use-getapplication-and-getapplicationcontext-from-non-activity

    public SoundPlayer (Activity act) { // Konstruktori
        this.activity = act;
        this.initSound();
    }

    private void initSound() { // https://stackoverflow.com/questions/17069955/play-sound-using-soundpool-example

        int maxStreams = 1; // http://www.vogella.com/tutorials/AndroidMedia/article.html#android-sound-and-media
        Context mContext = activity.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        // Load the sound
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                loaded = true;
            }
        });

        sm = new int[3];
        // fill your sounds
        sm[0] = soundPool.load(mContext, R.raw.test, 1);
        sm[1] = soundPool.load(mContext, R.raw.test, 1);
        sm[2] = soundPool.load(mContext, R.raw.test, 1);
        amg = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    static void playSound(int sound) {
        soundPool.play(sm[sound], 1, 1, 1, 0, 1f);
    }

    public final void cleanUpIfEnd() {
        sm = null;
        soundPool.release();
        soundPool = null;
    }
}