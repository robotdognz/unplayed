package ui;

import android.net.Uri;

import java.util.ArrayList;

import game.AppLogic;
import game.Game;
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

        Button editorB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, editor);
        Button folderB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, folder);
        Button backB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, back);
        objects.add(editorB);
        objects.add(folderB);
        objects.add(backB);
        constructMenu();
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