class Pause implements Widget{
  PVector position;
  float size;
  
  public Pause(){
    position = new PVector(100,100);
    size = 20;
  }
  
  public void draw(){
    fill(20,20,20);
    ellipse(position.x,position.y,size,size);
  }
  
  public void click(){
    gPaused = !gPaused; //switch pause state
  }
}
