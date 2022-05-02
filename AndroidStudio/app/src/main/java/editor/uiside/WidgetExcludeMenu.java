package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeMenu extends Widget {
    EditorSide toolbar;

    public WidgetExcludeMenu(PApplet p, Editor editor, Toolbar parent) {
        super(p, editor, parent);
        toolbar = (EditorSide) parent;
        icon = p.loadImage(folder + "ExcludeMenu.png");

        iconIsCurrentSubWidget = true;
        hasSActive = true;

        wd = widgetDirection.RIGHT;

        Widget w1 = new WidgetExcludePlayer(p, editor, parent);
        Widget w2 = new WidgetExcludeObstacles(p, editor, parent);
        Widget w3 = new WidgetExcludeTiles(p, editor, parent);
        Widget w4 = new WidgetExcludeImages(p, editor, parent);

        subWidgets.add(w1);
        subWidgets.add(w2);
        subWidgets.add(w3);
        subWidgets.add(w4);
    }

    @Override
    public void clicked() {
        if (!active) {
            active = true;
            editor.setRemovalMode();
        } else {
            super.clicked();
        }
    }

    @Override
    public void updateActive() {
        super.updateActive();

        // if a page is selected
        available = editor.selected != null && editor.selected instanceof Page;

        active = editor.isRemovalMode();

    }

}
