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
