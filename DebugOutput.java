package editor;

import static processing.core.PConstants.CENTER;
import java.util.LinkedList;
import processing.core.PApplet;

public class DebugOutput {
	// debug queue
	static LinkedList<DebugMessage> messageQueue = new LinkedList<DebugMessage>();
	static int maxMessages = 10;

	public static void pushMessage(String message, float duration) {
		messageQueue.add(new DebugMessage(message, duration));

		if (messageQueue.size() > 10) {
			messageQueue.remove();
		}
	}
	
	// maybe a heading string and an open list of strings and values for the values
	// so stuff like "Velocity x: 0 y: 0" can be done

	public static void pushConstantFloat(String name, float value) {

	}

	public static void pushConstantInt(String name, int value) {

	}

	public static void pushConstantBoolean(String name, boolean value) {

	}

	protected static void step(float deltaTime) {
		if (messageQueue.size() == 0) {
			return;
		}

		boolean current = messageQueue.peek().step(deltaTime);
		if (current) {
			messageQueue.remove();
		}
	}

	public static void drawMessages(PApplet p, float y, int textSize) {
		// if there are debug messages, draw them
		if (messageQueue.size() > 0) {
			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);

			int line = 0;
			for (int i = messageQueue.size() - 1; i >= 0; i--) {
				line += messageQueue.get(i).drawMessage(p, p.width / 2, y + textSize * line);
			}
		}
	}

	public static void drawConstants(PApplet p, float y, int textSize) {

	}
}
