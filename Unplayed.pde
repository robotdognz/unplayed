Game g;

void setup() {
  fullScreen(OPENGL);
  frameRate(60);
  g = new Game();
}

void draw() {
  g.draw();
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
