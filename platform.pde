class Platform{
  private int x, y, platformW, platformH;
  private PImage sprite;
  Platform(int x, int y, int platformW, int platformH){
    this.x = x;
    this.y = y;
    this.platformW = platformW;
    this.platformH = platformH;
    sprite = loadImage("test_sprite2.png");
    tint(140,188,150);
  }
  void draw(){
    color c = color(30);
    fill(c);
    noStroke();
    rect(x, y, 100, 100);

    for(int i = 0; i < platformH; i+=100){
      for(int j = 0; j < platformW; j+=100){
        image(sprite, x+j, y+i, 100, 100);
      }
    } 
    
  }
  PVector getTopLeft(){
    return new PVector(x,y);
  }
  PVector getBottomRight(){
    return new PVector(x+platformW,y+platformH);
  }
}