import game.*;

import android.content.Context;
import android.app.Activity;
import android.view.*;
import android.os.Bundle;
import android.content.Intent;

//import android.app.Activity;
//import android.content.ContentUris;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;

Activity activity;
Context context;
AppLogic app; //manages the application level game logic

//splash screen
int splash; //steps through the start up stages 
PImage splashScreen; //the splash screen to be drawn while the game is loading

void setup() {
  //setup graphics
  fullScreen(P2D);
  background(0, 78, 83);
  frameRate(60);
  splash = 0;
}

void init() {
  //setup game logic
  activity = this.getActivity();
  context = activity.getApplicationContext();
  app = new AppLogic(this, activity, context);

  //check and get permissions
  if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
  }

  //initalise the game
  app.init();
}

//this is the only draw method that should have step logic in it
void draw() {

  //splash screen
  if (splash == 0) {  //draw black screen
    background(0, 78, 83);
    splash = 1;
    return;
  } else if (splash == 1) { //draw loading image
    splashScreen = loadImage("SplashScreen.png");
    imageMode(CENTER);
    int size = (int) (width*0.8);
    image(splashScreen, width/2, height/2, size, size);
    imageMode(CORNER);
    splash = 2;
    return;
  } else if (splash == 2) { //initalize game
    //delay(736);
    init();
    splash = 3;
    return;
  }

  //testing.draw(); //draw touch events
  app.draw();
}

void touchStarted() {
  app.touchStarted();
}

void touchEnded() {
  app.touchEnded();
}

void touchMoved() {
  app.touchMoved();
}

void onTap (float x, float y) {
  app.onTap(x, y);
}

void onDoubleTap(float x, float y) {
  app.onDoubleTap(x, y);
}

void onFlick(float x, float y, float px, float py, float v) {
  app.onFlick(x, y, px, py, v);
}

void onLongPress(float x, float y) {
  app.onLongPress(x, y);
}

void onPinch(float x, float y, float d) {
  app.onPinch(x, y, d);
}


void onRotate(float x, float y, float angle) {
  app.onRotate(x, y, angle);
}

//void test(){
//  Intent intent = new Intent();
//  intent.setType("application/json");
//  intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
//  activity.startActivityForResult(Intent.createChooser(intent, "Select Level"), SELECT_IMAGE);
//}

@SuppressWarnings("static-access")
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == this.getActivity().RESULT_OK) {
      if (requestCode == SELECT_IMAGE) {
        app.setUri(data.getData());
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

//Stop navagation bar from appearing on top of the game
@Override
  public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  this.activity = this.getActivity();

  android.view.View decorView = activity.getWindow().getDecorView();
  decorView.setOnSystemUiVisibilityChangeListener
    (new android.view.View.OnSystemUiVisibilityChangeListener() {
    @Override
      public void onSystemUiVisibilityChange(int visibility) {
      // Note that system bars will only be "visible" if none of the
      // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
      if ((visibility & android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
        // TODO: The system bars are visible. Make any desired
        // adjustments to your UI, such as showing the action bar or
        // other navigational controls.
        android.view.View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
          android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
          // Set the content to appear under the system bars so that the
          // content doesn't resize when the system bars hide and show.
          | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          // Hide the nav bar and status bar
          | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN);
      } else {
        // TODO: The system bars are NOT visible. Make any desired
        // adjustments to your UI, such as hiding the action bar or
        // other navigational controls.
      }
    }
  }
  );
}

//------------------TouchTesting---------------------
class TouchTesting {
  void draw() {
    //go through the list of touches and draw them
    for (int i = 0; i < touches.length; i++) {
      ellipseMode(RADIUS); // Set ellipseMode to RADIUS fill(255); // Set fill to white ellipse(50, 50, 30, 30); // Draw white ellipse using RADIUS mode ellipseMode(CENTER); // Set ellipseMode to CENTER 
      fill(255); // Set fill to gray 
      ellipse(touches[i].x, touches[i].y, 70+2000*touches[i].area, 70+2000*touches[i].area); // Draw gray ellipse using CENTER
      fill(0);
      textSize(40);
      text(i, touches[i].x, touches[i].y-150);
    }
  }
}
