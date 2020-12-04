class Page {
  private Game game;
  private Rectangle view; //the page's view into the world
  private HashSet<Rectangle> pageObjects;
  private HashSet<String> excludedObjects; //a list of rectangle strings to exclude while drawing

  private PGraphics pageGraphics;
  private PVector position; //center of the page in page view
  private float size; //size of the page in page view
  private float angle; //rotation of the page in page view
  Rectangle angledRect; //an axis locked rectangle that contains the rotated page (used to check if page is on screen)
  //variables for flipping the page
  private float flipX;
  private float flipY;

  public Page(Game game, PVector topLeft, PVector bottomRight, PVector position, float size, float angle, boolean flipH, boolean flipV) {
    this.game = game;
    float rWidth = bottomRight.x-topLeft.x;
    float rHeight = bottomRight.y-topLeft.y;
    this.view = new Rectangle(topLeft.x, topLeft.y, rWidth, rHeight);
    this.pageObjects = new HashSet<Rectangle>();
    this.excludedObjects = new HashSet<String>();

    this.pageGraphics = createGraphics((int)rWidth, (int)rHeight, P2D);
    this.position = position;
    this.size = size;
    this.angle = angle;
    //to be implemented later
    //angledRect //calculate a rectangle that the angled page fits inside
    
    if(flipH){
      flipX = -1;
    }else{
      flipX = 1;
    }
    if(flipV){
      flipY = -1;
    }else{
      flipY = 1;
    }
  }

  public void exclude(Rectangle object) {
    excludedObjects.add(object.toString());
  }

  public void draw(float scale) {
    ArrayList<Rectangle> drawFirst = new ArrayList<Rectangle>();
    ArrayList<Rectangle> drawSecond = new ArrayList<Rectangle>();
    for (Rectangle r : pageObjects) {
      boolean excluded = false;
      for (String s : excludedObjects) { //check the rectangle against the excluded list
        if (r.toString().equals(s)) {
          excluded = true;
        }
      }
      if (!excluded) { //if the rectangle is not on the excluded list
        if (r instanceof Image) {
          drawFirst.add(r);
        } else {
          drawSecond.add(r);
        }
      }
    }
    
    //begin drawing on the page
    pageGraphics.beginDraw();
    
    pageGraphics.translate(-view.getX(), -view.getY());

    //draw environment and player
    pageGraphics.background(240);

    for (Rectangle r : drawFirst) { //draw pieces
      if (r instanceof Image) {
        ((Image) r).draw(pageGraphics, scale/size);
      }
    }
    for (Rectangle r : drawSecond) { //draw tiles and events
      if (r instanceof Tile) {
        ((Tile) r).draw(pageGraphics, scale/size); //scale is divided by size so that LODs are relative to page size
      }
      if (r instanceof Event) {
        ((Event) r).draw(pageGraphics, scale/size);
      }
    }

    game.player.draw(pageGraphics);
    game.paper.draw(pageGraphics, view, scale/size);
    //end drawing on the page
    pageGraphics.endDraw();
    
    //draw the page
    imageMode(CENTER);
    pushMatrix();
    translate(position.x, position.y);
    
    scale(size); //size the page will appear in the page view
    rotate(radians(angle)); //angle of the page
    scale(flipX, flipY); //flipping the page
    image(pageGraphics, 0, 0, pageGraphics.width, pageGraphics.height); //draw the page
    popMatrix();
  }

  public void step() {
    //get objects visible to this page
    pageObjects.clear();
    game.world.retrieve(pageObjects, view);
    //the step and draw process could be optimised by getting pageObjects once when the level is run
  }

  public Set<String> getExcluded() {
    return Collections.unmodifiableSet(excludedObjects);
  }
  
  public Rectangle getView(){
    return view;
  }
}

class PageView {
  private ArrayList<Page> pages;

  //world camera
  private Camera camera;

  public PageView(Camera camera) {
    this.camera = camera;
    pages = new ArrayList<Page>();
  }

  public void draw() {
    pushMatrix(); //start working at game scale
    translate(width/2, height/2); //set x=0 and y=0 to the middle of the screen

    //camera
    scale((float)width/(float)camera.getScale()); //width/screen fits the level scale to the screen
    scale(camera.getSubScale()); //apply offset for tall screen spaces
    translate(-camera.getCenter().x, -camera.getCenter().y); //moves the view around the level

    float currentScale = convert.getScale();
    
    background(100);

    for (Page p : pages) {
      p.draw(currentScale);
    }
    popMatrix();
  }

  public void step() {
    for (Page p : pages) {
      p.step();
    }
  }
  
  public void addPage(Page page){
    pages.add(page);
  }
  
  public List<Page> getPages(){
    return Collections.unmodifiableList(pages);
  }
}
