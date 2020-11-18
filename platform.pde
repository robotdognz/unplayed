class Platform extends Rectangle{
  //private int x, y, platformW, platformH;
  //private PVector topLeft, bottomRight;
  private PImage sprite;
  //private Rectangle rectangle;
  
  Platform(int x, int y, int platformW, int platformH){
    super(x, y, platformW, platformH);
    //rectangle = new Rectangle(x, y, platformW, platformH);
    sprite = texture.defaultBlock;
    
    //this.x = x;
    //this.y = y;
    //this.platformW = platformW;
    //this.platformH = platformH;
    //this.topLeft = new PVector(x, y);
    //this.bottomRight = new PVector(x+platformW, y+platformH);
  }
  
  void draw(){

    for(int i = 0; i < getHeight(); i+=100){
      for(int j = 0; j < getWidth(); j+=100){
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
  //PVector getTopLeft(){
  //  return rectangle.getTopLeft();
  //}
  //PVector getBottomRight(){
  //  return rectangle.getBottomRight();
  //}
}
