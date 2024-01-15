package fr.thesmyler.smylibgui.container;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.devices.Key;
import fr.thesmyler.smylibgui.devices.dummy.DummyMouse;
import fr.thesmyler.terramap.TerramapConfig;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import static fr.thesmyler.smylibgui.SmyLibGui.getMouse;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * A mock widget container to use in unit tests.
 * This class behaves like a normal container,
 * except that it can be passed user inputs programmatically,
 * and doesn't call its children draw methods.
 *
 * @author SmylerMC
 */
public class TestingWidgetContainer extends WidgetContainer {

    private long screenTime = 0L;
    private final int frameTime;
    private final Queue<MouseEvent> mouseEvents = new PriorityQueue<>();
    private final Queue<MouseWheelEvent> mouseWheelEvents = new PriorityQueue<>();
    private final Queue<KeyboardEvent> keyboardEvents = new PriorityQueue<>();

    private final float[] lastClickX = new float[getMouse().getButtonCount()];
    private final float[] lastClickY = new float[getMouse().getButtonCount()];
    private final long[] lastClickTime = new long[getMouse().getButtonCount()];
    private int lastClickedButton = -1;

    /**
     * {@link TestingWidgetContainer} constructor.
     *
     * @param fps       the frame-rate to simulate
     * @param width     the initial width of the window
     * @param height    the initial height of the window
     */
    public TestingWidgetContainer(int fps, float width, float height) {
        super(Integer.MAX_VALUE);
        SmyLibGui.getTestGameContext().setWindowWidth(width);
        SmyLibGui.getTestGameContext().setWindowHeight(height);
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
        float mouseX = SmyLibGui.getTestMouse().getX();
        float mouseY = SmyLibGui.getTestMouse().getY();
        this.onUpdate(mouseX, mouseY, this);
        // At this point we would draw the screen, but we aren't doing that here
        // this.draw(0, 0, this.mouseX, this.mouseY, true, true, null);
        long dt = System.currentTimeMillis() - stime;
        Thread.sleep(max(0, this.frameTime - dt));
        this.screenTime += System.currentTimeMillis() - stime;
    }

    public void runFor(long milliseconds) throws InterruptedException {
        long stime = this.screenTime;
        while (this.screenTime < stime + milliseconds) this.doTick();
    }

    public void moveMouse(float x, float y, long time) throws InterruptedException {
        long updates = time / this.frameTime;
        DummyMouse mouse = SmyLibGui.getTestMouse();
        float dX = (x - mouse.getX()) / updates;
        float dY = (y - mouse.getY()) / updates;
        for (long i = 0; i < updates; i++) {
            mouse.setX(mouse.getX() + dX);
            mouse.setY(mouse.getY() + dY);
            if (this.lastClickedButton >= 0 && SmyLibGui.getTestMouse().isButtonPressed(this.lastClickedButton)) {
                this.mouseEvents.add(new MouseEvent(-1, false, this.screenTime));
            }
            this.doTick();
        }
        mouse.setX(x);
        mouse.setY(y);
        this.doTick();
    }

    public void resize(float width, float height) {
        SmyLibGui.getTestGameContext().setWindowWidth(width);
        SmyLibGui.getTestGameContext().setWindowHeight(height);
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

    public void pressKey(char typedChar, Key key) {
        this.keyboardEvents.add(new KeyboardEvent(typedChar, key, this.screenTime));
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
        DummyMouse mouse = SmyLibGui.getTestMouse();
        if(event.buttonState) {
            SmyLibGui.getTestMouse().setButtonPressed(mouseButton, true);
            if(ctime - this.lastClickTime[mouseButton] <= TerramapConfig.CLIENT.doubleClickDelay && this.lastClickX[mouseButton] == mouse.getX() && this.lastClickY[mouseButton] == mouse.getY()) {
                this.onDoubleClick(mouse.getX(), mouse.getY(), mouseButton, null);
            } else {
                this.onClick(mouse.getX(), mouse.getY(), mouseButton, null);
            }
            this.lastClickedButton = mouseButton;
            this.lastClickTime[mouseButton] = ctime;
            this.lastClickX[mouseButton] = mouse.getX();
            this.lastClickY[mouseButton] = mouse.getY();
        } else if(mouseButton >= 0) {
            SmyLibGui.getTestMouse().setButtonPressed(mouseButton, false);
            this.lastClickedButton = -1;
            this.onMouseReleased(mouse.getX(), mouse.getY(), mouseButton, null);
        } else if(this.lastClickedButton >= 0 && SmyLibGui.getTestMouse().isButtonPressed(this.lastClickedButton)) {
            float dX = mouse.getX() - this.lastClickX[this.lastClickedButton];
            float dY = mouse.getY() - this.lastClickY[this.lastClickedButton];
            long dt = ctime - this.lastClickTime[this.lastClickedButton];
            this.lastClickX[this.lastClickedButton] = mouse.getX();
            this.lastClickY[this.lastClickedButton] = mouse.getY();
            this.lastClickTime[this.lastClickedButton] = ctime;
            this.onMouseDragged(mouse.getX(), mouse.getY(), dX, dY, this.lastClickedButton, null, dt);
        }

        Iterator<MouseWheelEvent> iterator = this.mouseWheelEvents.iterator();
        while (iterator.hasNext()) {
            MouseWheelEvent wheelEvent = iterator.next();
            if (wheelEvent.time > this.screenTime) break;
            if(wheelEvent.scroll != 0) this.onMouseWheeled(mouse.getX(), mouse.getY(), wheelEvent.scroll, null);
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
        return SmyLibGui.getTestGameContext().getWindowWidth();
    }

    @Override
    public float getHeight() {
        return SmyLibGui.getTestGameContext().getWindowHeight();
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
        public int compareTo(MouseEvent other) {
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
        public int compareTo(MouseWheelEvent other) {
            return Long.compare(this.time, other.time);
        }
    }

    private final static class KeyboardEvent implements Comparable<KeyboardEvent> {
        final char character;
        final Key eventKey;
        final long time;
        public KeyboardEvent(char character, Key eventKey, long time) {
            this.character = character;
            this.eventKey = eventKey;
            this.time = time;
        }
        @Override
        public int compareTo(KeyboardEvent other) {
            return Long.compare(this.time, other.time);
        }
    }

}
