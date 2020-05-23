package fr.smyler.terramap.input;

import org.lwjgl.input.Keyboard;

import fr.smyler.terramap.proxy.TerramapClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public abstract class KeyBindings {

	private static final String KEY_CATEGORY = "terramap.binding.category";
	
	public static final KeyBinding OPEN_MAP = new KeyBinding("terramap.binding.open_map", Keyboard.KEY_M, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding TOGGLE_DEBUG = new KeyBinding("terramap.binding.toggle_debug", Keyboard.KEY_P, KeyBindings.KEY_CATEGORY);
	
	public static void registerBindings() {
		ClientRegistry.registerKeyBinding(OPEN_MAP);
		ClientRegistry.registerKeyBinding(TOGGLE_DEBUG);
	}
	
	public static void checkBindings() {
		if(OPEN_MAP.isPressed() && Minecraft.getMinecraft().world != null) Minecraft.getMinecraft().displayGuiScreen(TerramapClientProxy.getTiledMap());
	}
	
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
    	KeyBindings.checkBindings();
    }
}
