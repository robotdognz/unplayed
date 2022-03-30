package com.flypuppy.unplayed;

import processing.core.*;
import game.AppLogic;
import android.content.ClipData;
import android.content.Context;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import java.util.ArrayList;

public class unplayed extends PApplet {

    private static final int SELECT_LEVEL = 1;
    private static final int SELECT_LEVELS = 2;

    Activity activity;
    Context context;
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
        activity = this.getActivity();
        context = activity.getApplicationContext();
        app = new AppLogic(this, activity, context);

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
            delay(736);
            init();
            splash = 3;
            return;
        }

        //testing.draw(); //draw touch events
        AppLogic.draw(delta_time);
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
  /* which is (stinking dead code...) this:
    // isAbsolute() could throw an access exception, but so will writing
   // to the local disk using the sketch path, so this is safe here.
   // for 0120, added a try/catch anyways.
   try {
   if (new File(where).isAbsolute()) return where;
   } catch (Exception e) { }
   
   return surface.getFileStreamPath(where).getAbsolutePath();
   
   */
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
    public void onStop() { //This should be called when the app closes
        //Save stuff
        super.onStop();
    }

    @Override
    public void onDestroy() { //This might be called when the app is killed
        //Save stuff
        super.onDestroy();
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
        fullScreen(P2D);
    }
}
