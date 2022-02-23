import game.AppLogic;
import android.content.Context;
import android.app.Activity;
import android.view.*;
import android.os.Bundle;
import android.content.Intent;

private static final int SELECT_LEVEL = 1;

Activity activity;
Context context;
AppLogic app; //manages the application level game logic

//splash screen
int splash; //steps through the start up stages 
PImage splashScreen; //the splash screen to be drawn while the game is loading

//delta time
final float FPS = 60;

long last_time;

void setup() {
  //setup graphics
  fullScreen(P2D);
  background(0, 78, 83);
  frameRate(FPS);
  splash = 0;
  last_time = System.nanoTime();
}

void init() {
  //setup game logic
  activity = this.getActivity();
  context = activity.getApplicationContext();
  app = new AppLogic(this, activity, context);

  //check and get permissions
  //if (!hasPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
  //  requestPermission("android.permission.WRITE_EXTERNAL_STORAGE");
  //}

  //initalise the game
  AppLogic.init();
}

//this is the only draw method that should have step logic in it
void draw() {
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
    int size = (int) (width*0.8);
    image(splashScreen, width/2, height/2, size, size);
    imageMode(CORNER);
    splash = 2;
    return;
  } else if (splash == 2) { //initalize game
    delay(736);
    init();
    splash = 3;
    return;
  }

  //testing.draw(); //draw touch events
  AppLogic.draw(delta_time);
}

@Override public String sketchPath(String where) {
  // just asking, not creating or checking permissions or existence...
  // ... by using File dependencies with unknown side effects.
  if (sketchPath != null&&where.length()>1)
  {
    // println("sp: "+where+" "+sketchPath); 
    if ('/'==where.charAt(0))
      return where;
    return sketchPath+"/"+where;
  }
  // nonsense, bwtf...
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

void touchStarted() {
  AppLogic.touchStarted();
}

void touchEnded() {
  AppLogic.touchEnded();
}

void touchMoved() {
  AppLogic.touchMoved();
}

void onTap (float x, float y) {
  AppLogic.onTap(x, y);
}

void onDoubleTap(float x, float y) {
  AppLogic.onDoubleTap(x, y);
}

void onFlick(float x, float y, float px, float py, float v) {
  AppLogic.onFlick(x, y, px, py, v);
}

void onLongPress(float x, float y) {
  AppLogic.onLongPress(x, y);
}

void onPinch(float x, float y, float d) {
  AppLogic.onPinch(x, y, d);
}


void onRotate(float x, float y, float angle) {
  AppLogic.onRotate(x, y, angle);
}

@SuppressWarnings("static-access")
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == this.getActivity().RESULT_OK) {
      if (requestCode == SELECT_LEVEL) {
        AppLogic.setUri(data.getData());
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
