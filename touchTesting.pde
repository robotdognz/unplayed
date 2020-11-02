class TouchTesting{
  TouchTesting(){
    
  }
  
  void draw(){
    //go through the list of touches and draw them
    for(int i = 0; i < touches.length; i++){
      ellipseMode(RADIUS); // Set ellipseMode to RADIUS fill(255); // Set fill to white ellipse(50, 50, 30, 30); // Draw white ellipse using RADIUS mode ellipseMode(CENTER); // Set ellipseMode to CENTER 
      fill(255); // Set fill to gray 
      ellipse(touches[i].x, touches[i].y, 70+2000*touches[i].area, 70+2000*touches[i].area); // Draw gray ellipse using CENTER
      fill(0);
      textSize(40);
      text(i , touches[i].x, touches[i].y-150);
    } 
  }
}