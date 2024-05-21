package fr.thesmyler.smylibgui.widgets;

import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget.ButtonSprites;
import net.smyler.smylib.gui.DrawContext;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.math.Math.clamp;

//TODO Use a texture
public class ScrollbarWidget extends WidgetContainer {

    private static final Color BAR_BG_COLOR = Color.DARKER_OVERLAY;
    private static final Color BAR_BORDER_COLOR = Color.BLACK;
    private static final Color DRAG_BG_COLOR = Color.DARK_GRAY;
    private static final Color DRAG_BG_COLOR_HOVER = Color.SELECTION;
    private static final Color DRAG_BORDER_COLOR = Color.MEDIUM_GRAY;
    
    private float x, y, length;

    private SpriteButtonWidget backwardButton;
    private SpriteButtonWidget forwardButton;
    private final Draggable drag = new Draggable();
    private float progress = 0f;
    private float targetProgress;
    private float scrollResponsiveness = 0.02f;
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
        getGameClient().soundSystem().playClickSound();
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
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
        float width = this.getWidth();
        float height = this.getHeight();
        context.drawRectangle(x, y, x + width, y + height, BAR_BG_COLOR);
        context.drawRectangle(x, y, x + 1, y + height, BAR_BORDER_COLOR);
        context.drawRectangle(x + width - 1, y, x + width, y + height, BAR_BORDER_COLOR);
        super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
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
        this.targetProgress = clamp(progress, 0, 1);
        return this;
    }
    
    public ScrollbarWidget setProgressNoAnimation(float progress) {
        this.progress = clamp(progress, 0, 1);
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
    
    private class Draggable implements Widget {

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
            ScrollbarWidget.this.progress = clamp(
                    ScrollbarWidget.this.orientation.dragMoveToProgress(ScrollbarWidget.this.length, ScrollbarWidget.this.progress, ScrollbarWidget.this.viewPort, dX, dY),
                    0, 1);
            ScrollbarWidget.this.targetProgress = ScrollbarWidget.this.progress;
        }

        @Override
        public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            Color bgcolor = hovered || focused ? DRAG_BG_COLOR_HOVER: DRAG_BG_COLOR;
            float height = this.getHeight();
            context.drawRectangle(x, y, x + this.getWidth(), y + height, bgcolor);

            context.drawRectangle(x, y, x + this.getWidth(), y + 1, DRAG_BORDER_COLOR);
            context.drawRectangle(x, y + height - 1, x + this.getWidth(), y + height, DRAG_BORDER_COLOR);
            context.drawRectangle(x, y, x + 1, y + height, DRAG_BORDER_COLOR);
            context.drawRectangle(x + this.getWidth() - 1, y, x + this.getWidth(), y  + height, DRAG_BORDER_COLOR);

        }

    }

    public float getScrollResponsiveness() {
        return this.scrollResponsiveness;
    }

    public ScrollbarWidget setScrollResponsiveness(float responsiveness) {
        checkArgument(responsiveness >= 0, "Invalid scroll responsiveness, it needs to be positive.");
        this.scrollResponsiveness = responsiveness;
        return this;
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
            SpriteButtonWidget newBackwardButton(float length) {
                return new SpriteButtonWidget(0f, 0f, 1, ButtonSprites.LEFT);
            }

            @Override
            SpriteButtonWidget newForwardButton(float length) {
                return new SpriteButtonWidget(length - 15f, 0f, 1, ButtonSprites.RIGHT);
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
            SpriteButtonWidget newBackwardButton(float length) {
                return new SpriteButtonWidget(0f, 0f, 1, ButtonSprites.UP);
            }

            @Override
            SpriteButtonWidget newForwardButton(float length) {
                return new SpriteButtonWidget(0f, length - 15f, 1, ButtonSprites.DOWN);
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
        
        abstract SpriteButtonWidget newBackwardButton(float length);
        
        abstract SpriteButtonWidget newForwardButton(float length);
        
        abstract float dragX(float length, float progress, float viewPort);
        
        abstract float dragY(float length, float progress, float viewPort);
        
        abstract float dragWidth(float length, float viewPort);
        
        abstract float dragHeight(float length, float viewPort);
        
        abstract float dragMoveToProgress(float length, float progress, float viewPort, float dX, float dY);
        
    }

}
