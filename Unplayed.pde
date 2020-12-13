import game.*;

import android.content.Context;
import android.app.Activity;

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

@ Override
  public void onStop() { //This should be called when the app closes
  //Save stuff
  super.onStop();
}

@ Override
  public void onDestroy() { //This might be called when the app is killed
  //Save stuff
  super.onDestroy();
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
