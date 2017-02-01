package com.game.alm7in.flappything.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Moaan on 5/26/2015.
 */
public class Background {
    private Bitmap background;
    private int imageWidth, imageHeight;
    private int screenWidth, screenHeight, viewImageRatio;
    private int xPosition, yPosition, srcDisplayAmount, scrollIncrement;
    private Rect src, dst;

    public Background(Bitmap spriteSheet, int screenWidth, int screenHeight) {
        background = spriteSheet;
        imageWidth = background.getWidth();
        imageHeight = background.getHeight();
        xPosition = 0;
        yPosition = 0;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        srcDisplayAmount = imageWidth/8;
        scrollIncrement = screenWidth/100;
        viewImageRatio = srcDisplayAmount/screenWidth;
    }

    public void update() {
        xPosition = (xPosition + scrollIncrement)%(imageWidth - srcDisplayAmount);

    }

    public void draw(Canvas canvas) {
        /*
        src = new Rect(xPosition, yPosition, xPosition + displayAmount, imageHeight);
        dst = new Rect(0, 0, screenWidth, screenHeight);
        canvas.drawBitmap(background, src, dst, null);

        */


        if((xPosition + srcDisplayAmount) < imageWidth) {
            src = new Rect(xPosition, yPosition, xPosition + srcDisplayAmount, imageHeight);
            dst = new Rect(0, 0, screenWidth, screenHeight);
            canvas.drawBitmap(background, src, dst, null);
        } else {
            src = new Rect(xPosition, yPosition, imageWidth, imageHeight);
            dst = new Rect(0, 0, (imageWidth-xPosition)*viewImageRatio, screenHeight);
            canvas.drawBitmap(background, src, dst, null);
        }


        /*
        if((xPosition + displayAmount) > imageWidth) {
            src = new Rect(xPosition, yPosition, xPosition + (imageWidth-xPosition), imageHeight);
            dst = new Rect(0, 0, screenWidth, screenHeight);
            canvas.drawBitmap(background, src, dst, null);
        }
        src = new Rect(xPosition, yPosition, xPosition + displayAmount, imageHeight);
        dst = new Rect(0, 0, screenWidth, screenHeight);
        canvas.drawBitmap(background, src, dst, null);


        */
    }

    public void restart() {
        xPosition = 0;
        yPosition = 0;
    }

    //get player position with respect to background image, not the screen
    public int getX() {return xPosition + screenWidth/2;}
    public int getY() {return yPosition + imageWidth/4;}

}