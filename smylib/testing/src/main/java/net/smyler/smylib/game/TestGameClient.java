package net.smyler.smylib.game;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.*;
import net.smyler.smylib.gui.DummyUiDrawContext;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.popups.Popup;
import net.smyler.smylib.gui.screen.PopupScreen;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.sprites.SpriteLibrary;
import net.smyler.smylib.gui.screen.TestPopupScreen;
import net.smyler.smylib.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Math.*;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static net.smyler.smylib.gui.screen.TestScreenProxy.setScreenResolution;

public class TestGameClient implements GameClient {

    private float width = 720f;
    private float height = 480f;
    private int scale = 1;
    private boolean isMac = false;
    private final MinecraftServerInfo serverInfo = new MinecraftServerInfo("Dummy Server", "example.com", "Message of the day.", false);
    private final DummyMouse mouse = new DummyMouse();
    private final Keyboard keyboard = new DummyKeyboard();
    private final Clipboard clipboard = new DummyClipboard();
    private final SoundSystem soundSystem = new DummySoundSystem();
    private final Translator translator = new DummyTranslator();
    private final Font font = new DummyFont(1f, 1f);
    private final UiDrawContext uiDrawContext = new DummyUiDrawContext();
    private final SpriteLibrary spriteLibrary = new SpriteLibrary();

    private final Path gameDirectory;

    private int targetFps;
    private int frameTime;
    private long screenTime = 0L;
    private Screen currentScreen;
    private final Queue<MouseEvent> mouseEvents = new PriorityQueue<>();
    private final Queue<MouseWheelEvent> mouseWheelEvents = new PriorityQueue<>();
    private final Queue<KeyboardEvent> keyboardEvents = new PriorityQueue<>();
    private final float[] lastClickX = new float[3];
    private final float[] lastClickY = new float[3];
    private final long[] lastClickTime = new long[3];
    private int lastClickedButton = -1;

