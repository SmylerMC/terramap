package net.smyler.terramap.entity.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.smyler.smylib.Identifier;

public class ForgePlayerClientside extends PlayerServersideForge implements PlayerClientside {

    private final AbstractClientPlayer player;

    public ForgePlayerClientside(AbstractClientPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public Identifier skin() {
        return Identifier.parse(this.player.getLocationSkin().toString());
    }

}
