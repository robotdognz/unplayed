//------------------LevelPiece---------------------
class Piece extends Rectangle {
  private PieceHandler pieceTexture;
  private PImage sprite;

  Piece(File file, int x, int y, int pieceW, int pieceH) {
    super(x, y, pieceW, pieceH);

    if (file != null && texture.getPieceMap().containsKey(file)) {
      this.pieceTexture = texture.getPieceMap().get(file);
      this.sprite = pieceTexture.getSprite();
      setWidth(pieceTexture.getWidth());
      setHeight(pieceTexture.getHeight());
    } else {
      //set sprite to file not found image
    }
  }

  public void draw() {
    image(sprite, getX(), getY(), getWidth(), getHeight());
  }

  public File getFile() {
    return pieceTexture.getFile();
  }
}

//------------------Platform---------------------
class Platform extends Rectangle {
  private TileHandler tileTexture;
  private PImage sprite;

  public Platform(File file, int x, int y, int platformW, int platformH) {
    super(x, y, platformW, platformH);

    if (file != null && texture.getTileMap().containsKey(file)) {
      this.tileTexture = texture.getTileMap().get(file);
      this.sprite = tileTexture.getSprite();
      setWidth(100);
      setHeight(100);
    } else {
      //set sprite to file not found image
      sprite = texture.defaultBlock;
    }
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

  public File getFile() {
    return tileTexture.getFile();
  }
}
