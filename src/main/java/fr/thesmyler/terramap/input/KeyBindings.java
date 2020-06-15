package fr.thesmyler.terramap.input;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.terramap.proxy.TerramapClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public abstract class KeyBindings {

	private static final String KEY_CATEGORY = "terramap.binding.category";
	
	public static final KeyBinding OPEN_MAP = new KeyBinding("terramap.binding.open_map", Keyboard.KEY_M, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding TOGGLE_DEBUG = new KeyBinding("terramap.binding.toggle_debug", Keyboard.KEY_P, KeyBindings.KEY_CATEGORY);
	
	public static void registerBindings() {
		ClientRegistry.registerKeyBinding(OPEN_MAP);
		ClientRegistry.registerKeyBinding(TOGGLE_DEBUG);
	}
	
	public static void checkBindings() {
		//TODO Only open the map on earth worlds
		if(OPEN_MAP.isPressed() && Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player.dimension == 0) Minecraft.getMinecraft().displayGuiScreen(TerramapClientProxy.getTiledMapGui());
	}

}
