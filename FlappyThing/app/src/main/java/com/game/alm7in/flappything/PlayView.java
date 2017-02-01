package com.game.alm7in.flappything;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game.alm7in.flappything.GameObjects.Background;
import com.game.alm7in.flappything.GameObjects.Bird;
import com.game.alm7in.flappything.GameObjects.Obstacles;

import java.util.ArrayList;

/**
 * Created by Moaan on 5/26/2015.
 */
public class PlayView extends SurfaceView implements Runnable {
    private PlayActivity activity;
    private Boolean readyToRun = false, gameStarted = false, gameOver = false;
    private Thread gameLoopThread;
    private int sleepTime = 15;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap backgroundSheet, playerFrames[], sharpBush;
    private int viewWidth, viewHeight;

    //objects in game
    private Background background;
    private Obstacles obstacles;
    private Bird player;
    private GameMenu gameMenu;

    public PlayView(Context context, int viewWidth, int viewHeight) {
        super(context);
        activity = (PlayActivity) context;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        holder = getHolder();

        backgroundSheet = BitmapFactory.decodeResource(getResources(), R.drawable.darkmountains);
        playerFrames = new Bitmap[5];
        playerFrames[0] = BitmapFactory.decodeResource(getResources(), R.drawable.frame1);
        playerFrames[1] = BitmapFactory.decodeResource(getResources(), R.drawable.frame2);
        playerFrames[2] = BitmapFactory.decodeResource(getResources(), R.drawable.frame3);
        playerFrames[3] = BitmapFactory.decodeResource(getResources(), R.drawable.frame4);
        playerFrames[4] = BitmapFactory.decodeResource(getResources(), R.drawable.framefall);
        sharpBush = BitmapFactory.decodeResource(getResources(), R.drawable.sharp_bush);

        gameMenu = new GameMenu(context);


    }

