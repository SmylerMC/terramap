package net.smyler.smylib;

import net.smyler.smylib.game.GameClient;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Preconditions.checkState;

public class SmyLib {

    private static GameClient gameClient;
    private static Logger logger;

    @NotNull
    public static GameClient getGameClient() {
        checkState(gameClient != null, "SmyLib has not been initialized");
        return gameClient;
    }

    @NotNull
    public static Logger getLogger() {
        checkState(logger != null, "SmyLib has not been initialized");
        return logger;
    }

    public static void initializeGameClient(GameClient game, Logger logger) {
        checkArgument(game != null, "Cannot initialize SmyLib with a null game");
        checkArgument(logger != null, "Cannot initialize SmyLib with a null logger");
        if (SmyLib.gameClient != null) {
            logger.warn("SmyLib is being initialized multiple times");
        }
        SmyLib.gameClient = game;
        SmyLib.logger = logger;
    }

}
