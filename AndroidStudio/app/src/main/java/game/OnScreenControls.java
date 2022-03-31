package game;

import handlers.TextureCache;
import misc.CountdownTimer;
import processing.core.PApplet;
import processing.core.PVector;
import ui.EditorMenu;
import ui.GameMenu;

import static processing.core.PConstants.*;

public class OnScreenControls {
    private PApplet p;

    private final float controlButtonArea; // the region of the screen taken up by a control button
    private final float controlButtonSize; // actual render size of the control buttons

    private final float menuButtonArea; // the region of the screen taken up by the menu button
    private final float menuButtonSize; // actual render size of the menu button
    private boolean drawMenu;
    private PVector lastTouch = null;

    // x positions of control buttons
    private final float leftButtonXPosition;
    private final float jumpButtonXPosition;
    private final float rightButtonXPosition;

    // y position of control buttons
    private final float controlsInGameHeight; // the height controls should render at in game (from bottom of button)
    private float controlsYPosition; // the current control render height (from bottom of button)

    // y position of menu button
    private final float menuInGameHeight;// the height the menu should render at in game (from top of button)
    private final float menuXPosition;
    private float menuYPosition; // the current menu render height (from top of button)

    // UI fade logic
    private boolean draw; // current draw state
    private final CountdownTimer fade; // fade timer
    private int fadeInt; // current fade level

    // UI tint logic
    private final CountdownTimer leftTint;
    private final CountdownTimer jumpTint;
    private final CountdownTimer rightTint;
    private final CountdownTimer menuTint;
    private final int tintShade; // tint shade used for tinting buttons when pressed

    public OnScreenControls(PApplet p, int screenWidth, int screenHeight) {
        this.p = p;

        this.controlButtonArea = screenWidth / 3f;
        this.controlButtonSize = controlButtonArea * 0.8f;
        this.leftButtonXPosition = 0 + (controlButtonArea * 0.5f);
        this.jumpButtonXPosition = screenWidth / 2f;
        this.rightButtonXPosition = screenWidth - (controlButtonArea * 0.5f);


        // calculate control height based on screen aspect ratio
        float idealRatio = 9f / 16f; // the aspect ratio the game is built for
        float idealHeight = screenWidth / idealRatio; // gives the height (in pixels) the game UI should work at, relative to the actual width
        float buttonVerticalOffset = (screenHeight - idealHeight) / 2f;
        this.controlsInGameHeight = screenHeight - buttonVerticalOffset;
        this.controlsYPosition = this.controlsInGameHeight;

        // calculate menu height based on screen aspect ratio
        this.menuButtonArea = screenWidth / 6f;
        this.menuButtonSize = menuButtonArea * 0.8f;
        this.menuInGameHeight = buttonVerticalOffset;
        this.menuYPosition = this.menuInGameHeight;
        this.menuXPosition = screenWidth / 2f;


        fade = new CountdownTimer(0.5f);
        float tintFadeTime = 0.2f;
        leftTint = new CountdownTimer(tintFadeTime);
        jumpTint = new CountdownTimer(tintFadeTime);
        rightTint = new CountdownTimer(tintFadeTime);
        menuTint = new CountdownTimer(0.8f);
        tintShade = 150;

    }

    public void step(float deltaTime, boolean draw, float bottom, boolean drawMenu, PVector lastTouch) {
        fade.deltaStep(deltaTime);
        leftTint.deltaStep(deltaTime);
        jumpTint.deltaStep(deltaTime);
        rightTint.deltaStep(deltaTime);
        menuTint.deltaStep(deltaTime);

        this.lastTouch = lastTouch;
        this.drawMenu = drawMenu;

        // move the on screen controls so they don't overlap the editor UI
        if (bottom < controlsInGameHeight) {
            controlsYPosition = PApplet.lerp(controlsYPosition, bottom, 4 * deltaTime);
        } else {
            controlsYPosition = PApplet.lerp(controlsYPosition, controlsInGameHeight, 4 * deltaTime);
        }

        if (fade.isFinished()) {
            this.draw = draw;
            fade.stop();
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
        if (leftTint.isRunning()) {
            int temp = (int) ((255 - tintShade) * leftTint.deltaRemainingRatio());
            temp = Math.min(tintShade + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsLeft(), leftButtonXPosition, controlsYPosition - (controlButtonArea * 0.5f), controlButtonSize, controlButtonSize);
        p.tint(255, fadeInt);

        // jump button
        if (jumpTint.isRunning()) {
            int temp = (int) ((255 - tintShade) * jumpTint.deltaRemainingRatio());
            temp = Math.min(tintShade + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsJump(), jumpButtonXPosition, controlsYPosition - (controlButtonArea * 0.5f), controlButtonSize, controlButtonSize);
        p.tint(255, fadeInt);

        // right button
        if (rightTint.isRunning()) {
            int temp = (int) ((255 - tintShade) * rightTint.deltaRemainingRatio());
            temp = Math.min(tintShade + temp, 255);
            p.tint(temp, fadeInt);
        }
        p.image(TextureCache.getControlsRight(), rightButtonXPosition, controlsYPosition - (controlButtonArea * 0.5f), controlButtonSize, controlButtonSize);
        p.tint(255, fadeInt);

        // menu button
        if (drawMenu) {
            if (menuTint.isRunning()) {
                int temp = (int) ((255 - tintShade) * rightTint.deltaRemainingRatio());
                temp = Math.min(tintShade + temp, 255);
                p.tint(tintShade, fadeInt);
            }
            p.image(TextureCache.getControlsMenu(), menuXPosition, menuYPosition + (menuButtonArea * 0.5f), menuButtonSize, menuButtonSize);
        }
        p.noTint();
    }

    public void touchEnded() {
        if (menuTint.isRunning() || AppLogic.hasMenu()) {
            return;
        }
        if (lastTouch == null) {
            return;
        }
        if (lastTouch.y < menuYPosition || lastTouch.y > menuYPosition + menuButtonArea) {
            return;
        }
        if (lastTouch.x < menuXPosition - (menuButtonArea * 0.5f) || lastTouch.x > menuXPosition + (menuButtonArea * 0.5f)) {
            return;
        }

        if (AppLogic.editor != null) {
            AppLogic.addMenu(new EditorMenu(p));
            //AppLogic.toggleEditor();
        } else {
            AppLogic.addMenu(new GameMenu(p, AppLogic.game));
        }

        menuTint.start();
    }

    public void left() {
        leftTint.start();
    }

    public void right() {
        rightTint.start();
    }

    public void jump() {
        jumpTint.start();
    }
}
