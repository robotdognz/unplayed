package game.player;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import misc.CountdownTimer;
import misc.Vibe;
import processing.core.PApplet;

public class PlayerVibration {

//	private boolean vibeFrame; // has a vibration happened yet this frame

	public List<PhysicsImpact> impacts;

	private float timeout;
	private long timeoutLong;

	private long currentTime;

	private CountdownTimer vibrationMonitor;
	private CountdownTimer vibrationCooldown;
	private int previousImpulse;
	private int currentBadImpulse;

	private static int maximum;
	private static int minimum;

	// new filtering system
	static private float currentImpulse;
	static private ArrayList<Float> usedImpulses = new ArrayList<>();
	static private float previousImpulse2;
	static private int vibeOutput;

//	// new system
//	HashMap<Float, Integer> stepImpacts;

	public PlayerVibration() {
		currentTime = System.nanoTime();
		impacts = new ArrayList<PhysicsImpact>();

		timeout = 0.25f; // how long the impacts are kept for in seconds
		timeoutLong = (long) (timeout * 1000000000); // translated to nanoseconds

		vibrationMonitor = new CountdownTimer(0.064f);
		vibrationCooldown = new CountdownTimer(0.020f);
		previousImpulse = -1;
		currentBadImpulse = -1;

		maximum = 22;
		minimum = 1;

//		stepImpacts = new HashMap<Float, Integer>();
	}

	public void step(float deltaTime) {
		// old system
//		vibeFrame = false; // clear vibeFrame
		vibrationMonitor.deltaStep(deltaTime);
		vibrationCooldown.deltaStep(deltaTime);

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

//		if (!vibrationCooldown.isRunning()) {
			if (vibeOutput > 0) {
//				vibrationCooldown.start();
				Vibe.vibrate(vibeOutput);
			}
//		}

//		if (vibeOutput > 0) {
//			Vibe.vibrate(vibeOutput);
			vibeOutput = 0;
//		}

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

	public static void EndStep() {
		// get impulse and clear field
		float currentStep = currentImpulse;

		if (currentStep > 1 && currentImpulse != previousImpulse2) {

			// print impulses
			String usedOutput = "";
			for (float used : usedImpulses) {
				usedOutput += used + ", ";
			}
			PApplet.print(usedOutput);
			PApplet.print("----------End------------ " + currentStep);

			// store vibration so it can be used in Step()
			//int tempVibe = (int) Math.round(currentStep);
			int tempVibe = (int) Math.round(currentStep); //Math.round(Math.log(currentStep)*2) * 2

			tempVibe = (int) Math.round(currentStep * 0.4f); //Math.min(currentStep, maximum);

			if (tempVibe > vibeOutput) {
				vibeOutput = tempVibe;
			}
		}

		// reset
		previousImpulse2 = currentImpulse;
		currentImpulse = 0;
		usedImpulses.clear();

	}

	// the current system in here works, but it could be improved:
	// there are patterns in the actual values (not converted to 'strength') that
	// could be analyzed from some kind of list of stored recent impulses, perhaps it's
	// always looking for patterns in the data and only lets something through when
	// it isn't part of a pattern

	// This needs to be changed to use a range of total instead of strength, if the
	// varying values in the impulse pattern fall on either side of the rounding of
	// 'strength' the vibration bug still happens
	public void physicsImpact(float[] normalImpulses, float[] tangentImpulses) {
		// find total impulse power
		float total = 0;
		for (float impulse : normalImpulses) {
			total += impulse;
		}



		// new insane impulse filtering algorithm
		float magicNumber = 4; // 6
		float impulse1 = Math.round(normalImpulses[0] * Game.DeltaStep());
		float impulse2 = Math.round(normalImpulses[1] * Game.DeltaStep());
		float newImpulse;
		if (impulse1 < magicNumber || impulse2 < magicNumber){
			// one equals 0 (functionally 0, less than magic number)
			// use the biggest number
			newImpulse = Math.max(impulse1, impulse2);
		} else {
			// neither equal 0 (functionally 0, less than magic number)
//			float diff = Math.abs(impulse1 - impulse2);
//			if (diff > magicNumber) {
//				// there is a significant difference, subtract the smallest from the biggest
//				newImpulse = Math.max(impulse1, impulse2) - Math.min(impulse1, impulse2);
//			} else {
				// no significant difference, use the max value
				newImpulse = Math.max(impulse1, impulse2);
//			}
		}

		if (newImpulse > 1) {

			// figure out if 'newImpulse' has been used
			boolean used = false;
			for (float usedImpulse : usedImpulses){
				used = Math.abs(newImpulse - usedImpulse) < magicNumber;
				if (used) {
					break;
				}
			}

				// 'newImpulse' hasn't been used
				if (currentImpulse > newImpulse) {
					if (!used) {
					// subtract it from the current step's impulse
//					currentImpulse -= newImpulse;
					}
				} else {
//					if (!used) {
					// replace the current step's impulse with this impulse
					currentImpulse = newImpulse;
//					}
				}

			// update used impulses list
			if (!usedImpulses.contains(newImpulse)) {
				usedImpulses.add(newImpulse);
			}
		}

		// update used impulses list
		if (impulse1 != 0 && !usedImpulses.contains(impulse1)) {
			usedImpulses.add(impulse1);
		}
		if (impulse2 != 0 && !usedImpulses.contains(impulse2)) {
			usedImpulses.add(impulse2);
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

		if (total > 800) { // && !vibeFrame) { // 400

			// print impulses
			String output = "";
			for(float impulse : normalImpulses){
				output += " " + (impulse * Game.DeltaStep());
			}
			PApplet.print(output);
			PApplet.print("New: " + newImpulse);
			PApplet.print("Stored: " + currentImpulse);
//			output = "";
//			for(float impulse : tangentImpulses){
//				output += " " + (impulse * Game.DeltaStep());
//			}
//			PApplet.print(output);

			// Math.abs returns positive no matter what goes in
			// Math.log returns the log of the number it is given
			int strength = Math.round(total / 200) * 200;
			strength = (int) Math.min(Math.max(Math.abs(strength / 1000), minimum), maximum); // 800

			if (!vibrationMonitor.isRunning()) {
				if (!vibrationCooldown.isRunning()) {
//					Vibe.vibrate(strength);
					vibrationCooldown.start();
//					vibeFrame = true;
				}
//				DebugOutput.pushMessage("Did vibe: " + strength + " - " + total, 1);
				

				vibrationMonitor.start();

				previousImpulse = strength;
				currentBadImpulse = -1;

			} else {
				if (strength == previousImpulse) {
					vibrationMonitor.start();
					currentBadImpulse = strength;
//					DebugOutput.pushMessage("Skipped vibe: " + strength + " - " + total, 1);
				} else {
					if (strength != currentBadImpulse) {
						if (!vibrationCooldown.isRunning()) {
//							Vibe.vibrate(strength);
							vibrationCooldown.start();
//							vibeFrame = true;
						}
//						DebugOutput.pushMessage("Did vibe: " + strength + " - " + total, 1);
					}
				}

				previousImpulse = strength;

			}

			return;
		}
	}

}
