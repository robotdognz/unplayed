package editor;

import processing.core.PApplet;

public class DebugMessage {
	private String message;
	private float time;
	private float duration;
	private int count;

	private int opacity;

	public DebugMessage(String message, float duration) {
		this.message = message;
		this.time = 0;
		this.duration = duration;

		this.count = 0;

		this.opacity = 255;
	}

	public void append(String message) {
		this.time = 0;
		this.message += "\n" + message;
	}
	
	public void increaseCount() {
		count++;
		time = 0;
	}

	public boolean step(float deltaTime) {
		this.time += deltaTime;

		if (time >= duration) {
			return true; // is finished
		}

		return false; // is still going
	}

	public String getMessage() {
		return message;
	}

	public int drawMessage(PApplet p, float x, float y) {
		// calculate opacity from remaining time
		opacity = 255 - (int) (255 * (time / duration));
		// draw the message
		p.fill(80, opacity);
		String printMessage = message;
		if (count > 0) {
			printMessage += " [" + count + "]";
		}
		p.text(printMessage, p.width / 2, y);
		// return how many lines the message was
		return 1 + message.split(System.getProperty("line.separator")).length;
	}
}
