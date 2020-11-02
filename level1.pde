class Level1 implements Level {
  private ArrayList<Platform> platforms;
  private ArrayList<Event> events;
  private PVector playerStart;
  private int startScale;
  private PVector startCenter;
  private int bottomOfTopBar;
  private int topOfBottomBar;

  Level1() {
    platforms = new ArrayList<Platform>();
    events = new ArrayList<Event>();
    playerStart = new PVector(-200, 1000);
    
    PVector cameraTopLeft = new PVector(-600, 800);
    PVector cameraBottomRight = new PVector(0, 1300);
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.startCenter = new PVector(centerX, centerY);
    this.startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
 
    this.bottomOfTopBar = (int)cameraTopLeft.y;
    this.topOfBottomBar = (int)cameraBottomRight.y;
    
    buildLevel();
  }

  private void buildLevel() {
    platforms.add(new Platform(-150, 1000, 200, 100));
    platforms.add(new Platform(-1400, 1300, 1400*3, 100)); //100
    platforms.add(new Platform(-350, 1100, 200, 100));

    platforms.add(new Platform(50, 1150, 200, 100)); 
    platforms.add(new Platform(330, 1090, 200, 100));

    platforms.add(new Platform(-660, 1120, 200, 100));
    platforms.add(new Platform(-50, 800, 100, 100));
    platforms.add(new Platform(600, 600, 100, 100));

    platforms.add(new Platform(-600, 600, 100, 100));
    platforms.add(new Platform(-400, 600, 100, 100));
    platforms.add(new Platform(-600, 700, 300, 100));
    //solution to the puzzle: put player into this slot to complete the pattern
    //later puzzles will include pattern colours being different, maybe orientation, etc.
    
    //zoom testing
    platforms.add(new Platform(-600, -8000, 100, 100));
    platforms.add(new Platform(200, 9900, 100, 100));

    events.add(new CameraChange(-600, 800, 100, 100, new PVector(-700, 200), new PVector(700, 1900), 2, 2)); //0.05, 0.1
    events.add(new CameraChange(600, 500, 100, 100, new PVector(-700, -8000), new PVector(700, 10000), 3, 3)); //0.03, 0.023
    
  }
  
  public PVector getPlayerStart() {
    return playerStart;
  }
  public ArrayList<Platform> getPlatforms() {
    return platforms;
  }
  public ArrayList<Event> getEvents() {
    return events;
  }
  public int getStartScale() {
    return startScale;
  }
  public PVector getStartCenter() {
    return startCenter;
  }
  public int getTopBar() {
    return this.bottomOfTopBar;
  }
  public int getBottomBar() {
    return this.topOfBottomBar;
  }
}
