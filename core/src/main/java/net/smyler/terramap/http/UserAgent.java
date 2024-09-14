package net.smyler.terramap.http;

import net.smyler.smylib.SmyLib;
import net.smyler.smylib.game.GameClient;
import net.smyler.terramap.Terramap;

import static java.lang.System.getProperty;

public final class UserAgent {

    public static String buildUserAgent(GameClient client, Terramap terramap) {
        StringBuilder userAgent = new StringBuilder();
        terramap.version();
        SmyLib.getGameClient().modLoader();

        userAgent.append(modString(terramap)).append(" ");

        userAgent.append(loaderString(client)).append(" ");
        userAgent.append(mcString(client)).append(" ");

        return userAgent.toString();
    }

    private static String osString() {
        return getProperty("os.name", "unknown");
    }

    private static String javaString() {
        String version = getProperty("java.version");
        return "Java" + (version != null ? "/" + version : "");
    }

    private static String mcString(GameClient client) {
        return "Minecraft/" + client.gameVersion();
    }

    private static String loaderString(GameClient client) {
        return client.modLoader();
    }

    private static String modString(Terramap terramap) {
        return String.format("Terramap/%s (%s; %s; +https://github.com/SmylerMC/terramap)",
                terramap.version(),
                osString(),
                javaString()
        );
    }
}
