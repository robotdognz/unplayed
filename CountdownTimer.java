package misc;

public class CountdownTimer {
	private int intTimer;
	private int intLimit;

	private float deltaTimer;
	private float deltaLimit;

	private boolean running; // only true when the timer is running
	private boolean finished; // only true when the timer has naturally finished

	public CountdownTimer(int limit) {
		this.intTimer = 0;
		this.intLimit = limit;
		this.running = false;
		this.finished = false;
	}

	public CountdownTimer(float limit) {
		this.deltaTimer = 0;
		this.deltaLimit = limit;
		this.running = false;
		this.finished = false;
	}

	public void intStep() {
		if (running) {
			if (intTimer > intLimit) {
				intTimer++;
			} else {
				running = false;
				finished = true;
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
				finished = true;
				deltaTimer = 0;
			}
		}
	}

	public boolean isFinished() {
		return finished;
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

	// start with current time limit
	public void start() {
		intTimer = 0;
		deltaTimer = 0;
		running = true;
		finished = false;
	}
	
	// start with new time limit
	public void start(float limit) {
		deltaTimer = 0;
		deltaLimit = limit;
		running = true;
		finished = false;
	}

	public void stop() {
		running = false;
		finished = false;
	}
}
