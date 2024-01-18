package net.smyler.smylib;

import net.smyler.smylib.game.*;
import org.junit.jupiter.api.BeforeEach;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * An abstract class that unit test classes can inherit from to be provided with a working SmyLibGui test environment.
 *
 * @author Smyler
 */
public abstract class SmyLibTest {

    private DummyGameClient game;

    @BeforeEach
    public void initSmyLibGui() {
        // We are doing it before each test, so we clear devices' states
        this.game = new DummyGameClient();
        SmyLib.initializeGameClient(this.game, getLogger("SmyLib unit test logger"));
        this.getMouse().setButtonCount(3);
        this.getMouse().setHasWheel(true);
    }

    protected DummyMouse getMouse() {
        return (DummyMouse) this.game.mouse();
    }

    protected DummyKeyboard getKeyboard() {
        return (DummyKeyboard) this.game.keyboard();
    }

}
