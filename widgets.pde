//------------------Pause---------------------
class Pause implements Widget{
  PVector position;
  float width, height;
  PVector bottomRight;
  
  public Pause(){
    position = new PVector(100,100);
    width = 60;
    height = 60;
    bottomRight = new PVector(position.x+width, position.y+height);
  }
  
  public void draw(){
    if(!gPaused){
      fill(100);
    }else{
      fill(150,0,0);
    }
    ellipseMode(CORNER);
    ellipse(position.x, position.y, width, height);
  }
  
  public void click(PVector lastTouch){
    if (lastTouch.x >= position.x && lastTouch.y >= position.y && lastTouch.x <= bottomRight.x && lastTouch.y <= bottomRight.y) {
      gPaused = !gPaused; //switch pause state
    }
  }
}
