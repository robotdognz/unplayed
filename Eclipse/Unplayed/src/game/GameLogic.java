package game;

import android.app.Activity;
import android.widget.Toast;
import menus.Menu;

public class GameLogic {
	private Activity activity;
	public boolean gPaused;
	public Menu menu;
	public boolean editorToggle;

	public GameLogic(Activity activity) {
		this.activity = activity;
		gPaused = false;
		menu = null;
		editorToggle = true;
	}

	public void init() {

		// TODO implement this
	}

	public void showToast(final String message) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity.getApplicationContext(), message, android.widget.Toast.LENGTH_SHORT).show();
			}
		});
	}

}
