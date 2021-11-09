package fr.thesmyler.smylibgui;

import com.sun.istack.internal.NotNull;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.terramap.config.TerramapConfig;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * A mock screen to use in unit tests.
 */
public class TestingScreen extends WidgetContainer {

    private long screenTime = 0L;
    private final int frameTime;
    private float width, height;
    private float mouseX, mouseY;
    private final Queue<MouseEvent> mouseEvents = new PriorityQueue<>();
    private final Queue<MouseWheelEvent> mouseWheelEvents = new PriorityQueue<>();
    private final Queue<KeyboardEvent> keyboardEvents = new PriorityQueue<>();

    private final boolean[] mouseButtonsPressed = new boolean[SmyLibGui.getMouseButtonCount()];
    private final float[] lastClickX = new float[SmyLibGui.getMouseButtonCount()];
    private final float[] lastClickY = new float[SmyLibGui.getMouseButtonCount()];
    private final long[] lastClickTime = new long[SmyLibGui.getMouseButtonCount()];
    private int lastClickedButton = -1;

    /**
     * {@link TestingScreen} constructor.
     *
     * @param fps       the frame-rate to simulate
     * @param width     the initial width of the window
     * @param height    the initial height of the window
     */
    public TestingScreen(int fps, float width, float height) {
        super(Integer.MAX_VALUE);
        this.width = width;
        this.height = height;
        this.frameTime = 1000 / fps;
    }

    /**
     * Runs this screen for a single frame.
     *
     * @throws InterruptedException if sleeping for the correct timing is interrupted
     */
    public void doTick() throws InterruptedException {
        long stime = System.currentTimeMillis();
        this.processMouseEvents();
        this.processKeyboardEvents();
        this.onUpdate(this.mouseX, this.mouseY, this);
        // At this point we would draw the screen, but we aren't doing that here
        // this.draw(0, 0, this.mouseX, this.mouseY, true, true, null);
        long dt = System.currentTimeMillis() - stime;
        Thread.sleep(max(0, this.frameTime - dt));
        this.screenTime += System.currentTimeMillis() - stime;
    }

    public void runFor(long time) throws InterruptedException {
        long stime = this.screenTime;
        while (this.screenTime < stime + time) this.doTick();
    }

