package net.smyler.smylib;

import net.smyler.smylib.game.*;
import net.smyler.smylib.gui.screen.Screen;
import org.junit.jupiter.api.BeforeEach;

import static net.smyler.smylib.gui.screen.BackgroundOption.DEFAULT;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * An abstract class that unit test classes can inherit from to be provided with a working SmyLibGui test environment.
 *
 * @author Smyler
 */
public abstract class SmyLibTest {

    private TestGameClient game;

    @BeforeEach
    public void initSmyLibGui() {
        // We are doing it before each test, so we clear devices' states
        this.game = new TestGameClient();
        SmyLib.initializeGameClient(this.game, getLogger("SmyLib unit test logger"));
        this.getMouse().setButtonCount(3);
        this.getMouse().setHasWheel(true);
        this.game.displayScreen(new Screen(DEFAULT));
    }

    protected TestGameClient getTestGameClient() {
        return this.game;
    }

    protected DummyMouse getMouse() {
        return (DummyMouse) this.game.mouse();
    }

    protected DummyKeyboard getKeyboard() {
        return (DummyKeyboard) this.game.keyboard();
    }

}
