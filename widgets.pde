//------------------Pause---------------------
class Pause implements Widget{
  PVector position;
  float pWidth, pHeight;
  PVector bottomRight;
  boolean hover = false;
  
  public Pause(){
    position = new PVector(100,100);
    pWidth = 60;
    pHeight = 60;
    bottomRight = new PVector(position.x+pWidth, position.y+pHeight);
  }
  
  public void draw(){
    if(!gPaused){
      fill(0,150,0);
    }else{
      fill(150,0,0);
    }
    ellipseMode(CORNER);
    
    if(hover){
      ellipse(position.x-pWidth, position.y-pHeight, pWidth*3, pHeight*3);
    }else{
      ellipse(position.x, position.y, pWidth, pHeight);
    }
  }
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x-pWidth && 
    lastTouch.y >= position.y-pHeight && 
    lastTouch.x <= bottomRight.x+pWidth && 
    lastTouch.y <= bottomRight.y+pHeight) {
      hover = true;
    }else{
      hover = false;
    }
  }
  
  public void click(){
    if(hover){
      gPaused = true; //switch pause state
      menus.add(new PauseMenu());
    }
  }
}
