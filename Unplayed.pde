import camera.*;
import controllers.*;
import editor.*;
import game.*;
import handlers.*;
import menus.*;
import misc.*;
import objects.*;
import ui.*;

import android.content.Context;
import android.app.Activity;

Activity activity;
Context context;
GameLogic gl; //manages the application level game logic

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
  gl = new GameLogic(this, activity, context);

  //check and get permissions
  if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
  }

  //initalise the game
  gl.init();
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
  } else if (splash == 2) { //load textures
    //delay(736);
    init();
    splash = 3;
    return;
  }

  //testing.draw(); //draw touch events
  //reset stored touch events
  gl.step();
  gl.draw();
}

void touchStarted() {
  gl.touchStarted();
}

void touchEnded() {
  gl.touchEnded();
}

void touchMoved() {
  gl.touchMoved();
}

void onPinch(float x, float y, float d) {
  gl.onPinch(x, y, d);
}

void onTap (float x, float y) {
  gl.onTap(x, y);
}

void onFlick(float x, float y, float px, float py, float v) {
  gl.onFlick(x, y, px, py, v);
}
void onRotate(float x, float y, float angle) {
  gl.onRotate(x, y, angle);
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
