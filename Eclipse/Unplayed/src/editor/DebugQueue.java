package editor;

import static processing.core.PConstants.CENTER;

import java.util.LinkedList;
import java.util.Queue;

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
		// draw debug messages
		if (messageQueue.size() > 0) {

			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);

			int j = 0;
//			for (DebugMessage m : messageQueue) {
//
//				j += m.drawMessage(p, p.width / 2, y + textSize * j);
//			}

			for (int i = messageQueue.size() - 1; i >= 0; i++) {
				j += messageQueue.get(i).drawMessage(p, p.width / 2, y + textSize * j);
			}
		}
	}
}
