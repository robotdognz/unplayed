package editor;

import java.util.PriorityQueue;

public class DebugMessage {
	static PriorityQueue<String> messageQueue = new PriorityQueue<String>();
	
	static float messageLength = 5f;
	static float currentMessage = 0;
	
	public static void pushMessage(String message) {
		messageQueue.add(message);
	}
	
	protected static void step(float deltaTime) {
		if(messageQueue.size() == 0) {
			return;
		}
		
		currentMessage += deltaTime;
		
		if(currentMessage >= messageLength) {
			currentMessage = 0;
			messageQueue.remove();
		}
	}
	
	protected static PriorityQueue<String> getMessages() {
		return messageQueue;
	}
}
