package com.game.alm7in.flappything.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Moaan on 5/26/2015.
 */
public class Bird {
    private Bitmap spriteFrames[];
    private int frameWidth, frameHeight;
    private int frameIndex;

    //private int screenWidth, screenHeight;
    private int displayWidth, displayHeight;

    private int startingXposition, startingAltitude;
    private int xPosition, altitude;
    private int displacement, fallAcceleration, jumpVelocity;
    private int fallTime = 0, jumpTime = 0;
    private int jumpDuration = 6;

    private Rect src, dst;

    public Bird(Bitmap frames[], int screenWidth, int screenHeight) {
        /*this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;*/
        displacement = fallAcceleration = screenHeight/545;
        jumpVelocity = -1*(screenHeight/193);
        xPosition = startingXposition = screenWidth/5;
        altitude = startingAltitude = screenHeight/3;

        spriteFrames = frames;
        frameWidth = frames[0].getWidth();
        frameHeight = frames[0].getHeight();
        frameIndex = 0;

        //bird is drawn to always be 1/7 the screen width and maintain proportional height with image.
        displayHeight = (screenWidth / 7) / (frameWidth / frameHeight);//frameHeight*2;//
        displayWidth = (frameWidth / frameHeight) * displayHeight;//frameWidth*2/3;//

        src = new Rect(0, 0, frameWidth, frameHeight);
        dst = new Rect(xPosition, altitude, xPosition + displayWidth, altitude + displayHeight);
    }

    public void update() {
        /*below accurately did free fall but that's not what we want*/
        //displacement = jumpVelocity*jumpTime + fallAcceleration*(fallTime*fallTime);

        displacement = jumpVelocity*jumpTime + fallAcceleration*fallTime;
        altitude += displacement;
        //displayHeight += displacement;
        if(jumpTime > 0){
            jumpTime--;
            frameIndex = (frameIndex + 1)%(spriteFrames.length - 1);
            if(jumpTime == 0) frameIndex = 0;
        } else {
            fallTime++;
        }
    }

    public void jump() {
        jumpTime = jumpDuration;
        fallTime = 0;
        /*
        altitude -= jumpVelocity;
        displayHeight -= jumpVelocity;*/
    }

    public void die() {
        frameIndex = (spriteFrames.length - 1);
    }

    public void revive() {
        xPosition = startingXposition;
        altitude = startingAltitude;
        frameIndex = 0;
    }

    public void fallDown() {
        displacement = fallAcceleration*fallTime;
        altitude += displacement;
        fallTime++;
    }

    public void draw(Canvas canvas) {
        dst.top = altitude;
        dst.bottom = altitude + displayHeight;
        canvas.drawBitmap(spriteFrames[frameIndex], src, dst, null);
    }

    public int getTop() {
        return altitude;
    }

    public int getLeft() {
        return xPosition;
    }

    public int getBottom() {
        return altitude + displayHeight;
    }

    public int getRight() {
        return xPosition + displayWidth;
    }
}
