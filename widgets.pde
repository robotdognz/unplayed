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
      fill(0,150,0);
    }else{
      fill(150,0,0);
    }
    ellipseMode(CORNER);
    
    if(hover){
      ellipse(position.x-width, position.y-height, width*2, height*2);
    }else{
      ellipse(position.x, position.y, width, height);
    }
  }
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x-width && 
    lastTouch.y >= position.y-height && 
    lastTouch.x <= bottomRight.x+width && 
    lastTouch.y <= bottomRight.y+height) {
      hover = true;
    }else{
      hover = false;
    }
  }
  
  public void click(){
    if(hover){
      gPaused = !gPaused; //switch pause state
      hover = false;
    }
  }
}
