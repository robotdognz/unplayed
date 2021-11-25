package game.player;

import java.util.ArrayList;
import java.util.List;

import editor.DebugOutput;
import misc.CountdownTimer;
import misc.Vibe;

public class PlayerVibration {

	private boolean vibeFrame; // has a vibration happened yet this frame

	public List<PhysicsImpact> impacts;

	private float timeout;
	private long timeoutLong;

	private long currentTime;

	private CountdownTimer pauseVibration;
	private int previousImpulse;
	private int currentBadImpulse;

	private int maximum;
	private int minimum;

	public PlayerVibration() {
		currentTime = System.nanoTime();
		impacts = new ArrayList<PhysicsImpact>();

		timeout = 0.25f; // how long the impacts are kept for in seconds
		timeoutLong = (long) (timeout * 1000000000); // translated to nanoseconds

		pauseVibration = new CountdownTimer(0.064f);
		previousImpulse = -1;
		currentBadImpulse = -1;

		maximum = 25;
		minimum = 1;
	}

	public void step(float deltaTime) {
		vibeFrame = false; // clear vibeFrame
		pauseVibration.deltaStep(deltaTime);

		currentTime = System.nanoTime();

		// remove old impacts
		for (int i = 0; i < impacts.size(); i++) {
			PhysicsImpact impact = impacts.get(i);
			if (impact.time + timeoutLong < currentTime) {
				impacts.remove(impact);
			} else {
				break;
			}
		}
	}

	public float getImpactHistory() {
		float total = 0;
		float max = 0;
		for (PhysicsImpact impact : impacts) {
			total += impact.impact;
			float current = Math.abs(impact.impact);
			if (current > max) {
				max = current;
			}
		}
//		return max;
		return total;
	}

	public void physicsImpact(float[] impulses) {
		// find total impulse power
		float total = 0;
		for (float impulse : impulses) {
			total += impulse;
		}

		impacts.add(new PhysicsImpact(total, currentTime));

		if (total > 800 && !vibeFrame) { // 400

			// Math.abs returns positive no matter what goes in
			// Math.log returns the log of the number it is given
			int strength = (int) Math.min(Math.max(Math.abs(total / 1000), minimum), maximum); // 800

//			if (currentBadImpulse != -1) { // there is a current bad impulse
//
//			} else {
//
//			}

//			if (!pauseVibration.isRunning() && strength != previousImpulse) {
//				Vibe.vibrate(strength);
//				vibeFrame = true;
//
//				// store this information
//				pauseVibration.start();
//				previousImpulse = strength;
//
//				DebugOutput.pushMessage("Did vibe: " + strength, 1);
//			} else if (strength == previousImpulse) {
//				pauseVibration.start();
//
//				DebugOutput.pushMessage("Skipped vibe: " + strength, 1);
//			}

			if (!pauseVibration.isRunning()) {
				Vibe.vibrate(strength);
				DebugOutput.pushMessage("Did vibe: " + strength, 1);
				vibeFrame = true;

				if (strength == previousImpulse) {
					pauseVibration.start();
				}
				
				previousImpulse = strength;

			} else {
				if (strength == previousImpulse) {
					pauseVibration.start();
					DebugOutput.pushMessage("Skipped vibe: " + strength, 1);
				} else {
					Vibe.vibrate(strength);
					DebugOutput.pushMessage("Did vibe: " + strength, 1);
					vibeFrame = true;
				}
				
				previousImpulse = strength;

			}

			return;
		}
	}

}
