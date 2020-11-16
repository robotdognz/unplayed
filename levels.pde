abstract class Level {
  protected ArrayList<Platform> platforms;
  protected ArrayList<Event> events;
  protected PVector playerStart;
  protected int startScale;
  protected PVector startCenter;
  protected int bottomOfTopBar;
  protected int topOfBottomBar;
  
  public Level(){
    platforms = new ArrayList<Platform>();
    events = new ArrayList<Event>();
    playerStart = new PVector(0, 0);
    
    buildLevel();
  }
  
  private void buildLevel(){}
  
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

//------------------BlankLevel---------------------
class BlankLevel extends Level {
  
  BlankLevel() {
    platforms = new ArrayList<Platform>();
    events = new ArrayList<Event>();
  
    buildLevel();
  }
  
  private void buildLevel() {
    playerStart = new PVector(50, 0);
    PVector cameraTopLeft = new PVector(-400, -400);
    PVector cameraBottomRight = new PVector(400, 400);
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.startCenter = new PVector(centerX, centerY);
    this.startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
    this.bottomOfTopBar = (int)cameraTopLeft.y;
    this.topOfBottomBar = (int)cameraBottomRight.y;
    
    platforms.add(new Platform(0, 100, 100, 100));
  }
}

//------------------Level1---------------------
class Level1 extends Level {

  Level1() {
    platforms = new ArrayList<Platform>();
    events = new ArrayList<Event>();
  
    buildLevel();
  }

  private void buildLevel() {
    playerStart = new PVector(-200, 1000);
    PVector cameraTopLeft = new PVector(-600, 800);
    PVector cameraBottomRight = new PVector(0, 1300);
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.startCenter = new PVector(centerX, centerY);
    this.startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
 
    this.bottomOfTopBar = (int)cameraTopLeft.y;
    this.topOfBottomBar = (int)cameraBottomRight.y;
    
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
}

//------------------Level2---------------------
class Level2 extends Level {

  Level2() {
    platforms = new ArrayList<Platform>();
    events = new ArrayList<Event>();
    playerStart = new PVector(-650, 1600);

    PVector cameraTopLeft = new PVector(-700, 1500);
    PVector cameraBottomRight = new PVector(-100, 2100);
    
    //temp
    //cameraTopLeft = new PVector(-1800, 300);
    //cameraBottomRight = new PVector(1800, 2300);
    
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.startCenter = new PVector(centerX, centerY);
    this.startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);

    this.bottomOfTopBar = (int)cameraTopLeft.y;
    this.topOfBottomBar = (int)cameraBottomRight.y;

    buildLevel();
  }
  
  private void buildLevel() {
    //platforms
    platforms.add(new Platform(-1400, 2000, 1400*2, 100));
    platforms.add(new Platform(-800, 500, 100, 1500));
    platforms.add(new Platform(-700, 1700, 100, 300));
    platforms.add(new Platform(600, 1300, 100, 700));
    
    platforms.add(new Platform(-500, 1700, 1000, 200));
    
    platforms.add(new Platform(-700, 1500, 600, 100));
    platforms.add(new Platform(100, 1500, 400, 100));

    platforms.add(new Platform(-700, 1100, 100, 400));
    platforms.add(new Platform(-700, 700, 100, 300));
    platforms.add(new Platform(-500, 1300, 1100, 100));
    platforms.add(new Platform(-500, 700, 100, 500));
   
    platforms.add(new Platform(-1000, 500, 200, 1200));
    
    platforms.add(new Platform(-400, 500, 100, 700));
    platforms.add(new Platform(-300, 500, 100, 500));
    platforms.add(new Platform(-200, 600, 100, 700));


    //events
    events.add(new CameraChange(-300, 1900, 100, 100, new PVector(-700, 1300), new PVector(700, 2100), 2, 1)); //0.06, 0.03
    events.add(new CameraChange(-600, 1400, 100, 100, new PVector(-700, 700), new PVector(-400, 1600), 2.5, 0)); //0.05, 0.5
    
    events.add(new CameraChange(-540, 1200, 10, 100, new PVector(-700, 700), new PVector(-400, 1600), 2.5, 0.5)); //0.06, 0.5
    events.add(new CameraChange(-440, 1200, 10, 100, new PVector(-800, 700), new PVector(-300, 1600), 2.5, 0.5)); //
    events.add(new CameraChange(-340, 1200, 10, 100, new PVector(-900, 700), new PVector(-200, 1600), 2.5, 0.5)); //
    events.add(new CameraChange(-240, 1200, 10, 100, new PVector(-1000, 700), new PVector(-100, 1600), 2.5, 0.5)); //
    
    events.add(new CameraChange(-500, 600, 100, 100, new PVector(-1800, 300), new PVector(1800, 2300), 2.5, 2.5)); //0.04, 0.05
    
  }
}
