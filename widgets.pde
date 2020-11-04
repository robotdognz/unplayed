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
  
  public void click(){
    gPaused = !gPaused; //switch pause state
    //println("click - " + millis());
  }
  
  public PVector getTopLeft(){
     return position; 
  }
  
  public PVector getBottomRight(){
    return bottomRight;
  }
  
}
