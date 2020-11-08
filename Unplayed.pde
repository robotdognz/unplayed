import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import android.app.Activity;

Game g; //holds the game class
Camera c;
boolean gPaused = false; //is the game class paused
private ArrayList<Widget> widgets = new ArrayList<Widget>();
private Menu menu;

//touch screen stuff
//private TouchTesting testing = new TouchTesting();
private ArrayList<PVector> touch = new ArrayList<PVector>();
private PVector lastTouch = new PVector(0, 0);

void setup() {
  //setup graphics
  fullScreen(OPENGL);
  frameRate(60);

  //make game and widgets
  c = new GameCamera();
  g = new Game(c);
  Pause p = new Pause();
  widgets.add(p);
}

void draw() {
  if (!gPaused) { //step the game if it is not paused
    playerDirection(); //update player left right controls
    g.step(); //step game
  }
  g.draw(); //draw the game

  //draw touch events
  //testing.draw();
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

  for (Widget w : widgets) { //draw the widgets
    w.draw();
    w.hover(lastTouch);
  }
  if (menu != null) { //draw the menus
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

  //player jumping
  if (!gPaused) {
    //jump if the last true touch was in the middle of the screen
    if (lastTouch.y >= height/3 && 
    lastTouch.x > width/4 && 
    lastTouch.x < (width/4)*3) {
      g.player.jump();
    }
  }
}

void touchEnded() {
  //check for clicking on widgets
  for (Widget w : widgets) {
    w.click();
  }
  //check for clicking on menu
  if (menu != null) {
    menu.click();
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
