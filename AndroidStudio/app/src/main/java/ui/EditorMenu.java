package ui;

import camera.FreeCamera;
import game.Game;
import game.AppLogic;
import handlers.ButtonHandler;
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

        // resume
        ButtonHandler resumeHandler = AppLogic.texture.getButtonList().get(5);
        Button resumeB = new Button(resumeHandler, p.width * 0.5f, resume);
        objects.add(resumeB);

        if (!AppLogic.editorToggle) {
            // restart
            ButtonHandler restartHandler = AppLogic.texture.getButtonList().get(10);
            Button restartB = new Button(restartHandler, p.width * 0.5f, restart);
            objects.add(restartB);
            // edit level
            ButtonHandler editHandler = AppLogic.texture.getButtonList().get(11);
            Button editorB = new Button(editHandler, p.width * 0.5f, editor);
            objects.add(editorB);
        } else {
            // quit editor
            ButtonHandler quitHandler = AppLogic.texture.getButtonList().get(4);
            Button mainMenuB = new Button(quitHandler, p.width * 0.5f, mainMenu);
            objects.add(mainMenuB);
        }
        constructMenu();
    }

    @Override
    public void onBackPressed() {
        // resume if in editor menu
        AppLogic.removeMenu();
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
