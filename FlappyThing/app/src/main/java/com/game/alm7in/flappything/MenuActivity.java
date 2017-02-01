package com.game.alm7in.flappything;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Moaan on 5/25/2015.
 */
public class MenuActivity extends Activity {
    private TextView highScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        highScore = (TextView) findViewById(R.id.highScoreMessage);

        /**
         * Retrieve the shared preferences for "ybirdgamedata". The name is arbitrary
         * and is equivalent to a SQL table name where the key/value pairs are
         * stored. Getting the shared preferences via MODE_PRIVATE will
         * guarantee that no other application has access to the key/value
         * pairs.
         */
        SharedPreferences prefs = this.getSharedPreferences("ybirdgamedata", Context.MODE_PRIVATE);

        /**
         * Read a shared preference with key "highscore". If the key does not yet
         * exist, return 0 as the default value.
         */
        highScore.append(" " + prefs.getInt("highscore", 0));
    }

    public void startGame(View v) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);

        //startActivityForResult(intent, 0);
    }

    //below method not needed anymore. was used by startActivityForResult(intent, 0);
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK ) {
            if(data.getExtras().getBoolean("REPLAY")) {
                Intent intent = new Intent(this, PlayActivity.class);

                startActivityForResult(intent, 0);
            }
        }
    }
}
