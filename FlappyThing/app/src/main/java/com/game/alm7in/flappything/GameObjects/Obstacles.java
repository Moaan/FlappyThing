package com.game.alm7in.flappything.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Moaan on 5/28/2015.
 */
public class Obstacles {
    private Bitmap trapImage;
    private int collision = 0;
    private Bird bird;
    private int screenWidth, screenHeight;
    private double screenWidth_d, screenHeight_d;
    private int imageWidth, imageHeight;

    private Trap[] trapCollection;
    private int[] trapPositions;
    private int trapPositionsSize;
    private Rect src;

    private Random rNG;

    public Obstacles(Bird bird, Bitmap trapImage, int screenWidth, int screenHeight) {
        rNG = new Random();

        this.bird = bird;
        this.trapImage = trapImage;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        screenWidth_d = (double)screenWidth;
        screenHeight_d = (double)screenHeight;
        imageWidth = trapImage.getWidth();
        imageHeight = trapImage.getHeight();

        trapPositions = new int[4];
        trapPositions[0] = screenHeight/3;
        trapPositions[1] = screenHeight*2/5;
        trapPositions[2] = screenHeight/2;
        trapPositions[3] = screenHeight*3/5;

        trapPositionsSize = trapPositions.length;

        trapCollection = new Trap[2];
        trapCollection[0] = new Trap(1.);
        trapCollection[1] = new Trap(8.);

        src = new Rect(0, 0, trapImage.getWidth(), trapImage.getHeight());
    }

    /*Returns
        0 if no collision and no obstacle passed
        1 if obstacle passed successively
        2 if collision happened
     */
    public int update() {
        collision = 0;
        if((bird.getTop() < -1*screenHeight) || (bird.getBottom() > screenHeight)) {
            collision = 2;
        } else {
            for(Trap trap : trapCollection) {
                trap.update();
                if((bird.getLeft() > trap.xPosition) && (bird.getRight() < trap.dstOne.right)) {
                    if(!((bird.getTop() > trap.clearTop) && (bird.getBottom() < trap.clearBottom))) {
                        collision = 2;
                    }else if(!trap.birdPassed){
                        collision = 1;
                        trap.birdPassed = true;
                    }
                }
            }
        }

        return collision;
    }

    public void draw(Canvas canvas) {
        for(Trap trap : trapCollection) {
            //if(trap.xPosition > -5 && (trap.xPosition + trap.width) < screenWidth+5) {
                canvas.drawBitmap(trapImage, src, trap.dstOne, null);
                canvas.drawBitmap(trapImage, src, trap.dstTwo, null);
            //}
        }
    }

    /* "easter egg" shhh */
    public void zoom() {
        for(int i = 0; i < trapCollection[0].width; i++) { //all traps have same width, it don't matter.
            for(Trap trap : trapCollection) {
                trap.update();
            }
        }
    }

    public void reset() {
        for(Trap trap : trapCollection) {
            trap.reset();
        }
    }


    //inner class; the trap that player must avoid.
    class Trap {
        public int xPosition, startingX, yPositionOne, yPositionTwo, width, height = screenHeight*2/3;
        public int clearTop, clearBottom, clearHeight = screenHeight/4;
        public int scrollIncrement = screenWidth/70;
        public boolean birdPassed = false;

        //the trap consists of a top and bottom which are drawn in two places: dstOne & dstTwo
        public Rect dstOne, dstTwo;

        public Trap(double startingPoint) {
            xPosition = startingX = screenWidth + (int)(screenWidth_d*(startingPoint / 10.0));

            clearTop = trapPositions[rNG.nextInt(trapPositionsSize)];//(int) (screenHeight_d /(rNG.nextDouble() + 1.5));//(screenHeight_d / rNG.nextDouble());
            clearBottom = clearTop + clearHeight;

            width = height / (imageHeight / imageWidth);

            yPositionOne = clearTop - height;
            yPositionTwo = clearBottom;

            dstOne = new Rect(xPosition, yPositionOne, xPosition + width, yPositionOne + height);
            dstTwo = new Rect(xPosition, yPositionTwo, xPosition + width, yPositionTwo + height);
        }

        public void update() {
            xPosition -= scrollIncrement;
            if(xPosition < -width) respawn();
            dstOne.left = dstTwo.left = xPosition;
            dstOne.right = dstTwo.right = (xPosition + width);
        }

        private void respawn() {
            birdPassed = false;
            xPosition = screenWidth;//(int)(screenWidth_d + screenWidth_d*(rNG.nextDouble()/2.0));//screenWidth + screenHeight /(rNG.nextInt(2) + 2);//screenWidth*((rNG.nextInt(6))/10);

            clearTop = trapPositions[rNG.nextInt(trapPositionsSize)];//(int) (screenHeight_d /(rNG.nextDouble() + 2.0));//screenHeight /(rNG.nextInt(2) + 2);
            clearBottom = clearTop + clearHeight;

            yPositionOne = clearTop - height;
            yPositionTwo = clearBottom;

            dstOne.top = yPositionOne;
            dstOne.bottom = yPositionOne + height;
            dstTwo.top = yPositionTwo;
            dstTwo.bottom = yPositionTwo + height;

        }

        private void reset() {
            birdPassed = false;
            xPosition = startingX;

            dstOne.top = yPositionOne;
            dstOne.bottom = yPositionOne + height;
            dstTwo.top = yPositionTwo;
            dstTwo.bottom = yPositionTwo + height;

            dstOne.left = dstTwo.left = xPosition;
            dstOne.right = dstTwo.right = (xPosition + width);
        }

    }
}


