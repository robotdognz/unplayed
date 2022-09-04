package com.flypuppy.unplayed;

import processing.core.*;
import game.AppLogic;
import android.content.ClipData;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class unplayed extends PApplet {

    private static final int SELECT_LEVEL = 1;
    private static final int SELECT_LEVELS = 2;

    AppLogic app; //manages the application level game logic

    //splash screen
    int splash; //steps through the start up stages
    PImage splashScreen; //the splash screen to be drawn while the game is loading

    //delta time
    final float FPS = 60;

    long last_time;

    public void setup() {
        //setup graphics
        background(0, 78, 83);
        frameRate(FPS);
        splash = 0;
        last_time = System.nanoTime();
    }

    public void init() {
        //setup game logic
        app = new AppLogic(this);

        //initialise the game
        AppLogic.init();
    }

    //this is the only draw method that should have step logic in it
    public void draw() {
        long time = System.nanoTime();
        float delta_time = (time - last_time) / 1000000000f; //1000000f
        last_time = time;

        //splash screen
        if (splash == 0) {  //draw black screen
            background(0, 78, 83);
            splash = 1;
            return;
        } else if (splash == 1) { //draw loading image
            splashScreen = loadImage("SplashScreen.png");
            imageMode(CENTER);
            int size = (int) (width * 0.8f);
            image(splashScreen, width * 0.5f, height * 0.5f, size, size);
            imageMode(CORNER);
            splash = 2;
            return;
        } else if (splash == 2) { //initialize game
            init();
            background(0, 78, 83);
            delay(736); //736
            splash = 3;
            return;
        }

        AppLogic.step(delta_time);
        AppLogic.draw(delta_time);
        //testing.draw(); //draw touch events
    }

    @Override
    public String sketchPath(String where) {
        // just asking, not creating or checking permissions or existence...
        // ... by using File dependencies with unknown side effects.
        if (sketchPath != null && where.length() > 1) {
            // println("sp: "+where+" "+sketchPath);
            if ('/' == where.charAt(0))
                return where;
            return sketchPath + "/" + where;
        }
        // nonsense...
        return super.sketchPath(where);
    }

    // Stop navigation bar from appearing on top of the game
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();

        android.view.View decorView = activity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                // The system bars are visible. Make any desired
                                // adjustments to your UI, such as showing the action bar or
                                // other navigational controls.
                                android.view.View decorView1 = activity.getWindow().getDecorView();
                                decorView1.setSystemUiVisibility(
                                        android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
                                                // Set the content to appear under the system bars so that the
                                                // content doesn't resize when the system bars hide and show.
                                                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                // Hide the nav bar and status bar
                                                | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN);
                            }

                        }
                );
    }

    public void touchStarted() {
        AppLogic.touchStarted();
    }

    public void touchEnded() {
        AppLogic.touchEnded();
    }

    public void touchMoved() {
        AppLogic.touchMoved();
    }

    public void onTap(float x, float y) {
        AppLogic.onTap(x, y);
    }

    public void onDoubleTap(float x, float y) {
        AppLogic.onDoubleTap(x, y);
    }

    public void onFlick(float x, float y, float px, float py, float v) {
        AppLogic.onFlick(x, y, px, py, v);
    }

    public void onLongPress(float x, float y) {
        AppLogic.onLongPress(x, y);
    }

    public void onPinch(float x, float y, float d) {
        AppLogic.onPinch(x, y, d);
    }


    public void onRotate(float x, float y, float angle) {
        AppLogic.onRotate(x, y, angle);
    }

    @SuppressWarnings("static-access")
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == this.getActivity().RESULT_OK) {
            if (requestCode == SELECT_LEVEL) {
                // get single uri if in single uri file select mode
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    // Perform operations on the document using its URI.
                    AppLogic.setUri(uri);
                } else {
                    PApplet.print("File result was null");
                }

            } else if (requestCode == SELECT_LEVELS) {
                // get all the Uri's if multiple select mode
                ClipData clipData = resultData.getClipData();
                ArrayList<Uri> uris = new ArrayList<>();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        clipData.getItemAt(i).getText();
                        uris.add(clipData.getItemAt(i).getUri());
                    }
                } else {
                    uris.add(resultData.getData());
                }
                AppLogic.setUris(uris);
            }
        }
    }

    @Override
    public void onStop() { // This should be called when the app closes
        // Save stuff
        super.onStop();
    }

    @Override
    public void onDestroy() { // This might be called when the app is killed
        // Save stuff
        super.onDestroy();
    }

    @Override
    public void onBackPressed() { // android back button pressed
        app.onBackPressed();
    }

    //------------------TouchTesting---------------------
    class TouchTesting {
        public void draw() {
            //go through the list of touches and draw them
            for (int i = 0; i < touches.length; i++) {
                ellipseMode(RADIUS); // Set ellipseMode to RADIUS fill(255); // Set fill to white ellipse(50, 50, 30, 30); // Draw white ellipse using RADIUS mode ellipseMode(CENTER); // Set ellipseMode to CENTER
                fill(255); // Set fill to gray
                ellipse(touches[i].x, touches[i].y, 70 + 2000 * touches[i].area, 70 + 2000 * touches[i].area); // Draw gray ellipse using CENTER
                fill(0);
                textSize(40);
                text(i, touches[i].x, touches[i].y - 150);
            }
        }
    }

    public void settings() {
        fullScreen(P2D); // p2d
    }
}
