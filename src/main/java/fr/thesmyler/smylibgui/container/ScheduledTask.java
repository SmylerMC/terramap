package fr.thesmyler.smylibgui.container;

class ScheduledTask {

    private final long after;
    private final Runnable action;

    public ScheduledTask(long when, Runnable action) {
        this.after = when;
        this.action = action;
    }

    public long getWhen() {
        return this.after;
    }

    public void execute() {
        this.action.run();
    }

}
