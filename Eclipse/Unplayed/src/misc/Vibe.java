package misc;

import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;

public class Vibe {
	private Vibrator vibe;
	private boolean deprecated;

	public Vibe(Context c) {
		vibe = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE); //changed from getActivity() to context
		deprecated = android.os.Build.VERSION.SDK_INT < 26;// && vibe.hasVibrator();
		// this class needs to be updated to calculate fine grained vibration strength
		// using a combination of amount and level
	}

	@SuppressWarnings("deprecation")
	public void vibrate(long amount) {
		// amount = duration
		if (!deprecated) {
			vibe.vibrate(VibrationEffect.createOneShot(amount, 255));
		} else {
			// this is for older versions of android
			// need to make a second version of vibration tuned for older systems
			vibe.vibrate(amount);
		}
	}

	@SuppressWarnings("deprecation")
	public void vibrate(long amount, int level) {
		// amount = duration
		// level = intensity
		if (!deprecated) {
			vibe.vibrate(VibrationEffect.createOneShot(amount, level));
		} else {
			// this is for older versions of android
			// need to make a second version of vibration tuned for older systems
			vibe.vibrate(amount);
		}
	}
}
