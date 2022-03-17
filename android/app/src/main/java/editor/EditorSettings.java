package editor;

import android.content.SharedPreferences;
import game.AppLogic;

public class EditorSettings {
	private static SharedPreferences settings;
	private static SharedPreferences.Editor saveGame;

	private static boolean debugOutput;
	private static boolean quadTreeLogic;
	private static boolean playerLogic;
	private static boolean cameraLogic;

	public EditorSettings() {
		// get save file system
		settings = AppLogic.settings;
		saveGame = AppLogic.saveGame;

		// load settings from save file
		debugOutput = settings.getBoolean("debugOutput", false);
		quadTreeLogic = settings.getBoolean("quadTreeLogic", false);
		playerLogic = settings.getBoolean("playerLogic", false);
		cameraLogic = settings.getBoolean("cameraLogic", false);
	}

	public static boolean debugOutput() {
		return debugOutput;
	}

	public static void toggleDebugOutput() {
		// toggle the setting
		debugOutput = !debugOutput;
		// update save file
		saveGame.putBoolean("debugOutput", debugOutput);
		saveGame.apply();
	}

	public static boolean quadTreeLogic() {
		return quadTreeLogic;
	}

	public static void toggleQuadTreeLogic() {
		// toggle the setting
		quadTreeLogic = !quadTreeLogic;
		// update save file
		saveGame.putBoolean("quadTreeLogic", quadTreeLogic);
		saveGame.apply();
	}

	public static boolean playerLogic() {
		return playerLogic;
	}

	public static void togglePlayerLogic() {
		// toggle the setting
		playerLogic = !playerLogic;
		// update save file
		saveGame.putBoolean("playerLogic", playerLogic);
		saveGame.apply();
	}

	public static boolean cameraLogic() {
		return cameraLogic;
	}

	public static void toggleCameraLogic() {
		// toggle the setting
		cameraLogic = !cameraLogic;
		// update save file
		saveGame.putBoolean("cameraLogic", cameraLogic);
		saveGame.apply();
	}

}
