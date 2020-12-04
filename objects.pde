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

//------------------Image---------------------
class Image extends Rectangle {
  private boolean hasTexture;
  private ImageHandler imageTexture;
  //rotation
  //vetrtical flip
  //horazontal flip

  Image(File file, int x, int y, int imageW, int imageH) {
    super(x, y, imageW, imageH);

    if (file != null && texture.getImageMap().containsKey(file)) {
      this.imageTexture = texture.getImageMap().get(file);
      setWidth(imageTexture.getWidth());
      setHeight(imageTexture.getHeight());
      hasTexture = true;
    } else {
      hasTexture = false;
    }
  }

  public void draw(PGraphics graphics, float scale) {
    if (hasTexture) {
      graphics.image(imageTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
    } else {
      //display missing texture texture
    }
  }
  
  public String getName(){
    return "Image";
  }

  public File getFile() {
    return imageTexture.getFile();
  }
}

//------------------PageArea---------------------
class PageArea extends Rectangle {
  public PageArea(int x, int y, int areaW, int areaH){
    super(x, y, areaW, areaH);
  }
}
