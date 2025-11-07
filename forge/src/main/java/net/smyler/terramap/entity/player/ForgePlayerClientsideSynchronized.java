package net.smyler.terramap.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.text.Text;

import java.util.UUID;

import static net.smyler.smylib.Objects.requireNonNullElseGet;

public class ForgePlayerClientsideSynchronized extends PlayerClientsideSynchronized {

    private Identifier texture;
    private boolean textureRequested = false;

    public ForgePlayerClientsideSynchronized(UUID uuid, Text name) {
        super(uuid, name);
    }

    @Override
    public Identifier skin() {
        if(this.texture == null && !this.textureRequested) {
            GameProfile profile = new GameProfile(this.uuid(), null);
            new Thread(() -> {
                Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(profile, this::skinAvailable, false);
            }).start();
            this.textureRequested = true;
        }
        return requireNonNullElseGet(this.texture, this::defaultSkin);
    }

    private void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        if (type.equals(Type.SKIN)) {
            this.texture = new Identifier(location.getNamespace(), location.getPath());
        }
    }

    private Identifier defaultSkin() {
        ResourceLocation resource = DefaultPlayerSkin.getDefaultSkin(this.uuid());
        return new Identifier(resource.getNamespace(), resource.getPath());
    }

}
