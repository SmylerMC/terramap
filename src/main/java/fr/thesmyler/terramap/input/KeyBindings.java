package fr.thesmyler.terramap.input;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.gui.HudScreenHandler;
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
	public static final KeyBinding MAP_SHORTCUT = new KeyBinding("terramap.binding.shortcuts", Keyboard.KEY_LCONTROL, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding COPY_GEO_COORDS = new KeyBinding("terramap.binding.copy_geo", Keyboard.CHAR_NONE, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding COPY_MC_COORDS = new KeyBinding("terramap.binding.copy_mc", Keyboard.CHAR_NONE, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding ZOOM_IN = new KeyBinding("terramap.binding.zoom_in", Keyboard.KEY_B, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding ZOOM_OUT = new KeyBinding("terramap.binding.zoom_out", Keyboard.KEY_V, KeyBindings.KEY_CATEGORY);
	public static final KeyBinding TOGGLE_MINIMAP = new KeyBinding("terramap.binding.toggle_minimap", Keyboard.KEY_N, KeyBindings.KEY_CATEGORY);
	
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
		TOGGLE_DEBUG.setKeyConflictContext(TERRAMAP_SCREEN_CONTEXT);
		ClientRegistry.registerKeyBinding(TOGGLE_DEBUG);
		MAP_SHORTCUT.setKeyConflictContext(TERRAMAP_SCREEN_CONTEXT);
		ClientRegistry.registerKeyBinding(MAP_SHORTCUT);
		ClientRegistry.registerKeyBinding(COPY_GEO_COORDS);
		ClientRegistry.registerKeyBinding(COPY_MC_COORDS);
		ClientRegistry.registerKeyBinding(ZOOM_IN);
		ClientRegistry.registerKeyBinding(ZOOM_OUT);
		ClientRegistry.registerKeyBinding(TOGGLE_MINIMAP);
	}
	
	public static void checkBindings() {
		if(OPEN_MAP.isPressed() && TerramapRemote.getRemote().allowsMap(MapContext.FULLSCREEN)) {
			Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, TerramapRemote.getRemote().getMapStyles()));
		}
		if(COPY_GEO_COORDS.isPressed()) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			GeographicProjection projection = TerramapRemote.getRemote().getProjection();
			if(player == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.noplayer"));
			} else if(projection == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.noproj"));
			} else {
				double[] projectedCoords = projection.toGeo(player.posX, player.posZ);
				if(!Double.isFinite(projectedCoords[0]) || !Double.isFinite(projectedCoords[1])) {
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.outproj"));
				} else {
					GuiScreen.setClipboardString("" + projectedCoords[1] + " " + projectedCoords[0]);
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.geo"));
				}
			}
		}
		//There is already a vanilla feature for that, but let's have it a key away instead of two
		if(COPY_MC_COORDS.isPressed()) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player == null) {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.noplayer"));
			} else {
				GuiScreen.setClipboardString("" + player.posX + " " + player.posY + " " + player.posZ);
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("terramap.ingameactions.copy.mc"));
			}
		}
		
		if(ZOOM_IN.isPressed()) {
			HudScreenHandler.zoomInMinimap();
		}
		
		if(ZOOM_OUT.isPressed()) {
			HudScreenHandler.zoomOutMinimap();
		}
		
		if(TOGGLE_MINIMAP.isPressed()) {
			HudScreenHandler.toggleMinimap();
		}
	}

}
