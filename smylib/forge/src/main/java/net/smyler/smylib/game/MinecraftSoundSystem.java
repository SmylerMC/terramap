package net.smyler.smylib.game;

import net.smyler.smylib.game.SoundSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class MinecraftSoundSystem implements SoundSystem {

    @Override
    public void playUiSound(String soundId) throws IllegalArgumentException {
        SoundEvent sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(soundId));
        if (sound == null) throw new IllegalArgumentException("Unknown sound: " + soundId);
        this.playSound(sound);
    }

    @Override
    public void playClickSound() {
        this.playSound(SoundEvents.UI_BUTTON_CLICK);
    }

    @Override
    public void playToastInSound() {
        this.playSound(SoundEvents.UI_TOAST_IN);
    }

    @Override
    public void playToastOutSound() {
        this.playSound(SoundEvents.UI_TOAST_OUT);
    }

    private void playSound(SoundEvent sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0f));
    }

}
