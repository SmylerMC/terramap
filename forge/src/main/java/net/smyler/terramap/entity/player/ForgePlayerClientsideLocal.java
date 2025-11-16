package net.smyler.terramap.entity.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.smyler.smylib.Identifier;

public class ForgePlayerClientsideLocal extends PlayerLocalForge implements PlayerClientside {

    private final AbstractClientPlayer player;

    public ForgePlayerClientsideLocal(AbstractClientPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public Identifier skin() {
        return Identifier.parse(this.player.getLocationSkin().toString());
    }

}
