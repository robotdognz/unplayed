import camera.*;
import controllers.*;
import editor.*;
import game.*;
import handlers.*;
import menus.*;
import misc.*;
import objects.*;
import ui.*;

import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;
import android.os.Environment;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.*;

import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import android.widget.Toast;

PGraphics mainGraphics; 

GameLogic gl;
KetaiGesture gesture;
Vibe vibe;
Converter convert;
TextureCache texture;

Activity activity;
Context context;

int splash; //true if the game hasn't started looping and a splash screen should be drawn
PImage splashScreen;
Game game; //holds the game class
Controller controller; //holds the current controller
Editor editor; //holds the editor

//boolean gPaused; //is the game class paused
ArrayList<Widget> gWidgets;
float gWidgetSpacing; //size of gap between widgets
//boolean editorToggle; //is the game in editor mode
//Menu menu;

//touch screen stuff
//TouchTesting testing = new TouchTesting();
ArrayList<PVector> touch;
PVector lastTouch;

//frame count
int frameDelay;
float frame;


void setup() {
  //setup graphics
  fullScreen(P2D);
  background(40, 40, 40);
  frameRate(60);
  mainGraphics = g; //get default PGraphics
  splash = 0;
  
  gl = new GameLogic();

  //setup feilds for Toast
  activity = this.getActivity();
  context = activity.getApplicationContext();

  //check and get permissions
  if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
    requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
  }
}

void init() {
  //setup fields
  gl.gPaused = false;
  gWidgets = new ArrayList<Widget>();
  gl.editorToggle = true;
  gl.menu = null;
  touch = new ArrayList<PVector>();
  lastTouch = new PVector(0, 0);
  frameDelay = 100;

  //setup special classes
  texture = new TextureCache(this);
  gesture = new KetaiGesture(this);
  vibe = new Vibe(context);

  
  //setup game
  Camera camera = new FreeCamera(); //new GameCamera();
  convert = new Converter(this, camera); //camera converter
  game = new Game(this, camera, vibe, texture, convert, gl); 
  controller = new PlayerControl(this, game);
  editor = new Editor(this, texture, game, camera, convert);

  ////setup non editor widget(s)
  Widget menuW = new MenuWidget(this, editor, null);
  gWidgets.add(menuW);
  gWidgetSpacing = width/(gWidgets.size()+1);
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
    image(splashScreen, width/2, height/2, size, size*1.777);
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
  touch.clear();
  for (TouchEvent.Pointer t : touches) {
    touch.add(new PVector(t.x, t.y));
  }
  if (touches.length > 0) {
    lastTouch = new PVector(touches[touches.length-1].x, touches[touches.length-1].y);
  } else {
    lastTouch = new PVector(0, 0);
  }

  
  
  //game
  if (!gl.gPaused) { //step the game if it is not paused
    //step editor or game controller depending on editor toggle
    if (gl.editorToggle) {
      editor.step(touch);
    } else {
      controller.step(touch);
    }
    game.step(); //step game
  }
  game.draw(); //draw the game
  
  if (gl.editorToggle) {
    editor.draw(lastTouch, gl.menu);
  } else {
    for (int i = 0; i < gWidgets.size(); i++) {
      gWidgets.get(i).draw(gWidgetSpacing*(i+1), 120);
      gWidgets.get(i).updateActive();
      if (gl.menu == null) {
        gWidgets.get(i).hover(lastTouch);
      }
    }
  }

  //draw the menu
  if (gl.menu != null) { 
    gl.menu.draw();
    gl.menu.hover(lastTouch);
  }
}

void touchStarted() {
  //find true last touch
  if (touches.length >= touch.size() && 
    touches.length > 1) {
    for (int i = 0; i < touches.length; i++) {
      boolean match = false;
      for (PVector t : touch) {
        float currentDiff = sqrt(sq(t.x-touches[i].x)+sq(t.x-touches[i].x));
        if (currentDiff < 10) {
          match = true;
        }
      }
      if (!match) { //no match for current touch, so it's new
        lastTouch = new PVector(touches[i].x, touches[i].y);
      }
    }
  } else if (touches.length == 1) {
    lastTouch = new PVector(touches[touches.length-1].x, touches[touches.length-1].y);
  }

  if (gl.menu == null) {
    if (gl.editorToggle) {
      editor.touchStarted(lastTouch);
    } else {
      controller.touchStarted(lastTouch);
    }
  }
}

void touchEnded() {
  if (gl.editorToggle) {
    editor.touchEnded();
  } else {
    for (int i = 0; i < gWidgets.size(); i++) {
      gWidgets.get(i).click();
    }
  }

  if (gl.menu != null) {
    gl.menu.click();
  }
}

void touchMoved() {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      editor.touchMoved(touch);
    } else {
      controller.touchMoved(touch);
    }
  }
}

void onPinch(float x, float y, float d) {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      editor.onPinch(touch, x, y, d);
    } else {
      controller.onPinch(touch, x, y, d);
    }
  }
}

void onTap (float x, float y) {
  if (gl.menu == null) {
    if (gl.editorToggle) {
      editor.onTap(x, y);
    } else {
      //gController.onTap(x, y);
    }
  }
}

//void onFlick(float x, float y, float px, float py, float v) {
//  //x/y start of flick
//  //px/yx end of flick
//  //v velocity of flick
//}
//void onRotate(float x, float y, float angle) {}

//used for printing messages to the screen
void showToast(final String message) { 
  activity.runOnUiThread(new Runnable() { 
    public void run() { 
      android.widget.Toast.makeText(activity.getApplicationContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }
  }
  );
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
