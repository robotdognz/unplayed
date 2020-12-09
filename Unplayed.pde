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

  //setup game logic
  activity = this.getActivity();
  context = activity.getApplicationContext();
  gl = new GameLogic(this, activity, context);

  //check and get permissions
  if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
  }
}

void init() {
  //initalise the game
  gl.init();
}

//this is the only draw method that should have step logic in it
void draw() {

  //splash screen
  if (splash == 0) {  //draw black screen
    background(40, 40, 40);
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
  gl.touches.clear();
  for (TouchEvent.Pointer t : touches) {
    gl.touches.add(new PVector(t.x, t.y));
  }
  if (touches.length > 0) {
    gl.lastTouch = new PVector(touches[touches.length-1].x, touches[touches.length-1].y);
  } else {
    gl.lastTouch = new PVector(0, 0);
  }

  //game
  if (!gl.gPaused) { //step the game if it is not paused
    //step editor or game controller depending on editor toggle
    if (gl.editorToggle) {
      gl.editor.step(gl.touches);
    } else {
      gl.controller.step(gl.touches);
    }
    gl.game.step(); //step game
  }
  gl.game.draw(); //draw the game
  
  if (gl.editorToggle) {
    gl.editor.draw(gl.lastTouch, gl.menu);
  } else {
    for (int i = 0; i < gl.widgets.size(); i++) {
      gl.widgets.get(i).draw(gl.widgetSpacing*(i+1), 120);
      gl.widgets.get(i).updateActive();
      if (gl.menu == null) {
        gl.widgets.get(i).hover(gl.lastTouch);
      }
    }
  }

  //draw the menu
  if (gl.menu != null) { 
    gl.menu.draw();
    gl.menu.hover(gl.lastTouch);
  }
}

void touchStarted() {
  //find true last touch
  if (touches.length >= gl.touches.size() && 
    touches.length > 1) {
    for (int i = 0; i < touches.length; i++) {
      boolean match = false;
      for (PVector t : gl.touches) {
        float currentDiff = sqrt(sq(t.x-touches[i].x)+sq(t.x-touches[i].x));
        if (currentDiff < 10) {
          match = true;
        }
      }
      if (!match) { //no match for current touch, so it's new
        gl.lastTouch = new PVector(touches[i].x, touches[i].y);
      }
    }
  } else if (touches.length == 1) {
    gl.lastTouch = new PVector(touches[touches.length-1].x, touches[touches.length-1].y);
  }

  if (gl.menu == null) {
    if (gl.editorToggle) {
      gl.editor.touchStarted(gl.lastTouch);
    } else {
      gl.controller.touchStarted(gl.lastTouch);
    }
  }
}

void touchEnded() {
  if (gl.editorToggle) {
    gl.editor.touchEnded(gl.lastTouch);
  } else {
    for (int i = 0; i < gl.widgets.size(); i++) {
      gl.widgets.get(i).click();
    }
  }

  if (gl.menu != null) {
    gl.menu.click();
  }
}

void touchMoved() {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      gl.editor.touchMoved(gl.touches);
    } else {
      gl.controller.touchMoved(gl.touches);
    }
  }
}

void onPinch(float x, float y, float d) {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      gl.editor.onPinch(gl.touches, x, y, d);
    } else {
      gl.controller.onPinch(gl.touches, x, y, d);
    }
  }
}

void onTap (float x, float y) {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      gl.editor.onTap(x, y);
    } else {
      //controller.onTap(x, y);
    }
  }
}

//void onFlick(float x, float y, float px, float py, float v) {
//  //x/y start of flick
//  //px/yx end of flick
//  //v velocity of flick
//}
//void onRotate(float x, float y, float angle) {}

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
