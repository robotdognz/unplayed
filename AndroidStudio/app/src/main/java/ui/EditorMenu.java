package ui;

import camera.FreeCamera;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;

public class EditorMenu extends Menu {
    Game game;
    String resume = "Resume";
    String editor = "Edit";
    String restart = "Restart";
    String mainMenu = "Main Menu";

    public EditorMenu(PApplet p) {
        super(p);
        this.game = AppLogic.game;

        Button resumeB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, resume);
        objects.add(resumeB);
        if (!AppLogic.editorToggle) {
            Button restartB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, restart);
            objects.add(restartB);
            Button editorB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, editor);
            objects.add(editorB);
        } else {
            Button mainMenuB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, mainMenu);
            objects.add(mainMenuB);
        }
        constructMenu();
    }

    @Override
    public void click() {
        for (MenuObject object : objects) {
            if (!(object instanceof Button)) {
                continue;
            }
            Button b = (Button) object;

            if (b.click().equals(resume)) { // resume the game if resume button pressed
                AppLogic.removeMenu(); // remove pause menu
            } else if (b.click().equals(editor)) {
                AppLogic.editorToggle = !AppLogic.editorToggle;
                AppLogic.editor.resetUI();
                AppLogic.removeMenu(); // remove pause menu
                AppLogic.editor.camera = new FreeCamera(); // back to editor camera
            } else if (b.click().equals(mainMenu)) {
                AppLogic.init(); // rebuild the game
            } else if (b.click().equals(restart)){
                AppLogic.game.startGame();
                AppLogic.removeMenu(); // remove pause menu
            }
        }
    }
}
