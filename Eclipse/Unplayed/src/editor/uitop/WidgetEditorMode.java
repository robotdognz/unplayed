package editor.uitop;

import camera.FreeCamera;
import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import editor.Editor.editorMode;
import processing.core.PApplet;
import processing.core.PImage;
import ui.Widget;

public class WidgetEditorMode extends Widget {

	protected PImage externalModeIcon;

	public WidgetEditorMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "PlaceBlock.png");
		externalModeIcon = p.loadImage(folder + "diamond.png");
		iconIsCurrentSubWidget = true;
		Widget w1 = new WidgetAdd(p, editor, parent);
		Widget w2 = new WidgetErase(p, editor, parent);
		Widget w3 = new WidgetSelect(p, editor, parent);
		subWidgets.add(w1);
		subWidgets.add(w2);
		subWidgets.add(w3);

		hasSActive = true;
	}

//	@Override
//	public void clicked() {
//		if (active == false) {
//			active = true;
//			editor.controller = new EditorControl(p, editor);
//			editor.camera = new FreeCamera();
//		} else {
//
//			if (editor.eMode == editorMode.EXTERNAL) {
//				subWidgets.get(2).clicked();
//				editor.editorSide.clearExternalModes();
//				return;
//			}
//
//			for (int i = 0; i < subWidgets.size(); i++) {
//				Widget w = subWidgets.get(i);
//
//				// found active widget
//				if (w.isActive()) {
//					if (i + 1 < subWidgets.size()) {
//						subWidgets.get(i + 1).clicked();
//						return;
//					} else {
//						subWidgets.get(0).clicked();
//						return;
//					}
//				}
//			}
//		}
//	}

	@Override
	public void updateActive() {
		if (editor.eMode == editorMode.EXTERNAL) {
			this.icon = externalModeIcon;
		} else if (subWidgets.size() > 0) {
			for (Widget w : subWidgets) {
				w.updateActive();
				if (iconIsCurrentSubWidget && w.isActive()) {
					this.icon = w.getIcon();
				}
			}
		}

		if (editor.controller instanceof EditorControl) {
			active = true;
		} else {
			active = false;
		}
	}
}
