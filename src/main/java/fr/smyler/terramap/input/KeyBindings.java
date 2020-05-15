package fr.smyler.terramap.input;

import org.lwjgl.input.Keyboard;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.gui.GuiTiledMap;
import fr.smyler.terramap.maps.TileFactory;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.WikimediaTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public abstract class KeyBindings {

	
	public static final KeyBinding OPEN_MAP = new KeyBinding("key.terramap.openmap", Keyboard.KEY_M, "key.terramap.category");
	
	public static void registerBindings() {
		ClientRegistry.registerKeyBinding(OPEN_MAP);
	}
	
	public static void checkBindings() {
		if(OPEN_MAP.isPressed()) {
			TerramapMod.logger.info("Openning map");
			Minecraft.getMinecraft().displayGuiScreen(new GuiTiledMap(new TiledMap<WikimediaTile>(TileFactory.WIKIMEDIA_TILE_FACTORY)));
		}
	}
	
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
    	System.out.println("triggered");
    	KeyBindings.checkBindings();
    }
}
