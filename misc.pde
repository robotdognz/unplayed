//------------------Vibe---------------------
class Vibe{
  Vibrator vibe;
  
  public Vibe(){
    vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); 
    //this class needs to be updated to calculate fine grained vibration strength using a combination of amount and level
  }
  
  public void vibrate(long amount){
    //amount = duration
    vibe.vibrate(VibrationEffect.createOneShot(amount, 255));
  }
  
  public void vibrate(long amount, int level){
    //amount = duration
    //level = intensity
    vibe.vibrate(VibrationEffect.createOneShot(amount, level));
  }
}

//------------------TouchTesting---------------------
class TouchTesting{
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
