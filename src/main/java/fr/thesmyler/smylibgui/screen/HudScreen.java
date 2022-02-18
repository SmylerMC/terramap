package fr.thesmyler.smylibgui.screen;

import java.lang.reflect.Field;
import java.util.List;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import fr.thesmyler.smylibgui.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import static fr.thesmyler.smylibgui.SmyLibGui.getGameContext;
import static fr.thesmyler.smylibgui.SmyLibGui.getMouse;

public final class HudScreen {

    private static float renderWidth = 0;
    private static float renderHeight = 0;
    private static GuiScreen lastTickScreen = null;

    private static final HudScreenContainer CONTAINER = new HudScreenContainer(0);
    private static final InputProcessor PROCESSOR = new InputProcessor(CONTAINER);

    private static final Field NEW_CHAT_DRAW_CHAT_LINES_FIELD = ObfuscationReflectionHelper.findField(GuiNewChat.class, "field_146253_i");
    private static final Field GUI_INGAME_UPDATE_COUNTER_FIELD = ObfuscationReflectionHelper.findField(GuiIngame.class, "field_73837_f");

    private HudScreen() {}

    @SubscribeEvent
    public static void onRenderHUD(RenderGameOverlayEvent.Pre e) {
        if(!e.getType().equals(ElementType.HOTBAR)) return;
        boolean chatOpen = Minecraft.getMinecraft().currentScreen instanceof GuiChat;
        float width = getGameContext().getWindowWidth();
        float height = getGameContext().getWindowHeight();
        if(width != renderWidth || height != renderHeight) {
            renderWidth = width;
            renderHeight = height;
            init();
        }
        float mouseX = getMouse().getX();
        float mouseY = getMouse().getY();
        CONTAINER.onUpdate(mouseX, mouseY, null);
        Color color = Color.currentGL();
        CONTAINER.draw(0, 0, mouseX, mouseY, chatOpen && !isOverChat(mouseX, mouseY), false, null);
        GlStateManager.enableAlpha();
        color.applyGL(); // Reset color to what it was
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if(event.phase.equals(Phase.START)) {
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if(Minecraft.getMinecraft().world != null && (lastTickScreen != null || currentScreen != null) && (lastTickScreen == null || !lastTickScreen.equals(currentScreen))) {
                init();
            }
            lastTickScreen = currentScreen;
        }
    }

    @SubscribeEvent
    public static void onMouseInput(MouseInputEvent.Pre event) {
        if(!(event.getGui() instanceof GuiChat)) return;
        float mouseX = getMouse().getX();
        float mouseY = getMouse().getY();
        if(isOverChat(mouseX, mouseY)) return;
        event.setCanceled(true);
        PROCESSOR.processMouseEvent();
    }

    private static void init() {
        MinecraftForge.EVENT_BUS.post(new HudScreenInitEvent(CONTAINER));
        CONTAINER.init();
    }

    public static WidgetContainer getContent() {
        return CONTAINER;
    }

    public static boolean isOverChat(float x, float y) {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.ingameGUI == null) return false;
        boolean chatOpen = mc.currentScreen instanceof GuiChat;

        // Check if we are on top of the text box
        if(chatOpen) {
            float chatLeft = 2;
            float chatRight = renderWidth - 2;
            float chatTop = renderHeight - 14;
            float chatBottom = renderHeight - 2;
            if(x >= chatLeft && x <= chatRight && y >= chatTop && y <= chatBottom) return true;
        }

        float[] chatBbox = getChatLinesBoundingBox();
        return x >= chatBbox[0] && x <= chatBbox[2] && y <= chatBbox[3] && y >= chatBbox[1];
    }

    public static float[] getChatLinesBoundingBox() {
        Minecraft mc = Minecraft.getMinecraft();
        boolean chatOpen = mc.currentScreen instanceof GuiChat;
        GuiNewChat chat = mc.ingameGUI.getChatGUI();
        float chatLeft = 0;
        float chatBottom = renderHeight - chat.getChatHeight();
        float chatRight = chatLeft + chat.getChatWidth() + 6;
        float chatTop = renderHeight - 40;
        float scale = chat.getChatScale();
        try {
            int updateCounter = getChatUpdateCounter();
            @SuppressWarnings("unchecked") // Taken care of in the catch block
            List<ChatLine> lines = (List<ChatLine>)NEW_CHAT_DRAW_CHAT_LINES_FIELD.get(chat);
            int visibleChatLines = 0;
            for(ChatLine line: lines) {
                if(chatOpen || (line != null && updateCounter - line.getUpdatedCounter() < 200)) visibleChatLines++;
            }
            chatBottom = renderHeight - 40;
            chatTop = chatBottom - Math.min(visibleChatLines * 9 / scale, chat.getChatHeight());
        } catch (ClassCastException | IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException("Reflection fail when checking chat position!");
        }
        return new float[] { chatLeft, chatTop, chatRight, chatBottom };
    }

    public static int getChatUpdateCounter() throws IllegalArgumentException, IllegalAccessException {
        return GUI_INGAME_UPDATE_COUNTER_FIELD.getInt(Minecraft.getMinecraft().ingameGUI);
    }

    private static class HudScreenContainer extends WidgetContainer {

        public HudScreenContainer(int z) {
            super(z);
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getWidth() {
            return renderWidth;
        }

        @Override
        public float getHeight() {
            return renderHeight;
        }

    }

}
