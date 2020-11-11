import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;
import ketai.ui.KetaiGesture;

KetaiGesture k;

Game g; //holds the game class
Camera c; //holds the camera
Controller con; //holds the current controller
float minZoom = 200;
float maxZoom = 20000;
boolean gPaused = false; //is the game class paused
private ArrayList<Widget> widgets = new ArrayList<Widget>();
private float widgetSpacing; //size of gap between widgets
private boolean editorToggle = false; //is the game in editor mode
private Menu menu;

//touch screen stuff
//private TouchTesting testing = new TouchTesting();
private ArrayList<PVector> touch = new ArrayList<PVector>();
private PVector lastTouch = new PVector(0, 0);

void setup() {
  //setup graphics
  fullScreen(P2D);
  //fullScreen(OPENGL);
  frameRate(60);

  //make game and widgets
  c = new GameCamera();
  g = new Game(c);
  con = new GameControl();
  MenuWidget menuW = new MenuWidget();
  widgets.add(menuW);
  PauseWidget pauseW = new PauseWidget();
  widgets.add(pauseW);
  PlayerWidget playW = new PlayerWidget();
  widgets.add(playW);
  CameraWidget cameraW = new CameraWidget();
  widgets.add(cameraW);
  BlockWidget blockW = new BlockWidget();
  widgets.add(blockW);
  widgetSpacing = width/(widgets.size()+1);
  
  k = new KetaiGesture(this);
}

void draw() {
  if (!gPaused) { //step the game if it is not paused
    //playerDirection(); //update player left right controls
    con.draw();
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

  //draw the widgets
  for(int i = 0; i < widgets.size(); i++){
    if(i > 0 && !editorToggle){ //don't draw editor widgets if in game mide
      continue;
    }
    widgets.get(i).draw(widgetSpacing*(i+1), 120);
    widgets.get(i).active();
    widgets.get(i).hover(lastTouch);
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
  
  if(menu == null){
    con.touchStarted();
  }
}

void touchEnded() {
  //check for clicking on widgets
  for(int i = 0; i < widgets.size(); i++){
    if(i > 0 && !editorToggle){ //don't click editor widgets if in game mide
      continue;
    }
    widgets.get(i).click();
  }
  //check for clicking on menu
  if (menu != null) {
    menu.click();
  }
}

void touchMoved(){
  if(menu == null){
    con.touchMoved();
  }
}

void onPinch(float x, float y, float d){
  if(menu == null){
    con.onPinch(x,y,d);
  }
}

void playerDirection() {
  int left = 0;
  int right = 0;
  for (TouchEvent.Pointer t : touches) {
    if (t.y >=  height/3) {
      if (t.x < width/4) {
        left++;
      }
      if (t.x > (width/4)*3) {
        right++;
      }
    }
  }
  if (left > right) {
    g.player.left();
  } else if (left < right) {
    g.player.right();
  } else {
    g.player.still();
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
