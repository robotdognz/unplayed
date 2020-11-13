import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;
import ketai.ui.KetaiGesture;

KetaiGesture k;

Game g; //holds the game class
Camera c; //holds the camera
Controller con; //holds the current controller
boolean controllerActive = true; //is the current controller active
Editor edit; //holds the editor

float minZoom = 200;
float maxZoom = 20000;
boolean gPaused = false; //is the game class paused
private ArrayList<Widget> widgets = new ArrayList<Widget>();
private float widgetSpacing; //size of gap between widgets
private boolean editorToggle = true; //is the game in editor mode
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
  c = new GameCamera();
  g = new Game(c);
  con = new PlayerControl();

  //setup widgets
  Widget menuW = new MenuWidget();
  Widget settingsW = new SettingsWidget();
  Widget controlW = new ControlWidget();
  Widget editTypeW = new EditorTypeWidget(); //edit type
  Widget editModeW = new EditorModeWidget(); //edit mode
  Widget extraW = new ExtraWidget(); //extra
  widgets.add(menuW);
  widgets.add(settingsW);
  widgets.add(controlW);
  widgets.add(editTypeW);
  widgets.add(editModeW);
  widgets.add(extraW);
  widgetSpacing = width/(widgets.size()+1);

  k = new KetaiGesture(this);
}

void draw() {
  if (!gPaused) { //step the game if it is not paused
    if(controllerActive){
      con.draw(); //draw event for controls
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
  
  //widget menus - draw them and close them is lastTouch is below longest open widget menu
  float currentWidgetHeight = 0;  
  boolean wMenuOpen = false; 
  for(int i = 0; i < widgets.size(); i++){
    if(widgets.get(i).isActive()){
      ArrayList<Widget> children = widgets.get(i).getChildren();
      if(children.size() > 0){
        wMenuOpen = true;
        float current = children.get(children.size()-1).getPosition().y;
        if(current > currentWidgetHeight){
          currentWidgetHeight = current;
        }
      }
    }
    if (i > 0 && !editorToggle) { //don't draw editor widgets if in game mode - only needed until editor class is implemented with its own menu widget
      continue;
    }
    widgets.get(i).draw(widgetSpacing*(i+1), 120);
    widgets.get(i).updateActive();
    if(menu == null){
      widgets.get(i).hover(lastTouch);
    }
  }
  currentWidgetHeight += widgets.get(0).getSize()*1.5; //add a little on to the bottom
  if(wMenuOpen && lastTouch.y > currentWidgetHeight || menu != null){
    for(Widget w: widgets){
      if(w.isMenu()){
        w.deactivate();
      }
    }
  }
  controllerActive = !wMenuOpen; //is a menu is open, deactivate controls

  //draw the menu
  if (menu != null) { 
    menu.draw();
    menu.hover(lastTouch);
  }

  //draw frame counter
  if (frameDelay > 30) {
    frame = frameRate;
    frameDelay = 0;
  } else {
    frameDelay++;
  }
  fill(255);
  textSize(50);
  textAlign(CENTER, CENTER);
  text(nf(frame, 2, 2), width/2, height-50);
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
    if(controllerActive){
      con.touchStarted(); //controlls for touch started event
    }
  }
}

void touchEnded() {
  //check for clicking on widgets
  for (int i = 0; i < widgets.size(); i++) {
    if (i > 0 && !editorToggle) { //don't click editor widgets if in game mide
      continue;
    }
    widgets.get(i).click();
  }
  //check for clicking on menu
  if (menu != null) {
    menu.click();
  }
}

void touchMoved() {
  if (menu == null) {
    if(controllerActive){
      con.touchMoved(); //controlls for touch moved event
    }
  }
}

void onPinch(float x, float y, float d) {
  if (menu == null) {
    if(controllerActive){
      con.onPinch(x, y, d); //controlls for on pinch event
    }
  }
}

//interfaces
interface Level {
  public PVector getPlayerStart();
  public ArrayList<Platform> getPlatforms();
  public ArrayList<Event> getEvents();
  public int getStartScale();
  public PVector getStartCenter();
  public int getTopBar();
  public int getBottomBar();
}
interface Event {
  public PVector getTopLeft();
  public PVector getBottomRight();
  public String getType();
  public void activate(Game g);
  public void draw();
}

//------------------Vibe---------------------
class Vibe {
  Vibrator vibe;

  public Vibe() {
    vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); 
    //this class needs to be updated to calculate fine grained vibration strength using a combination of amount and level
  }

  public void vibrate(long amount) {
    //amount = duration
    if (android.os.Build.VERSION.SDK_INT > 26 && vibe.hasVibrator()) {
      vibe.vibrate(VibrationEffect.createOneShot(amount, 255));
    } else {
      //this is for older versions of anroid
      //need to make a second version of vibration tuned for older systems
      vibe.vibrate(amount);
    }
  }

  public void vibrate(long amount, int level) {
    //amount = duration
    //level = intensity
    if (android.os.Build.VERSION.SDK_INT > 26 && vibe.hasVibrator()) {
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
