class Page {
  Game game;
  Rectangle view; //the page's view into the world
  HashSet<Rectangle> pageObjects;
  HashSet<String> excludedObjects; //a list of rectangle strings to exclude while drawing

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
    this.position = position;
    this.size = size;

    //to be implemented later
    this.angle = 0; 
    //angledRect //calculate a rectangle that the angled page fits inside
  }
  
  public void exclude(Rectangle object){
    excludedObjects.add(object.toString());
  }

  public void draw(float scale) {
    //draw the page

    //draw environment and player
    background(240);

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

    for (Rectangle r : drawFirst) { //draw pieces
      if (r instanceof Piece) {
        ((Piece) r).draw(scale);
      }
    }
    for (Rectangle r : drawSecond) { //draw tiles and events
      if (r instanceof Tile) {
        ((Tile) r).draw(scale);
      }
      if (r instanceof Event) {
        ((Event) r).draw(scale);
      }
    }

    game.player.draw();
    game.paper.draw(view, scale);
  }

  public void step() {
    //get objects visible to this page
    pageObjects.clear();
    game.world.retrieve(pageObjects, view);
    //the step and draw process could be optimised by getting pageObjects once when the level is run
  }
}

class PageView {
  //arraylist of pages
  private ArrayList<Page> pages;

  public PageView() {
    pages = new ArrayList<Page>();
  }

  public void draw(float scale) {
    for(Page p : pages){
      p.draw(scale);
    }
  }

  public void step() {
  }
}
