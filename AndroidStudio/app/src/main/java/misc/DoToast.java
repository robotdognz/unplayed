package misc;

import android.app.Activity;
import android.widget.Toast;

import processing.core.PApplet;

public class DoToast {
	PApplet p;

	public DoToast(PApplet p) {
		this.p = p;
	}

	public void showToast(final String message) {
		Activity activity = p.getActivity();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity.getApplicationContext(), message, android.widget.Toast.LENGTH_SHORT).show();
			}
		});
	}
}
