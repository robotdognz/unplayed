//------------------Pause---------------------
class Pause implements Widget{
  PVector position;
  float width, height;
  PVector bottomRight;
  boolean hover = false;
  
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
    if(hover){
      ellipse(position.x-width, position.y-height, width*2, height*2);
    }
  }
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x && lastTouch.y >= position.y && lastTouch.x <= bottomRight.x && lastTouch.y <= bottomRight.y) {
      hover = true;
    }else{
      hover = false;
    }
  }
  
  //public void click(PVector lastTouch){
  //  if (lastTouch.x >= position.x && lastTouch.y >= position.y && lastTouch.x <= bottomRight.x && lastTouch.y <= bottomRight.y) {
  //    gPaused = !gPaused; //switch pause state
  //  }
  //}
  
  public void click(){
    if(hover){
      gPaused = !gPaused; //switch pause state
    }
  }
}
