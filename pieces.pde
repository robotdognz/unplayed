//------------------LevelPiece---------------------
class Piece extends Rectangle {
  private boolean hasTexture;
  private PieceHandler pieceTexture;
  //private PImage sprite;

  Piece(File file, int x, int y, int pieceW, int pieceH) {
    super(x, y, pieceW, pieceH);

    if (file != null && texture.getPieceMap().containsKey(file)) {
      this.pieceTexture = texture.getPieceMap().get(file);
      //this.sprite = pieceTexture.getSprite();
      setWidth(pieceTexture.getWidth());
      setHeight(pieceTexture.getHeight());
      hasTexture = true;
    } else {
      //set sprite to file not found image
      hasTexture = false;
    }
  }

  public void draw() {
    if (hasTexture) {
      image(pieceTexture.getSprite(), getX(), getY(), getWidth(), getHeight());
    } else {
      //display missing texture texture
    }
  }

  public File getFile() {
    return pieceTexture.getFile();
  }
}

//------------------Platform---------------------
class Platform extends Rectangle {
  private boolean hasTexture;
  private TileHandler tileTexture;
  //private PImage sprite;

  public Platform(File file, int x, int y) { //, int platformW, int platformH
    super(x, y, 100, 100);

    if (file != null && texture.getTileMap().containsKey(file)) {
      this.tileTexture = texture.getTileMap().get(file);
      //this.sprite = tileTexture.getSprite();
      //setWidth(100);
      //setHeight(100);
      hasTexture = true;
    } else {
      //set sprite to file not found image
      hasTexture = false;
      //sprite = texture.defaultBlock;
    }
  }

  public void draw() {
    //for (int i = 0; i < getHeight(); i+=100) {
    //for (int j = 0; j < getWidth(); j+=100) {
    if (hasTexture) {
      image(tileTexture.getSprite(), getX(), getY(), getWidth(), getHeight()); //image(tileTexture.getSprite(), getX()+j, getY()+i, 100, 100);
    } else {
      //display missing texture texture
    }
    //}
    //}
  }

  public File getFile() {
    return tileTexture.getFile();
  }
}
