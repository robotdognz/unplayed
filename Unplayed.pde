Game g;

//touch screen stuff
private ArrayList<PVector> touch = new ArrayList<PVector>();
private PVector lastTouch = new PVector(0, 0);

void setup() {
  fullScreen(OPENGL);
  frameRate(60);
  g = new Game();
}

void draw() {
  g.draw();
}

void touchStarted() {
    //default last touch is end of touch array
    //lastTouch = new PVector(touches[touches.length-1].x, touches[touches.length-1].y);

    //find true last touch
    if (touches.length >= touch.size() && touches.length > 1) {
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

    //jump if the last true touch was in the middle of the screen
    if (lastTouch.x > width/4 && lastTouch.x < (width/4)*3) {
      g.player.jump();
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