    @Override
    public void run() {
        background = new Background(backgroundSheet, viewWidth, viewHeight);
        player = new Bird(playerFrames, viewWidth, viewHeight);
        obstacles = new Obstacles(player, sharpBush, viewWidth, viewHeight);

        while(readyToRun) {

            if(!holder.getSurface().isValid()) continue;
            canvas = holder.lockCanvas();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(gameOver) {
                gameMenu.drawGameOver(canvas);
            } else if(gameStarted) {

                background.update();
                player.update();
                background.draw(canvas);
                player.draw(canvas);

                switch (obstacles.update()) {
                    case 1:
                        gameMenu.score++;
                    case 0:
                        obstacles.draw(canvas);
                        gameMenu.drawHUD(canvas);
                        break;
                    case 2:
                        gameOver = true;
                        gameMenu.resolveHighScore();
                        player.die();
                        break;
                }
            } else {
                gameMenu.drawStartScreen(canvas);
            }


            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //check if paused position is pressed...
        if(!gameStarted) gameStarted = true;
        if(gameOver) {
            if(gameMenu.replayPressed(event.getX(), event.getY())) {
                endGame();
            }
        }else if((event.getX() > viewWidth/2) && (event.getY() < viewHeight/2) && (event.getDownTime() > 3000)) {
            //obstacles.zoom(); easter egg attempt
        }else {
            player.jump();
        }
        return super.onTouchEvent(event);
    }

    public void pause() {
        readyToRun = false;

        try {
            gameLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        readyToRun = true;
        gameLoopThread = new Thread(this);
        gameLoopThread.start(); //start calls run
    }

    public void endGame() {
        //pause();
        //activity.endGame();

        //activity.restartGame();
        //above not needed no more.
        gameStarted = gameOver = false;
        player.revive();
        background.restart();
        obstacles.reset();
    }


    //inner class for all in-game text and menu items
    class GameMenu {
        private Paint menuColor, textPaintScore, textPaintHighScore, textPaintSmall, textPaintLarge, buttonPaint;
        private ArrayList<String> inGameText;
        private int score = 0, highscore = 0;
        private RectF gameOverRect, replayRect;


        private int gameOverTextHeight = viewHeight*4/12;
        private int scoreMessageTextHeight = gameOverTextHeight + viewWidth/16 + viewHeight/50;
        private int scoreTextHeight = scoreMessageTextHeight + viewWidth/12 + viewHeight/70;
        private int highscoreMessageTextHeight = scoreTextHeight + viewWidth/16 + viewHeight/50;
        private int highscoreTextHeight = highscoreMessageTextHeight + viewWidth/12 + viewHeight/70;
        private int playButtonText = viewHeight*16/24 + viewWidth/48;

        //game over menu variables
        private int replayPositionW = viewWidth/3, replayPositionH = viewHeight*2/3;

        public GameMenu(Context context) {
            menuColor = new Paint();
            textPaintScore = new Paint();
            textPaintSmall = new Paint();
            textPaintLarge = new Paint();
            textPaintHighScore = new Paint();
            buttonPaint = new Paint();

            menuColor.setARGB(255,163,72,11);
            menuColor.setTextAlign(Paint.Align.CENTER);
            menuColor.setShadowLayer(2f, 1.5f, 1.5f, Color.BLACK );

            textPaintScore.setARGB(255,255,255,255);
            textPaintScore.setTextAlign(Paint.Align.CENTER);
            textPaintScore.setShadowLayer(3.5f, 1.5f, 1.5f, Color.BLACK );
            textPaintScore.setTextSize(viewWidth/12);
            textPaintScore.setTypeface(Typeface.DEFAULT_BOLD);

            textPaintHighScore.setARGB(255,232,208,28);
            textPaintHighScore.setTextAlign(Paint.Align.CENTER);
            textPaintHighScore.setShadowLayer(3.5f, 1.5f, 1.5f, Color.BLACK );
            textPaintHighScore.setTextSize(viewWidth/12);
            textPaintHighScore.setTypeface(Typeface.DEFAULT_BOLD);

            textPaintSmall.setARGB(255,255,255,255);
            textPaintSmall.setTextAlign(Paint.Align.CENTER);
            textPaintSmall.setShadowLayer(2f, 1.5f, 1.5f, Color.BLACK );
            textPaintSmall.setTextSize(viewWidth/16);
            textPaintSmall.setTypeface(Typeface.SANS_SERIF);

            textPaintLarge.setARGB(255,255,255,255);
            textPaintLarge.setTextAlign(Paint.Align.CENTER);
            textPaintLarge.setShadowLayer(2f, 2.5f, 2.5f, Color.argb(255,219,46,26) );
            textPaintLarge.setTextSize(viewWidth/12);
            textPaintLarge.setTypeface(Typeface.SERIF);

            buttonPaint.setARGB(255,219,46,26);
            buttonPaint.setTextAlign(Paint.Align.CENTER);
            buttonPaint.setShadowLayer(2f, 2.5f, 2.5f, Color.BLACK );
            buttonPaint.setTextSize(viewWidth/12);

            inGameText = new ArrayList();
            inGameText.add(0, context.getResources().getString(R.string.game_over_message));
            inGameText.add(1, context.getResources().getString(R.string.score_message));
            inGameText.add(2, context.getResources().getString(R.string.instruction));
            inGameText.add(3, context.getResources().getString(R.string.play_button));
            inGameText.add(4, context.getResources().getString(R.string.highScore_message));

            gameOverRect = new RectF(viewWidth/8, viewHeight*3/12, viewWidth*7/8, viewHeight*9/12);
            replayRect = new RectF(viewWidth/3, viewHeight*15/24, viewWidth*2/3, viewHeight*17/24);
        }

        public void drawStartScreen(Canvas canvas) {
            background.draw(canvas);
            canvas.drawText(inGameText.get(2), viewWidth/2, viewHeight/2, textPaintSmall);
            player.draw(canvas);
        }

        public void drawHUD(Canvas canvas) {
            //canvas.drawText(""+score, viewWidth/2 - 2, viewHeight/6 - 2, textPaintB);
            canvas.drawText(""+score, viewWidth/2, viewHeight/6, textPaintScore);
            //uh, pause button ?

        }

        public void resolveHighScore() {
            /**
             * Retrieve the shared preferences for "ybirdgamedata". The name is arbitrary
             * and is equivalent to a SQL table name where the key/value pairs are
             * stored. Getting the shared preferences via MODE_PRIVATE will
             * guarantee that no other application has access to the key/value
             * pairs.
             */
            SharedPreferences prefs = activity.getSharedPreferences("ybirdgamedata", Context.MODE_PRIVATE);

            /**
             * Read a shared preference with key "highscore". If the key does not yet
             * exist, return 0 as the default value.
             */
            highscore = prefs.getInt("highscore", 0);

            /**
             * The following three lines show how to modify a key/value pair via a
             * so-called Editor.
             */
            SharedPreferences.Editor editor = prefs.edit();
            if(score > highscore) {
                editor.putInt("highscore", (highscore = score));
                editor.commit();
            }
        }

        public void drawGameOver(Canvas canvas) {
            if(player.getTop() < viewHeight) {
                background.draw(canvas);
                obstacles.draw(canvas);
                player.fallDown();
                player.draw(canvas);
            } else {
                background.draw(canvas);
                obstacles.draw(canvas);
                canvas.drawRoundRect(gameOverRect, 5.5f, 5.5f, menuColor);
                canvas.drawText(inGameText.get(0), viewWidth / 2, gameOverTextHeight, textPaintLarge);
                canvas.drawText(inGameText.get(1), viewWidth/2, scoreMessageTextHeight, textPaintSmall);
                canvas.drawText(""+score, viewWidth/2, scoreTextHeight, textPaintScore);
                canvas.drawText(inGameText.get(4), viewWidth/2, highscoreMessageTextHeight, textPaintSmall);
                canvas.drawText(""+highscore, viewWidth/2, highscoreTextHeight, textPaintHighScore);

                canvas.drawRoundRect(replayRect, 5.5f, 5.5f, buttonPaint);
                canvas.drawText(inGameText.get(3), viewWidth/2, playButtonText, textPaintSmall);
                //canvas.drawText(inGameText.get(3), viewWidth/3, viewHeight*2/3, textPaintSmall);
                //canvas.drawRect(viewWidth/6, viewHeight/5, viewWidth*5/6, viewHeight * 4/5, menuColor);//ugly
            }
        }

        public void update() {

        }

        public Boolean replayPressed(float pressX, float pressY) {
            if((pressX > replayRect.left) && (pressX < replayRect.right)
                    && (pressY < replayRect.bottom) && (pressY > replayRect.top)) {
                score = 0;
                return true;
            }
            return false;
        }

    }
}