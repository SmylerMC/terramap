package fr.thesmyler.smylibgui.util;

public class Animation {

    protected final long duration;
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
                    this.progress = Util.saturate(f);
                    if(this.progress == 1f) this.state = AnimationState.STOPPED;
                    break;
                case LEAVE:
                    float g = (float)age/(float)this.duration;
                    this.progress = 1 - Util.saturate(g);
                    if(this.progress == 0f) this.state = AnimationState.STOPPED;
                    break;
                case FLASH:
                    float k = 2 * Util.saturate(Math.abs(((float)(age % this.duration) - halfDuration)/halfDuration));
                    this.progress = (int)k;
                    break;
                case CONTINUOUS_ENTER:
                    float h = (float)(age % this.duration)/(float)this.duration;
                    this.progress = Util.saturate(h);
                    break;
                case CONTINUOUS_LEAVE:
                    float i = (float)(age % this.duration)/(float)this.duration;
                    this.progress = 1 - Util.saturate(i);
                    break;
                case BACK_AND_FORTH:
                    float j = ((float)(age % this.duration) - halfDuration)/halfDuration;
                    this.progress = Util.saturate(Math.abs(j));
                    break;
                case STOPPED:
                    break;
            }
        }
    }

    public long blend(long end, long start) {
        return Math.round((end - start) * (double)this.progress + start);
    }

    public int blend(int end, int start) {
        return (int) this.blend(end, (long)start);
    }

    public double blend(double end, double start) {
        return (end - start) * this.progress + start;
    }

    public float blend(float end, float start) {
        return (float) this.blend(end, (double)start);
    }

    /**
     * Linearly interpolates two colors using their RGBA channels.
     *
     * @param   end   end color
     * @param   start start color
     * @return  the blended color
     */
    public Color blend(Color end, Color start) {
        return new Color(
                this.blend(end.red(), start.red()),
                this.blend(end.green(), start.green()),
                this.blend(end.blue(), start.blue()),
                this.blend(end.alpha(), start.alpha())
        );
    }

    public Color fadeColor(Color color) {
        return color.withAlpha(Util.saturate(color.alphaf()*this.progress));
    }

    public Color rainbowColor() {
        return Color.fromHSL(this.getProgress(), 1f, 0.5f);
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
        ENTER, LEAVE, FLASH, CONTINUOUS_ENTER, CONTINUOUS_LEAVE, BACK_AND_FORTH, STOPPED
    }
}
