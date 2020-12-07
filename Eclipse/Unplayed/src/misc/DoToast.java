package misc;

import android.app.Activity;
import android.widget.Toast;

public class DoToast {
	Activity activity;

	public DoToast(Activity activity) {
		this.activity = activity;
	}

	public void showToast(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity.getApplicationContext(), message, android.widget.Toast.LENGTH_SHORT).show();
			}
		});
	}
}
