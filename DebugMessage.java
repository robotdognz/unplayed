package editor;

import processing.core.PApplet;

public class DebugMessage {
	private String message;
	private float time;
	private float duration;

	public DebugMessage(String message, float duration) {
		this.message = message;
		this.time = 0;
		this.duration = duration;
	}

	public boolean step(float deltaTime) {
		this.time += deltaTime;

		if (time >= duration) {
			return true; // is finished
		}

		return false; // is still going
	}

	public int drawMessage(PApplet p, float x, float y) {
		p.text(message, p.width / 2, y);
		return 1 + message.split(System.getProperty("line.separator")).length;
	}

	public float getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}

}
