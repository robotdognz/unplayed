package ui;

import android.net.Uri;

import java.util.ArrayList;

import game.AppLogic;
import game.Game;
import handlers.ButtonHandler;
import processing.core.PApplet;

public class DeveloperMenu extends Menu {
    private final Game game;
    private final String editor = "Start Editor";
    private final String folder = "Play Level(s)";
    private final String back = "Back";

    boolean loadingFile = false; // true if this menu should start loading a campaign from a file

    public DeveloperMenu(PApplet p) {
        super(p);
        this.game = AppLogic.game;

        // start editor
        ButtonHandler startEditorHandler = AppLogic.texture.getButtonList().get(7);
        Button editorB = new Button(startEditorHandler, p.width * 0.5f, editor);
        objects.add(editorB);

        // load level(s)
        ButtonHandler loadLevelsHandler = AppLogic.texture.getButtonList().get(8);
        Button folderB = new Button(loadLevelsHandler, p.width * 0.5f, folder);
        objects.add(folderB);

        // back
        ButtonHandler backHandler = AppLogic.texture.getButtonList().get(9);
        Button backB = new Button(backHandler, p.width * 0.5f, back);
        objects.add(backB);

        constructMenu();
    }

    @Override
    public void onBackPressed() {
        // go back to previous menu
        AppLogic.previousMenu();
    }

    @Override
    public void click() {
        for (MenuObject object : objects) {
            if (!(object instanceof Button)) {
                continue;
            }
            Button b = (Button) object;

            switch (b.click()) {
                case editor:
                    game.emptyGame();
                    AppLogic.toggleEditor();

                    break;
                case folder:
                    AppLogic.files.createLoadFiles();
                    loadingFile = true;

                    break;
                case back:
                    AppLogic.previousMenu();
                    break;
            }
        }
    }

    @Override
    public void activate() {
        // I'm using this against it's original purpose. There is no step method, so I'm
        // going to use this instead

        if (loadingFile) {
            if (AppLogic.files.hasUris()) {
                ArrayList<Uri> uris = AppLogic.files.getUris();
                PApplet.print("Found Uri's: " + uris.size());

                AppLogic.setExternalLevels(uris); // pass uri's to the game to be played through
                AppLogic.files.removeUri(); // remove uri's

                child = null; // clear any existing menus
                AppLogic.newGame(); // start game

                loadingFile = false; // end loading
            }
        }
    }
}
