package net.smyler.terramap.entity.player;

public enum GameMode {
    SURVIVAL("survival"),
    CREATIVE("creative"),
    ADVENTURE("adventure"),
    SPECTATOR("spectator"),
    UNSUPPORTED("unsupported");

    public final String name;

    GameMode(String name) {
        this.name = name;
    }

    public static GameMode fromName(String name) {
        for (GameMode mode : GameMode.values()) {
            if (mode.name.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return UNSUPPORTED;
    }
}
