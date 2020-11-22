//------------------LevelPiece---------------------
class Piece extends Rectangle {
  private PImage sprite;
  
  Piece(PImage sprite, int x, int y, int pieceW, int pieceH) {
    super(x, y, pieceW, pieceH);
    this.sprite = sprite;
  }
  
  public void draw() {
    image(sprite, getX(), getY(), getWidth(), getHeight());
  }
}

//------------------Platform---------------------
class Platform extends Rectangle {
  private PImage sprite;

  public Platform(int x, int y, int platformW, int platformH) {
    super(x, y, platformW, platformH);
    sprite = texture.defaultBlock;
  }

  public void draw() {
    for (int i = 0; i < getHeight(); i+=100) {
      for (int j = 0; j < getWidth(); j+=100) {
        image(sprite, getX()+j, getY()+i, 100, 100);
      }
    } 
    //textureMode(NORMAL);
    //textureWrap(REPEAT);
    //int rX = (int)(platformW/100);
    //int rY = (int)(platformH/100);
    //beginShape();
    //texture(sprite);
    //vertex(x, y, 0, 0); //top left
    //vertex(x+platformW, y, rX, 0); //top right
    //vertex(x+platformW, y+platformH, rX, rY); //bottom right
    //vertex(x, y+platformH, 0, rY); //bottom left
    //endShape();
  }
}
