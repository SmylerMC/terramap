package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

//TODO Use a texture
public class ScrollbarWidget extends WidgetContainer {

    private static final Color BAR_BG_COLOR = Color.DARKER_OVERLAY;
    private static final Color BAR_BORDER_COLOR = Color.BLACK;
    private static final Color DRAG_BG_COLOR = Color.DARK_GRAY;
    private static final Color DRAG_BG_COLOR_HOVER = Color.SELECTION;
    private static final Color DRAG_BORDER_COLOR = Color.MEDIUM_GRAY;
    
    private float x, y, length;

    private TexturedButtonWidget backwardButton;
    private TexturedButtonWidget forwardButton;
    private final Draggable drag = new Draggable();
    private float progress = 0f;
    private float targetProgress;
    private final float scrollResponsiveness = 0.02f;
    private float viewPort = 0.1f;
    private final ScrollbarOrientation orientation;

    private long lastUpdateTime = 0;
    
    public ScrollbarWidget(float x, float y, int z, ScrollbarOrientation orientation, float length) {
        super(z);
        this.orientation = orientation;
        this.init();
    }
    
    public ScrollbarWidget(int z, ScrollbarOrientation orientation) {
        this(0, 0, z, orientation, 10);
    }
    
    @Override
    public void init() {
        this.removeAllWidgets();
        this.backwardButton = this.orientation.newBackwardButton(this.length);
        this.backwardButton.setOnClick(this::scrollBackward);
        this.forwardButton = this.orientation.newForwardButton(this.length);
        this.forwardButton.setOnClick(this::scrollForward);
        this.backwardButton.enable();
        this.forwardButton.enable();
        this.addWidget(this.backwardButton).addWidget(this.forwardButton);
        this.addWidget(this.drag);
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        float i = 4;
        float y = this.drag.getY();
        if(mouseY > y + this.drag.getHeight() && mouseY < this.getHeight() - this.forwardButton.getHeight()) {
            for(; i>0; i--) this.scrollForward();
        } else if(mouseY < y && mouseY > this.backwardButton.getHeight()){
            for(; i>0; i--) this.scrollBackward();
        }
        super.onClick(mouseX, mouseY, mouseButton, parent);
        return false;
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        long ctime = System.currentTimeMillis();
        long dt = ctime - this.lastUpdateTime;
        super.onUpdate(mouseX, mouseY, parent);
        if(Math.abs(this.targetProgress - this.progress) < 0.00001f * this.viewPort) {
            this.progress = this.targetProgress;
        } else if(dt < 10000) {
            double maxDprog = this.targetProgress - this.progress;
            double dprog = this.scrollResponsiveness * maxDprog * dt;
            dprog = maxDprog > 0 ? Math.min(dprog, maxDprog) : Math.max(dprog, maxDprog);
            this.progress += dprog;
        }
        this.lastUpdateTime = ctime;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        return this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
        float width = this.getWidth();
        float height = this.getHeight();
        RenderUtil.drawRect(x, y, x + width, y + height, BAR_BG_COLOR);
        RenderUtil.drawRect(x, y, x + 1, y + height, BAR_BORDER_COLOR);
        RenderUtil.drawRect(x + width - 1, y, x + width, y + height, BAR_BORDER_COLOR);
        super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
    }

    public void scrollBackward() {
        this.targetProgress = Math.max(0f, this.targetProgress - this.viewPort * 0.5f);
    }

    public void scrollForward() {
        this.targetProgress = Math.min(1f, this.targetProgress + this.viewPort * 0.5f);
    }

    public float getProgress() {
        return this.progress;
    }

    public ScrollbarWidget setProgress(float progress) {
        this.targetProgress = MathHelper.clamp(progress, 0, 1);
        return this;
    }
    
    public ScrollbarWidget setProgressNoAnimation(float progress) {
        this.progress = MathHelper.clamp(progress, 0, 1);
        this.targetProgress = this.progress;
        return this;
    }

    public double getViewPort() {
        return this.viewPort;
    }

    public ScrollbarWidget setViewPort(float viewPort) {
        this.viewPort = viewPort;
        return this;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.viewPort < 1;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    public ScrollbarWidget setX(float x) {
        this.x = x;
        this.init();
        return this;
    }

    @Override
    public float getY() {
        return this.y;
    }
    
    public ScrollbarWidget setY(float y) {
        this.y = y;
        this.init();
        return this;
    }
    
    public ScrollbarWidget setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.init();
        return this;
    }

    @Override
    public float getWidth() {
        return this.orientation.width(this.length);
    }

    @Override
    public float getHeight() {
        return this.orientation.height(this.length);
    }
    
    public ScrollbarWidget setLength(float length) {
        this.length = length;
        this.init();
        return this;
    }
    
    private class Draggable implements IWidget {

        @Override
        public float getX() {
            return ScrollbarWidget.this.orientation.dragX(ScrollbarWidget.this.length, ScrollbarWidget.this.progress, ScrollbarWidget.this.viewPort);
        }

