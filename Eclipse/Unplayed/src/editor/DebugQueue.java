package editor;

import static processing.core.PConstants.CENTER;
import java.util.LinkedList;
import processing.core.PApplet;

public class DebugQueue {
	static LinkedList<DebugMessage> messageQueue = new LinkedList<DebugMessage>();

	static float messageLength = 5f;
	static float currentMessage = 0;

	public static void pushMessage(String message, float duration) {
		messageQueue.add(new DebugMessage(message, duration));
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
}
