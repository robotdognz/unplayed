package misc;

public class CountdownTimer {
	private int intTimer;
	private int intLimit;

	private float deltaTimer;
	private float deltaLimit;

	private boolean running;

	public CountdownTimer(int limit) {
		this.intTimer = 0;
		this.intLimit = limit;
		this.running = false;
	}

	public CountdownTimer(float limit) {
		this.deltaTimer = 0;
		this.deltaLimit = limit;
		this.running = false;
	}

	public void intStep() {
		if (running) {
			if (intTimer > intLimit) {
				intTimer++;
			} else {
				running = false;
				intTimer = 0;
			}
		}
	}

	public void deltaStep(float delta) {
		if (running) {
			if (deltaTimer < deltaLimit) {
				deltaTimer += delta;
			} else {
				running = false;
				deltaTimer = 0;
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public int intRemaining() {
		return intLimit - intTimer;
	}

	public float deltaRemaining() {
		return deltaLimit - deltaTimer;
	}

	public float intRemainingRatio() {
		return (float) intTimer / (float) intLimit;
	}

	public float deltaRemainingRatio() {
		return deltaTimer / deltaLimit;
	}

	public void start() {
		intTimer = 0;
		deltaTimer = 0;
		running = true;
	}

	public void stop() {
		running = false;
	}
}
