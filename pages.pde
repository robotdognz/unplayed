class Page {
  Game game;
  Rectangle view; //the page's view into the world
  HashSet<Rectangle> pageObjects;
  HashSet<String> excludedObjects; //a list of rectangle strings to exclude while drawing

  PGraphics pageGraphics;
  PVector position; //center of the page in page view
  float size; //size of the page in page view
  float angle; //rotation of the page in page view
  Rectangle angledRect; //a rectangle that contains the rotated page

  //PGraphics //need to figure out if these can be rotated

  public Page(Game game, PVector topLeft, PVector bottomRight, PVector position, float size) {
    this.game = game;
    float rWidth = bottomRight.x-topLeft.x;
    float rHeight = bottomRight.y-topLeft.y;
    this.view = new Rectangle(topLeft.x, topLeft.y, rWidth, rHeight);
    this.pageObjects = new HashSet<Rectangle>();
    this.excludedObjects = new HashSet<String>();

    this.pageGraphics = createGraphics((int)rWidth, (int)rHeight, P2D);
    this.position = position;
    this.size = size;
    //to be implemented later
    //this.angle = 0; 
    //angledRect //calculate a rectangle that the angled page fits inside
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
        if (r instanceof Piece) {
          drawFirst.add(r);
        } else {
          drawSecond.add(r);
        }
      }
    }
    
    //begin drawing on the page
    pageGraphics.beginDraw();
    
    pageGraphics.translate(view.getX()+view.getWidth()/2, view.getY()+view.getHeight()/2);

    //draw environment and player
    pageGraphics.background(240);

    for (Rectangle r : drawFirst) { //draw pieces
      if (r instanceof Piece) {
        ((Piece) r).draw(pageGraphics, scale);
      }
    }
    for (Rectangle r : drawSecond) { //draw tiles and events
      if (r instanceof Tile) {
        ((Tile) r).draw(pageGraphics, scale);
      }
      if (r instanceof Event) {
        ((Event) r).draw(pageGraphics, scale);
      }
    }

    game.player.draw(pageGraphics);
    game.paper.draw(pageGraphics, view, scale);
    //end drawing on the page
    pageGraphics.endDraw();
    
    imageMode(CENTER);
    pushMatrix();
    scale(size);
    image(pageGraphics, position.x, position.y, pageGraphics.width, pageGraphics.height);
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
  //arraylist of pages
  private ArrayList<Page> pages;

  //editor camera
  private Camera camera;
  private float scale;
  private float subScale = 1; //defaults to 1
  private PVector center;

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

  public float getScale() {
    return scale;
  }
  public float getSubScale() {
    return subScale;
  }
  public PVector getCenter() {
    return center;
  }
  public void setScale(float scale) {
    this.scale = scale;
  }
  public void setSubScale(float subScale) {
    this.subScale = subScale;
  }
  public void setCenter(float x, float y) {
    this.center.x = x;
    this.center.y = y;
  }
}
