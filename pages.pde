class Page {
  Game game;
  Rectangle view; //the page's view into the world
  HashSet<Rectangle> pageObjects;

  PVector position; //center of the page in page view
  float size; //size of the page in page view
  float angle; //rotation of the page in page view
  
  //PGraphics //need to figure out if these can be rotated

  public Page(Game game, PVector topLeft, PVector bottomRight, PVector position, float size) {
    this.game = game;
    float rWidth = bottomRight.x-topLeft.x;
    float rHeight = bottomRight.y-topLeft.y;
    this.view = new Rectangle(topLeft.x, topLeft.y, rWidth, rHeight);
    this.pageObjects = new HashSet<Rectangle>();
    this.position = position;
    this.size = size;
    this.angle = 0; //to be implemented later
  }

  public void draw(float scale) {
    //draw the page
    
    //draw player and environment
    background(240);
    for (Rectangle p : pageObjects) { //draw pieces
      if (p instanceof Piece) {
        ((Piece) p).draw(scale);
      }
    }
    for (Rectangle p : pageObjects) { //draw tiles and events on top of pieces
      if (p instanceof Tile) {
        ((Tile) p).draw(scale);
      }
      if (p instanceof Event) {
        ((Event) p).draw(scale);
      }
    }
    game.player.draw();
    game.paper.draw(view, scale);
  }

  public void step() {
    //get objects visible to this page
    pageObjects.clear();
    game.world.retrieve(pageObjects, view);
  }
}

//page view class

//arraylist of pages

//draw()

//step()
