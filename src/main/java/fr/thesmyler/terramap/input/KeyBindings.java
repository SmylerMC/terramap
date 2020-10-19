package fr.thesmyler.terramap.input;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.gui.TerramapScreen;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public abstract class KeyBindings {

	private static final String KEY_CATEGORY = "terramap.binding.category";
	
	public static final KeyBinding OPEN_MAP = new KeyBinding("terramap.binding.open_map", Keyboard.KEY_M, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding TOGGLE_DEBUG = new KeyBinding("terramap.binding.toggle_debug", Keyboard.KEY_P, KeyBindings.KEY_CATEGORY);
	
	//TODO Localize
	public static final KeyBinding MAP_SHORTCUT = new KeyBinding("Map shortcuts", Keyboard.KEY_LCONTROL, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding COPY_GEO_COORDS = new KeyBinding("Copy geographic coordinates", Keyboard.CHAR_NONE, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding COPY_MC_COORDS = new KeyBinding("Copy minecraft coordinates", Keyboard.CHAR_NONE, KeyBindings.KEY_CATEGORY);
	
	private static final IKeyConflictContext TERRAMAP_SCREEN_CONTEXT = new IKeyConflictContext() {
		@Override
		public boolean isActive() {
			return  Minecraft.getMinecraft().currentScreen instanceof TerramapScreen;
		}
		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return other.equals(this);
		}
	};
	
	public static void registerBindings() {
		ClientRegistry.registerKeyBinding(OPEN_MAP);
		ClientRegistry.registerKeyBinding(TOGGLE_DEBUG);
		MAP_SHORTCUT.setKeyConflictContext(TERRAMAP_SCREEN_CONTEXT);
		ClientRegistry.registerKeyBinding(MAP_SHORTCUT);
		ClientRegistry.registerKeyBinding(COPY_GEO_COORDS);
		ClientRegistry.registerKeyBinding(COPY_MC_COORDS);
	}
	
	public static void checkBindings() {
		if(OPEN_MAP.isPressed()
				  && Minecraft.getMinecraft().world != null
				  && Minecraft.getMinecraft().player.dimension == 0) { //TODO Follow Sledgehammer preferences
			Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, TerramapRemote.getRemote().getMapStyles()));
		}
		if(COPY_GEO_COORDS.isPressed()) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			GeographicProjection projection = TerramapRemote.getRemote().getProjection();
			if(player == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Could not copy coordinates: no player")); //TODO Localize
			} else if(projection == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Could not copy coordinates: no projection")); //TODO Localize
			} else {
				double[] projectedCoords = projection.toGeo(player.posX, player.posZ);
				if(!Double.isFinite(projectedCoords[0]) || !Double.isFinite(projectedCoords[1])) {
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Could not copy coordinates: you are outside the projection")); //TODO Localize
				} else {
					GuiScreen.setClipboardString("" + projectedCoords[1] + " " + projectedCoords[0]);
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Copied geographic coordinates to clipboard")); //TOOD Localize
				}
			}
		}
		//There is already a vanilla feature for that, but let's have it a key away instead of two
		if(COPY_MC_COORDS.isPressed()) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Could not copy coordinates: no player")); //TODO Localize
			} else {
				GuiScreen.setClipboardString("" + player.posX + " " + player.posY + " " + player.posZ);
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("Copied minecraft coordinates to clipboard")); //TODO Localize
			}
		}
	}

}
