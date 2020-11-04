class Vibe{
  import android.os.Vibrator;
  import android.os.VibrationEffect;
  import android.content.Context;
  import android.app.Activity;
  Vibrator vibe;
  
  public Vibe(){
    vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); 
  }
  
  public void vibrate(long amount){
    vibe.vibrate(VibrationEffect.createOneShot(amount, 255));
  }
  
  public void vibrate(long amount, int level){
    vibe.vibrate(VibrationEffect.createOneShot(amount, level));
  }
  
}
