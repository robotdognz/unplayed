package game.player;

import processing.core.PApplet;

public class RotationLerp {
	private float oldAngle;
	private float newAngle;

	private boolean isFinished = false;
	private float lerpAmount;

	public RotationLerp(float oldAngle, float newAngle) {
		this.oldAngle = oldAngle;
		this.newAngle = newAngle;

		this.lerpAmount = 0.3f;
	}

	public float getAngle() {
		oldAngle = PApplet.lerp(oldAngle, newAngle, lerpAmount);
		if (Math.abs(oldAngle-newAngle) < 0.0001) {
			this.isFinished = true;
		}
		return oldAngle;
	}

	public boolean isFinished() {
		return isFinished;
	}
}
