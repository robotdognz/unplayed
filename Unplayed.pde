import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;
import android.os.Environment;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.regex.*;

import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import android.widget.Toast;

public KetaiGesture gesture;
public Vibe vibe;
public Converter convert;
public TextureCache texture;

public Activity activity;
public Context context;

private int splash; //true if the game hasn't started looping and a splash screen should be drawn
private PImage splashScreen;
Game g; //holds the game class
Camera gCamera; //holds the game camera
Controller gController; //holds the current controller
Editor edit; //holds the editor

boolean gPaused; //is the game class paused
private ArrayList<Widget> gWidgets;
private float gWidgetSpacing; //size of gap between widgets
private boolean editorToggle; //is the game in editor mode
private Menu menu;

//touch screen stuff
//private TouchTesting testing = new TouchTesting();
private ArrayList<PVector> touch;
private PVector lastTouch;

//frame count
int frameDelay;
float frame;


void setup() {
  //setup graphics
  fullScreen(P2D);
  background(40, 40, 40);
  frameRate(60);
  splash = 0;

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
  gPaused = false;
  gWidgets = new ArrayList<Widget>();
  editorToggle = true;
  menu = null;
  touch = new ArrayList<PVector>();
  lastTouch = new PVector(0, 0);
  frameDelay = 100;

  //setup special classes
  texture = new TextureCache();
  gesture = new KetaiGesture(this);
  vibe = new Vibe();
  convert = new Converter();

  //setup game
  gCamera = new FreeCamera(); //new GameCamera();
  g = new Game(gCamera, vibe); 
  gController = new PlayerControl();
  edit = new Editor(g);

  ////setup non editor widget(s)
  Widget menuW = new MenuWidget(edit, null);
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

  //game
  if (!gPaused) { //step the game if it is not paused
    //step editor or game controller depending on editor toggle
    if (editorToggle) {
      edit.step();
    } else {
      gController.step();
    }
    g.step(); //step game
  }
  g.draw(); //draw the game

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

  if (editorToggle) {
    edit.draw();
  } else {
    for (int i = 0; i < gWidgets.size(); i++) {
      gWidgets.get(i).draw(gWidgetSpacing*(i+1), 120);
      gWidgets.get(i).updateActive();
      if (menu == null) {
        gWidgets.get(i).hover(lastTouch);
      }
    }
  }

  //draw the menu
  if (menu != null) { 
    menu.draw();
    menu.hover(lastTouch);
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

  if (menu == null) {
    if (editorToggle) {
      edit.touchStarted();
    } else {
      gController.touchStarted();
    }
  }
}

void touchEnded() {
  if (editorToggle) {
    edit.touchEnded();
  } else {
    for (int i = 0; i < gWidgets.size(); i++) {
      gWidgets.get(i).click();
    }
  }

  if (menu != null) {
    menu.click();
  }
}

void touchMoved() {
  if (menu == null) {
    if (editorToggle) {
      edit.touchMoved();
    } else {
      gController.touchMoved();
    }
  }
}

void onPinch(float x, float y, float d) {
  if (menu == null) {
    if (editorToggle) {
      edit.onPinch(x, y, d);
    } else {
      gController.onPinch(x, y, d);
    }
  }
}

void onTap (float x, float y) {
  if (menu == null) {
    if (editorToggle) {
      edit.onTap(x, y);
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



//------------------LevelToScreenConverter---------------------
class Converter {
  private float currentScale;
  private float currentSubScale;
  private PVector currentCenter;

  private PVector lastCalc;

  public Converter() {
    lastCalc = new PVector(0, 0);
  }

  public PVector screenToLevel(float screenX, float screenY) {
    currentScale = gCamera.getScale();
    currentSubScale = gCamera.getSubScale();
    currentCenter = gCamera.getCenter();
    lastCalc.x = ((screenX-width/2)/((float)width/currentScale)/currentSubScale) + currentCenter.x;
    lastCalc.y = ((screenY-height/2)/((float)width/currentScale)/currentSubScale) + currentCenter.y;
    return lastCalc;
  }

  public float screenToLevel(float distance) {
    currentScale = gCamera.getScale();
    currentSubScale = gCamera.getSubScale();
    float result = distance/((float)width/currentScale)/currentSubScale;
    return result;
  }

  public float getScale() {
    //return (((float)width/currentScale)/currentSubScale);
    return currentScale/currentSubScale/100; //how many square is the width of the screen
  }

  public float getTotalFromScale(float scale) { //calculate total scale from potential scale
    return scale/currentSubScale/100;
  }

  public float getScaleFromTotal(float totalScale) {
    return totalScale*currentSubScale*100;
  }
  //public PVector levelToScreen(float levelX, levelY){

  //}
}

//------------------Vibe---------------------
class Vibe {
  private Vibrator vibe;
  private boolean deprecated;

  public Vibe() {
    vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); 
    deprecated = android.os.Build.VERSION.SDK_INT < 26;// && vibe.hasVibrator();
    //this class needs to be updated to calculate fine grained vibration strength using a combination of amount and level
  }

  @SuppressWarnings("deprecation")
    public void vibrate(long amount) {
    //amount = duration
    if (!deprecated) {
      vibe.vibrate(VibrationEffect.createOneShot(amount, 255));
    } else {
      //this is for older versions of anroid
      //need to make a second version of vibration tuned for older systems
      vibe.vibrate(amount);
    }
  }

  @SuppressWarnings("deprecation")
    public void vibrate(long amount, int level) {
    //amount = duration
    //level = intensity
    if (!deprecated) {
      vibe.vibrate(VibrationEffect.createOneShot(amount, level));
    } else {
      //this is for older versions of anroid
      //need to make a second version of vibration tuned for older systems
      vibe.vibrate(amount);
    }
  }
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