        @Override
        public float getY() {
            return ScrollbarWidget.this.orientation.dragY(ScrollbarWidget.this.length, ScrollbarWidget.this.progress, ScrollbarWidget.this.viewPort);
        }

        @Override
        public int getZ() {
            return 1;
        }

        @Override
        public float getWidth() {
            return ScrollbarWidget.this.orientation.dragWidth(ScrollbarWidget.this.length, ScrollbarWidget.this.viewPort);
        }

        @Override
        public float getHeight() {
            return ScrollbarWidget.this.orientation.dragHeight(ScrollbarWidget.this.length, ScrollbarWidget.this.viewPort);
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int button, WidgetContainer parent, long dt) {
            ScrollbarWidget.this.progress = MathHelper.clamp(
                    ScrollbarWidget.this.orientation.dragMoveToProgress(ScrollbarWidget.this.length, ScrollbarWidget.this.progress, ScrollbarWidget.this.viewPort, dX, dY),
                    0, 1);
            ScrollbarWidget.this.targetProgress = ScrollbarWidget.this.progress;
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            Color bgcolor = hovered || focused ? DRAG_BG_COLOR_HOVER: DRAG_BG_COLOR;
            float height = this.getHeight();
            RenderUtil.drawRect(x, y, x + this.getWidth(), y + height, bgcolor);

            RenderUtil.drawRect(x, y, x + this.getWidth(), y + 1, DRAG_BORDER_COLOR);
            RenderUtil.drawRect(x, y + height - 1, x + this.getWidth(), y + height, DRAG_BORDER_COLOR);
            RenderUtil.drawRect(x, y, x + 1, y + height, DRAG_BORDER_COLOR);
            RenderUtil.drawRect(x + this.getWidth() - 1, y, x + this.getWidth(), y  + height, DRAG_BORDER_COLOR);

        }

    }
    
    public enum ScrollbarOrientation {
        
        HORIZONTAL {
            
            @Override
            float width(float length) {
                return length;
            }

            @Override
            float height(float length) {
                return 15f;
            }

            @Override
            TexturedButtonWidget newBackwardButton(float length) {
                return new TexturedButtonWidget(0f, 0f, 1, IncludedTexturedButtons.LEFT);
            }

            @Override
            TexturedButtonWidget newForwardButton(float length) {
                return new TexturedButtonWidget(length - 15f, 0f, 1, IncludedTexturedButtons.RIGHT);
            }

            @Override
            float dragX(float length, float progress, float viewPort) {
                float h = length - 30 - this.dragWidth(length, viewPort);
                return 15 + h*progress;
            }

            @Override
            float dragY(float length, float progress, float viewPort) {
                return 1;
            }

            @Override
            float dragWidth(float length, float viewPort) {
                return Math.min(viewPort, 1) * (length - 30);
            }

            @Override
            float dragHeight(float length, float viewPort) {
                return 13;
            }

            @Override
            float dragMoveToProgress(float length, float progress, float viewPort, float dX, float dY) {
                float h = length - 30 - this.dragWidth(length, viewPort);
                float frac = this.dragX(length, progress, viewPort) + dX - 15;
                return frac / h;
            }
            
        },
        VERTICAL {
            
            @Override
            float width(float length) {
                return 15f;
            }

            @Override
            float height(float length) {
                return length;
            }

            @Override
            TexturedButtonWidget newBackwardButton(float length) {
                return new TexturedButtonWidget(0f, 0f, 1, IncludedTexturedButtons.UP);
            }

            @Override
            TexturedButtonWidget newForwardButton(float length) {
                return new TexturedButtonWidget(0f, length - 15f, 1, IncludedTexturedButtons.DOWN);
            }

            @Override
            float dragX(float length, float progress, float viewPort) {
                return 1f;
            }

            @Override
            float dragY(float length, float progress, float viewPort) {
                float h = length - 30 - this.dragHeight(length, viewPort);
                return 15 + h*progress;
            }

            @Override
            float dragWidth(float length, float viewPort) {
                return 13;
            }

            @Override
            float dragHeight(float length, float viewPort) {
                return Math.min(viewPort, 1) * (length - 30);
            }

            @Override
            float dragMoveToProgress(float length, float progress, float viewPort, float dX, float dY) {
                float h = length - 30 - this.dragHeight(length, viewPort);
                float frac = this.dragY(length, progress, viewPort) + dY - 15;
                return frac / h;
            }
            
        };
        
        abstract float width(float length);
        
        abstract float height(float length);
        
        abstract TexturedButtonWidget newBackwardButton(float length);
        
        abstract TexturedButtonWidget newForwardButton(float length);
        
        abstract float dragX(float length, float progress, float viewPort);
        
        abstract float dragY(float length, float progress, float viewPort);
        
        abstract float dragWidth(float length, float viewPort);
        
        abstract float dragHeight(float length, float viewPort);
        
        abstract float dragMoveToProgress(float length, float progress, float viewPort, float dX, float dY);
        
    }

}
