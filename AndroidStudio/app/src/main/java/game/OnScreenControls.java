package game;

import handlers.TextureCache;
import misc.CountdownTimer;
import processing.core.PApplet;

import static processing.core.PConstants.*;

public class OnScreenControls {
    private float buttonArea; // the region of the screen taken up by a button
    private float buttonSize; // actual render size of the buttons

    // x positions of buttons
    private float leftButtonXPosition;
    private float jumpButtonXPosition;
    private float rightButtonXPosition;

    // fade logic
    private boolean draw; // current draw state
    private CountdownTimer fade; // fade timer
    private int fadeInt; // fade level

    private float bottomYPosition;

    // button tint
    private CountdownTimer left;
    private CountdownTimer jump;
    private CountdownTimer right;
    private int tint;

    public OnScreenControls(int screenWidth, int screenHeight) {
        this.buttonArea = screenWidth / 3f;
        this.buttonSize = buttonArea * 0.8f;
        this.leftButtonXPosition = 0 + (buttonArea * 0.5f);
        this.jumpButtonXPosition = screenWidth / 2f;
        this.rightButtonXPosition = screenWidth - (buttonArea * 0.5f);

        this.bottomYPosition = screenHeight;

        fade = new CountdownTimer(0.5f);
        float tintFadeTime = 0.2f; // 0.15f // 0.7fy
        left = new CountdownTimer(tintFadeTime);
        jump = new CountdownTimer(tintFadeTime);
        right = new CountdownTimer(tintFadeTime);
        tint = 150;

    }

    public void step(float deltaTime, boolean draw, float bottom) {
        fade.deltaStep(deltaTime);
        left.deltaStep(deltaTime);
        jump.deltaStep(deltaTime);
        right.deltaStep(deltaTime);

        bottomYPosition = bottom;

        if (fade.isFinished()) {
            this.draw = draw;
            fade.stop();
            PApplet.print("Why?");
            if (this.draw) {
                // fading out
                fadeInt = 255;
            } else {
                // fading in
                fadeInt = 0;
            }
            return;
        }
        if (draw != this.draw && !fade.isRunning()) {
            fade.start();
        }
        if (fade.isRunning()) {
            // doing a fade animation
            int ratio = (int) (255 * fade.deltaRemainingRatio());

            if (this.draw) {
                // fading out
                fadeInt = 255 - ratio;
            } else {
                // fading in
                fadeInt = ratio;
            }
        }


    }

    public void draw(PApplet p) {
        if (fadeInt == 0) {
            return;
        }
        p.imageMode(CENTER);
        p.tint(255, fadeInt);

        // left button
        if (left.isRunning()) {
            int temp = (int) ((255 - tint) * left.deltaRemainingRatio());
            temp = Math.min(tint + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsLeft(), leftButtonXPosition, bottomYPosition - (buttonArea * 0.5f), buttonSize, buttonSize); // draw the left button
        p.tint(255, fadeInt);

        // jump button
        if (jump.isRunning()) {
            int temp = (int) ((255 - tint) * jump.deltaRemainingRatio());
            temp = Math.min(tint + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsJump(), jumpButtonXPosition, bottomYPosition - (buttonArea * 0.5f), buttonSize, buttonSize); // draw the jump button
        p.tint(255, fadeInt);

        // right button
        if (right.isRunning()) {
            int temp = (int) ((255 - tint) * right.deltaRemainingRatio());
            temp = Math.min(tint + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsRight(), rightButtonXPosition, bottomYPosition - (buttonArea * 0.5f), buttonSize, buttonSize); // draw the right button
        p.noTint();
    }

    public void left() {
        left.start();
    }

    public void right() {
        right.start();
    }

    public void jump() {
        jump.start();
    }
}
