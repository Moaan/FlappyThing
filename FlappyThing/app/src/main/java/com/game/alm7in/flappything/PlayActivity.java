package com.game.alm7in.flappything;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Display;
import android.view.Window;

import java.util.ArrayList;

/**
 * Created by Moaan on 5/26/2015.
 */
public class PlayActivity extends Activity {
    private static PlayView playView;
    private ArrayList inGameStrings;
    private Intent startingIntent, restartIntent;
    private static Boolean thisIsFirstRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // get screen size.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int viewWidth = size.x;
        int viewHeight = size.y;

        /*if(thisIsFirstRun) {
            playView = new PlayView(this, viewWidth, viewHeight);
            setContentView(playView);
            thisIsFirstRun = false;
        }*/

        playView = new PlayView(this, viewWidth, viewHeight);
        setContentView(playView);

        //get intent that called this activity
        startingIntent = getIntent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playView.resume();
    }

    public void endGame() { //called after the game over screen
        /*replayBundle = new Bundle();
        onSaveInstanceState(replayBundle);
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("bundle", temp_bundle);
        //startActivity(intent);
        startingIntent.putExtra("bundle", replayBundle);

        startActivity(startingIntent);*/

        //playView = null;
        finish();
        //startActivity(startingIntent);

    }

    public void restartGame() {
        restartIntent = new Intent();
        restartIntent.putExtra("REPLAY", true);
        setResult(RESULT_OK, restartIntent);
        finish();
    }

    /*
    @Override
    public void onSaveInstanceState(Bundle replayGameBundle) {
        replayGameBundle.put

        super.onSaveInstanceState(outState);
    }*/
}
