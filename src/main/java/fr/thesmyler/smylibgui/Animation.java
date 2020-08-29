package fr.thesmyler.smylibgui;

public class Animation {

	protected long duration;
	protected AnimationState state = AnimationState.STOPPED;
	protected long started = Long.MIN_VALUE;
	protected long updated = Long.MIN_VALUE;
	private float progress = 0;
	
	public Animation(long duration) {
		this.duration = duration;
	}
	
	public AnimationState getState() {
		return this.state;
	}
	
	public float getProgress() {
		return this.progress;
	}
	
	public void update() {
		this.updated = System.currentTimeMillis();
		long age = this.getAge();
		long halfDuration = this.duration/2;
		if(this.duration == 0) {
			this.progress = 1f;
		} else {
			switch(this.state) {
			case ENTER:
				float f = (float)age/(float)this.duration;
				this.progress = Utils.saturate(f);
				if(this.progress == 1f) this.state = AnimationState.STOPPED;
				break;
			case LEAVE:
				float g = (float)age/(float)this.duration;
				this.progress = 1 - Utils.saturate(g);
				if(this.progress == 0f) this.state = AnimationState.STOPPED;
				break;
			case FLASH:
				float k = 2 * Utils.saturate(Math.abs(((float)(age % this.duration) - halfDuration)/(float)halfDuration));
				this.progress = (int)k;
				break;
			case CONTINUOUS_ENTER:
				float h = (float)(age % this.duration)/(float)this.duration;
				this.progress = Utils.saturate(h);
				break;
			case CONTINUOUS_LEAVE:
				float i = (float)(age % this.duration)/(float)this.duration;
				this.progress = 1 - Utils.saturate(i);
				break;
			case BACK_AND_FORTH:
				float j = ((float)(age % this.duration) - halfDuration)/(float)halfDuration;
				this.progress = Utils.saturate(Math.abs(j));
				break;
			case STOPPED:
				break;
			default:
				break;
			}
		}
	}
	
	public int between(int x1, int x2) {
		return Math.round((x1 - x2) * this.progress + x2);
	}
	
	public int fadeColor(int color) {
		return Utils.adaptAlpha(color, this.getProgress());
	}
	
	public int rainbowColor() {
		return Utils.hslToRgb(this.getProgress(), 1f, 0.5f);
	}
	
	public void stop() {
		this.start(AnimationState.STOPPED);
	}
	
	public void start(AnimationState state) {
		this.started = System.currentTimeMillis();
		this.state = state;
		this.update();
	}
	
	public long getAge() {
		return this.updated - this.started;
	}
	
	public enum AnimationState {
		ENTER, LEAVE, FLASH, CONTINUOUS_ENTER, CONTINUOUS_LEAVE, BACK_AND_FORTH, STOPPED;
	}
}
