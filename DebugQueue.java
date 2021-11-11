package editor;

import static processing.core.PConstants.CENTER;

import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;

public class DebugQueue {
	// static Queue<String> messageQueue = new LinkedList<String>();
	static Queue<DebugMessage> messageQueue = new LinkedList<DebugMessage>();

	static float messageLength = 5f;
	static float currentMessage = 0;

	// TODO: it would probably be good to make a class the the queue is of that
	// stores how long the message should stay for and holds the count for that
	// message, this would be really useful for messages that don't need to stay on
	// screen for different lengths of time

	public static void pushMessage(String message, float duration) {
//		messageQueue.add(message);

		messageQueue.add(new DebugMessage(message, duration));
	}

	protected static void step(float deltaTime) {
		if (messageQueue.size() == 0) {
			return;
		}

//		currentMessage += deltaTime;

		boolean current = messageQueue.peek().step(deltaTime);
		if (current) {
			messageQueue.remove();
		}

//		if (currentMessage >= messageLength) {
//			currentMessage = 0;
//			messageQueue.remove();
//		}
	}

//	protected static Queue<String> getMessages() {
//		return messageQueue;
//	}

	public static void drawMessages(PApplet p, float y, int textSize) {
		// draw debug messages
		if (messageQueue.size() > 0) {
//			float messageHeight = editorTop.getHeight() + textSize;
			
			
			
			p.textSize(textSize);
			p.textAlign(CENTER, CENTER);
			
			int i = 0;
			for (DebugMessage m : messageQueue) {
				
				i += m.drawMessage(p, p.width / 2, y + textSize * i);

//				p.text(m.getMessage(), p.width / 2, y + textSize * i);
//				i += 1 + m.getMessage().split(System.getProperty("line.separator")).length;
			}
		}
	}
}
