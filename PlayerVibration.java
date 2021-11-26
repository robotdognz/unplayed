package game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import editor.DebugOutput;
import misc.CountdownTimer;
import misc.Vibe;
import processing.core.PApplet;

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

//	// new system
//	HashMap<Float, Integer> stepImpacts;

	public PlayerVibration() {
		currentTime = System.nanoTime();
		impacts = new ArrayList<PhysicsImpact>();

		timeout = 0.25f; // how long the impacts are kept for in seconds
		timeoutLong = (long) (timeout * 1000000000); // translated to nanoseconds

		pauseVibration = new CountdownTimer(0.064f);
		previousImpulse = -1;
		currentBadImpulse = -1;

		maximum = 22;
		minimum = 1;

//		stepImpacts = new HashMap<Float, Integer>();
	}

	public void step(float deltaTime) {
		// old system
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

//		// new system
//		float biggestImpact = 0;
//
//		Iterator it = stepImpacts.entrySet().iterator();
//		while (it.hasNext()) {
//			Float key = (Float) ((Map.Entry) it.next()).getKey();
//
//			if (stepImpacts.get(key) > 1) { // bad impact from this frame
//				stepImpacts.put(key, 0);
//			} else if (stepImpacts.get(key) == 0) { // bad impact from last frame
//				it.remove();
//			} else { // good impact from this frame
//				if (key > biggestImpact) {
//					biggestImpact = key;
//				}
//				it.remove();
//			}
//		}
//
//		if (biggestImpact > 800) {
//			int strength = (int) Math.min(Math.max(Math.abs(biggestImpact / 1000), minimum), maximum); // 800
//			Vibe.vibrate(strength);
//			DebugOutput.pushMessage("Did vibe: " + strength + " - " + biggestImpact, 1);
//			stepImpacts.clear();
//		}

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

	// the current system in here works, but it could be improved:
	// there are patterns in the actual values (not converted to 'strength') that
	// could
	// be analyzed from some kind of list of stored recent impulses, perhaps it's
	// always looking for patterns in the data and only lets something through when
	// it
	// isn't part of a pattern

	// This needs to be changed to use a range of total instead of strength, if the
	// varying values in the impulse pattern fall on either side of the rounding of
	// 'strength' the vibration bug still happens
	public void physicsImpact(float[] impulses) {
		// find total impulse power
		float total = 0;
		for (float impulse : impulses) {
			total += impulse;
		}

//		// store rounded impact
//		if (total > 800) {
//			float rounded = Math.round(total / 20) * 20; // round to nearest 100
//
//			if (stepImpacts.containsKey(rounded)) {
//				stepImpacts.put(rounded, stepImpacts.get(rounded) + 1);
//			} else {
//				stepImpacts.put(rounded, 1);
//			}
//		}

		// store impact
		impacts.add(new PhysicsImpact(total, currentTime));

		if (total > 800 && !vibeFrame) { // 400

			// Math.abs returns positive no matter what goes in
			// Math.log returns the log of the number it is given
			int strength = Math.round(total / 200) * 200;
			strength = (int) Math.min(Math.max(Math.abs(strength / 1000), minimum), maximum); // 800

			if (!pauseVibration.isRunning()) {
				Vibe.vibrate(strength);
				DebugOutput.pushMessage("Did vibe: " + strength, 1);
				vibeFrame = true;

				pauseVibration.start();

				previousImpulse = strength;
				currentBadImpulse = -1;

			} else {
				if (strength == previousImpulse) {
					pauseVibration.start();
					currentBadImpulse = strength;
					DebugOutput.pushMessage("Skipped vibe: " + strength + " - " + total, 1);
				} else {
					if (strength != currentBadImpulse) {
						Vibe.vibrate(strength);
						DebugOutput.pushMessage("Did vibe: " + strength + " - " + total, 1);
						vibeFrame = true;
					}
				}

				previousImpulse = strength;

			}

			return;
		}
	}

}
