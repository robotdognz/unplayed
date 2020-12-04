//------------------LevelPiece---------------------
class Image extends Rectangle {
  private boolean hasTexture;
  private PieceHandler pieceTexture;
  //rotation
  //vetrtical flip
  //horazontal flip

  Image(File file, int x, int y, int pieceW, int pieceH) {
    super(x, y, pieceW, pieceH);

    if (file != null && texture.getPieceMap().containsKey(file)) {
      this.pieceTexture = texture.getPieceMap().get(file);
      setWidth(pieceTexture.getWidth());
      setHeight(pieceTexture.getHeight());
      hasTexture = true;
    } else {
      hasTexture = false;
    }
  }

  public void draw(PGraphics graphics, float scale) {
    if (hasTexture) {
      graphics.image(pieceTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
    } else {
      //display missing texture texture
    }
  }
  
  public String getName(){
    return "Piece";
  }

  public File getFile() {
    return pieceTexture.getFile();
  }
}

//------------------Tile---------------------
class Tile extends Rectangle {
  private boolean hasTexture;
  private TileHandler tileTexture;
  //rotation
  //vetrtical flip
  //horazontal flip

  public Tile(File file, int x, int y) {
    super(x, y, 100, 100);

    if (file != null && texture.getTileMap().containsKey(file)) {
      this.tileTexture = texture.getTileMap().get(file);
      hasTexture = true;
    } else {
      hasTexture = false;
    }
  }

  public void draw(PGraphics graphics, float scale) {
    if (hasTexture) {
      graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
    } else {
      //display missing texture texture
    }
  }
  
  public String getName(){
    return "Tile";
  }

  public File getFile() {
    return tileTexture.getFile();
  }
}