    public TestGameClient() {
        try {
            this.gameDirectory = Files.createTempDirectory("smylibgui");
            Files.createDirectories(this.gameDirectory);
            this.setTargetFps(60);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String gameVersion() {
        return "test";
    }

    @Override
    public String modLoader() {
        return "JUnit";
    }

    @Override
    public float windowWidth() {
        return this.width;
    }

    public void setWindowWidth(float width) {
        this.setWindowDimensions(width, this.height);
    }

    @Override
    public float windowHeight() {
        return this.height;
    }

    public void setWindowHeight(float height) {
        this.setWindowDimensions(this.width, height);
    }

    public void setWindowDimensions(float width, float height) {
        this.width = width;
        this.height = height;
        setScreenResolution(width, height, this.getCurrentScreen());
    }

    @Override
    public int nativeWindowWidth() {
        return round(this.windowWidth() * this.scaleFactor());
    }

    @Override
    public int nativeWindowHeight() {
        return round(this.windowHeight() * this.scaleFactor());
    }

    @Override
    public int scaleFactor() {
        return this.scale;
    }

    public void setScaleFactor(int scale) {
        this.scale = scale;
    }

    @Override
    public boolean isMac() {
        return this.isMac;
    }

    @Override
    public Path gameDirectory() {
        return this.gameDirectory;
    }

    @Override
    public MinecraftServerInfo currentServerInfo() {
        return this.serverInfo;
    }

    @Override
    public Mouse mouse() {
        return this.mouse;
    }

    @Override
    public Keyboard keyboard() {
        return this.keyboard;
    }

    @Override
    public Clipboard clipboard() {
        return this.clipboard;
    }

    @Override
    public Optional<Cursor> cursor() {
        return Optional.empty();
    }

    @Override
    public void setCursor(@Nullable Identifier identifier) {

    }

    @Override
    public Optional<Cursor> getCursor(Identifier identifier) {
        return Optional.empty();
    }

    @Override
    public SoundSystem soundSystem() {
        return this.soundSystem;
    }

    @Override
    public Translator translator() {
        return this.translator;
    }

    @Override
    public Font defaultFont() {
        return this.font;
    }

    @Override
    public UiDrawContext guiDrawContext() {
        return this.uiDrawContext;
    }

    @Override
    public SpriteLibrary sprites() {
        return this.spriteLibrary;
    }

    @Override
    public boolean isGlAvailabale() {
        return false;
    }

    @Override
    public void displayScreen(Screen screen) {
        if (this.currentScreen != null) {
            this.currentScreen.onClosed();
        }
        this.currentScreen = screen;
        setScreenResolution(this.windowWidth(), this.windowHeight(), this.currentScreen);
    }

    @Override
    public Screen getCurrentScreen() {
        return this.currentScreen;
    }

    @Override
    public void displayPopup(@NotNull Popup popup) {
        this.displayScreen(new TestPopupScreen(this.currentScreen, popup));
    }

    @Override
    public @Nullable Popup getTopPopup() {
        if (this.currentScreen instanceof PopupScreen) {
            return ((PopupScreen) this.currentScreen).getPopup();
        }
        return null;
    }

    @Override
    public @Nullable Popup closeTopPopup() {
        if (!(this.currentScreen instanceof PopupScreen)) {
            return null;
        }
        TestPopupScreen popupScreen = (TestPopupScreen) this.currentScreen;
        this.displayScreen(popupScreen.background);
        return popupScreen.getPopup();
    }

    @Override
    public int closeAllPopups() {
        int closed = 0;
        while (this.currentScreen instanceof PopupScreen) {
            TestPopupScreen popupScreen = (TestPopupScreen) this.currentScreen;
            this.displayScreen(popupScreen.background);
            closed++;
        }
        return closed;
    }

    @Override
    public int currentFPS() {
        return this.targetFps;
    }

    @Override
    public Optional<Resource> getResource(Identifier resource) {
        return Optional.empty();
    }

    public void setIsMac(boolean yesNo) {
        this.isMac = yesNo;
    }

    public MinecraftServerInfo setServerInfo(MinecraftServerInfo info) {
        return this.serverInfo;
    }

    public void setTargetFps(int fps) {
        this.targetFps = fps;
        this.frameTime = (int)(round(1 / (double)fps * 100));
    }

    public void doTick() throws InterruptedException {
        long stime = currentTimeMillis();
        this.processMouseEvents();
        this.processKeyboardEvents();
        this.currentScreen.onUpdate(this.mouse.x(), this.mouse.y(), null);
        // At this point we would draw the screen, but we aren't doing that here
        // this.currentScreen.draw(0, 0, this.mouseX, this.mouseY, true, true, null);
        long dt = currentTimeMillis() - stime;
        sleep(max(0, this.frameTime - dt));
        this.screenTime += currentTimeMillis() - stime;
    }

    public void runFor(long milliseconds) throws InterruptedException {
        long stime = this.screenTime;
        while (this.screenTime < stime + milliseconds) this.doTick();
    }

    public void moveMouse(float x, float y, long time) throws InterruptedException {
        long updates = time / this.frameTime;
        float dX = (x - this.mouse.x()) / updates;
        float dY = (y - this.mouse.y()) / updates;
        for (long i = 0; i < updates; i++) {
            this.mouse.setX(this.mouse.x() + dX);
            this.mouse.setY(this.mouse.y() + dY);
            if (this.lastClickedButton >= 0 && this.mouse.isButtonPressed(this.lastClickedButton)) {
                this.mouseEvents.add(new MouseEvent(-1, false, this.screenTime));
            }
            this.doTick();
        }
        this.mouse.setX(x);
        this.mouse.setY(y);
        this.doTick();
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

    private void processMouseEvent(MouseEvent event) {
        long ctime = this.screenTime;
        int mouseButton = event.button;
        if(event.buttonState) {
            this.mouse.setButtonPressed(mouseButton, true);
            boolean mouseDidNotMove = this.lastClickX[mouseButton] == this.mouse.x() && this.lastClickY[mouseButton] == this.mouse.y();
            if(ctime - this.lastClickTime[mouseButton] <= 500 && mouseDidNotMove) {  //TODO de-hardcode double click delay in tests
                this.currentScreen.onDoubleClick(this.mouse.x(), this.mouse.y(), mouseButton, null);
            } else {
                this.currentScreen.onClick(this.mouse.x(), this.mouse.y(), mouseButton, null);
            }
            this.lastClickedButton = mouseButton;
            this.lastClickTime[mouseButton] = ctime;
            this.lastClickX[mouseButton] = this.mouse.x();
            this.lastClickY[mouseButton] = this.mouse.y();
        } else if(mouseButton >= 0) {
            this.mouse.setButtonPressed(mouseButton, false);
            this.lastClickedButton = -1;
            this.currentScreen.onMouseReleased(this.mouse.x(), this.mouse.y(), mouseButton, null);
        } else if(this.lastClickedButton >= 0 && this.mouse.isButtonPressed(this.lastClickedButton)) {
            float dX = this.mouse.x() - this.lastClickX[this.lastClickedButton];
            float dY = this.mouse.y() - this.lastClickY[this.lastClickedButton];
            long dt = ctime - this.lastClickTime[this.lastClickedButton];
            this.lastClickX[this.lastClickedButton] = this.mouse.x();
            this.lastClickY[this.lastClickedButton] = this.mouse.y();
            this.lastClickTime[this.lastClickedButton] = ctime;
            this.currentScreen.onMouseDragged(this.mouse.x(), this.mouse.y(), dX, dY, this.lastClickedButton, null, dt);
        }

        Iterator<MouseWheelEvent> iterator = this.mouseWheelEvents.iterator();
        while (iterator.hasNext()) {
            MouseWheelEvent wheelEvent = iterator.next();
            if (wheelEvent.time > this.screenTime) break;
            if(wheelEvent.scroll != 0) this.currentScreen.onMouseWheeled(this.mouse.x(), this.mouse.y(), wheelEvent.scroll, null);
            iterator.remove();
        }
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
    private void processKeyboardEvents() {
        Iterator<KeyboardEvent> iterator = this.keyboardEvents.iterator();
        while (iterator.hasNext()) {
            KeyboardEvent event = iterator.next();
            if (event.time > this.screenTime) break;
            this.currentScreen.onKeyTyped(event.character, event.eventKey, null);
            iterator.remove();
        }
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