    public void moveMouse(float x, float y, long time) throws InterruptedException {
        long updates = time / this.frameTime;
        float dX = (x - this.mouseX) / updates;
        float dY = (y - this.mouseY) / updates;
        for (long i = 0; i < updates; i++) {
            this.mouseX += dX;
            this.mouseY += dY;
            if (this.lastClickedButton >= 0 && this.mouseButtonsPressed[this.lastClickedButton]) {
                this.mouseEvents.add(new MouseEvent(-1, false, this.screenTime));
            }
            this.doTick();
        }
        this.mouseX = x;
        this.mouseY = y;
        this.doTick();
    }

    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
        this.init();
    }

    public void click(int mouseButton) {
        this.mouseEvents.add(new MouseEvent(mouseButton, true, this.screenTime));
        this.mouseEvents.add(new MouseEvent(mouseButton, false, this.screenTime + this.frameTime - 1));
    }

    public void doubleClick(int mouseButton) {
        this.click(mouseButton);
        this.click(mouseButton);
    }

    public void pressMouseButton(int button) {
        this.mouseEvents.add(new MouseEvent(button, true, this.screenTime));
    }

    public void releaseMouseButton(int button) {
        this.mouseEvents.add(new MouseEvent(button, false, this.screenTime));
    }

    public void scrollMouse(int amount) {
        if (amount == 0) return;
        int inc = amount / abs(amount);
        for (int i = 0; i != amount; i += inc) {
            this.mouseWheelEvents.add(new MouseWheelEvent(inc, this.screenTime));
        }
    }

    public void pressKey(char key, int keyCode) {
        this.keyboardEvents.add(new KeyboardEvent(key, keyCode, this.screenTime));
    }

    private void processMouseEvents() {
        Iterator<MouseEvent> iterator = this.mouseEvents.iterator();
        while (iterator.hasNext()) {
            MouseEvent event = iterator.next();
            if (event.time > this.screenTime) break;
            this.processMouseEvent(event);
            iterator.remove();
        }
    }

    private void processMouseEvent(MouseEvent event) {
        long ctime = this.screenTime;
        int mouseButton = event.button;

        if(event.buttonState) {
            this.mouseButtonsPressed[mouseButton] = true;
            if(ctime - this.lastClickTime[mouseButton] <= TerramapConfig.CLIENT.doubleClickDelay && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
                this.onDoubleClick(this.mouseX, this.mouseY, mouseButton, null);
            } else {
                this.onClick(this.mouseX, this.mouseY, mouseButton, null);
            }
            this.lastClickedButton = mouseButton;
            this.lastClickTime[mouseButton] = ctime;
            this.lastClickX[mouseButton] = this.mouseX;
            this.lastClickY[mouseButton] = this.mouseY;
        } else if(mouseButton >= 0) {
            this.mouseButtonsPressed[mouseButton] = false;
            this.lastClickedButton = -1;
            this.onMouseReleased(this.mouseX, this.mouseY, mouseButton, null);
        } else if(this.lastClickedButton >= 0 && this.mouseButtonsPressed[this.lastClickedButton]) {
            float dX = this.mouseX - this.lastClickX[this.lastClickedButton];
            float dY = this.mouseY - this.lastClickY[this.lastClickedButton];
            long dt = ctime - this.lastClickTime[this.lastClickedButton];
            this.lastClickX[this.lastClickedButton] = this.mouseX;
            this.lastClickY[this.lastClickedButton] = this.mouseY;
            this.lastClickTime[this.lastClickedButton] = ctime;
            this.onMouseDragged(this.mouseX, this.mouseY, dX, dY, this.lastClickedButton, null, dt);
        }

        Iterator<MouseWheelEvent> iterator = this.mouseWheelEvents.iterator();
        while (iterator.hasNext()) {
            MouseWheelEvent wheelEvent = iterator.next();
            if (wheelEvent.time > this.screenTime) break;
            if(wheelEvent.scroll != 0) this.onMouseWheeled(this.mouseX, this.mouseY, wheelEvent.scroll, null);
            iterator.remove();
        }
    }

    private void processKeyboardEvents() {
        Iterator<KeyboardEvent> iterator = this.keyboardEvents.iterator();
        while (iterator.hasNext()) {
            KeyboardEvent event = iterator.next();
            if (event.time > this.screenTime) break;
            this.onKeyTyped(event.character, event.eventKey, null);
            iterator.remove();
        }
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    private final static class MouseEvent implements Comparable<MouseEvent> {
        final int button;
        final boolean buttonState;
        final long time;
        public MouseEvent(int button, boolean buttonState, long time) {
            this.button = button;
            this.buttonState = buttonState;
            this.time = time;
        }
        @Override
        public int compareTo(@NotNull MouseEvent other) {
            return Long.compare(this.time, other.time);
        }
    }

    private final static class MouseWheelEvent implements Comparable<MouseWheelEvent> {
        final int scroll;
        final long time;
        public MouseWheelEvent(int scroll, long time) {
            this.scroll = scroll;
            this.time = time;
        }
        @Override
        public int compareTo(@NotNull MouseWheelEvent other) {
            return Long.compare(this.time, other.time);
        }
    }

    private final static class KeyboardEvent implements Comparable<KeyboardEvent> {
        final char character;
        final int eventKey;
        final long time;
        public KeyboardEvent(char character, int eventKey, long time) {
            this.character = character;
            this.eventKey = eventKey;
            this.time = time;
        }
        @Override
        public int compareTo(@NotNull KeyboardEvent other) {
            return Long.compare(this.time, other.time);
        }
    }

}
