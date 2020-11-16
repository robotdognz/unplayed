import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;
import ketai.ui.KetaiGesture;

KetaiGesture k;

Game g; //holds the game class
Camera gCamera; //holds the game camera
Controller gController; //holds the current controller
//boolean controllerActive = true; //is the current controller active
Editor edit; //holds the editor

//float minZoom = 200;
//float maxZoom = 20000;
boolean gPaused = false; //is the game class paused
private ArrayList<Widget> gWidgets = new ArrayList<Widget>();
private float gWidgetSpacing; //size of gap between widgets
private boolean editorToggle = true; //is the game in editor mode            //TODO: implement this
private Menu menu;

//touch screen stuff
//private TouchTesting testing = new TouchTesting();
private ArrayList<PVector> touch = new ArrayList<PVector>();
private PVector lastTouch = new PVector(0, 0);

//frame count
int frameDelay = 100;
float frame;

void setup() {
  //setup graphics
  fullScreen(P2D);
  frameRate(60);

  //setup game
  gCamera = new GameCamera();
  g = new Game(gCamera);            //currently game does not have access to editor camera, this is bad
  gController = new PlayerControl();
  edit = new Editor(g);

  ////setup non editor widgets
  Widget menuW = new MenuWidget(edit);
  gWidgets.add(menuW);
  gWidgetSpacing = width/(gWidgets.size()+1);

  k = new KetaiGesture(this);
}

void draw() {
  if (!gPaused) { //step the game if it is not paused
    //step editor or game controller depending on editor toggle
    if (editorToggle) {
      edit.step();
    } else {
      gController.draw();
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

//------------------Vibe---------------------
class Vibe {
  private Vibrator vibe;
  private boolean deprecated;
  
  public Vibe() {
    vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); 
    deprecated = android.os.Build.VERSION.SDK_INT >= 26 && vibe.hasVibrator();
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
