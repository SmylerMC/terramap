package net.smyler.smylib.gui.widgets.buttons;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.sprites.Sprite;
import org.jetbrains.annotations.Nullable;

import net.smyler.smylib.gui.containers.WidgetContainer;

import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;


public class SpriteButtonWidget extends AbstractButtonWidget {

    protected Sprite sprite;
    protected Sprite spriteDisabled;
    protected Sprite spriteHighlighted;

    public SpriteButtonWidget(float x, float y, int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
        super(x, y, z, (float)sprite.width(), (float)sprite.height(), onClick, onDoubleClick);
        this.sprite = sprite;
        this.spriteDisabled = spriteDisabled;
        this.spriteHighlighted = spriteHighlighted;
    }

    public SpriteButtonWidget(float x, float y, int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted, Runnable onClick) {
        this(x, y, z, sprite, spriteDisabled, spriteHighlighted, onClick, onClick);
    }

    public SpriteButtonWidget(float x, float y, int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted) {
        this(x, y, z, sprite, spriteDisabled, spriteHighlighted, null);
        this.disable();
    }

    public SpriteButtonWidget(float x, float y, int z, ButtonSprites sprites, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
        this(x, y, z, sprites.sprite, sprites.spriteDisabled, sprites.spriteHighlighted, onClick, onDoubleClick);
    }

    public SpriteButtonWidget(float x, float y, int z, ButtonSprites sprites, @Nullable Runnable onClick) {
        this(x, y, z, sprites, onClick, onClick);
    }

    public SpriteButtonWidget(float x, float y, int z, ButtonSprites sprites) {
        this(x, y, z, sprites, null);
        this.disable();
    }

    public SpriteButtonWidget(int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
        this(0, 0, z, sprite, spriteDisabled, spriteHighlighted, onClick, onDoubleClick);
    }

    public SpriteButtonWidget(int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted, Runnable onClick) {
        this(z, sprite, spriteDisabled, spriteHighlighted, onClick, onClick);
    }

    public SpriteButtonWidget(int z, Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted) {
        this(z, sprite, spriteDisabled, spriteHighlighted, null);
        this.disable();
    }

    public SpriteButtonWidget(int z, ButtonSprites sprites, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
        this(z, sprites.sprite, sprites.spriteDisabled, sprites.spriteHighlighted, onClick, onDoubleClick);
    }

    public SpriteButtonWidget(int z, ButtonSprites sprites, @Nullable Runnable onClick) {
        this(z, sprites, onClick, onClick);
    }

    public SpriteButtonWidget(int z, ButtonSprites sprites) {
        this(z, sprites, null);
        this.disable();
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Sprite sprite = this.sprite;
        if(!this.isEnabled()) {
            sprite = this.spriteDisabled;
        } else if(hovered || hasFocus) {
            sprite = this.spriteHighlighted;
        }
        context.drawSprite(x, y, sprite);
    }

    public static class ButtonSprites {

        // 15x15
        public static final ButtonSprites BLANK_15 = new ButtonSprites(BUTTON_BLANK_15.sprite, BUTTON_BLANK_15_DISABLED.sprite, BUTTON_BLANK_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites PLUS = new ButtonSprites(BUTTON_PLUS_15.sprite, BUTTON_PLUS_15_DISABLED.sprite, BUTTON_PLUS_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites MINUS = new ButtonSprites(BUTTON_MINUS_15.sprite, BUTTON_MINUS_15_DISABLED.sprite, BUTTON_MINUS_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites LEFT = new ButtonSprites(BUTTON_LEFT_15.sprite, BUTTON_LEFT_15_DISABLED.sprite, BUTTON_LEFT_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites RIGHT = new ButtonSprites(BUTTON_RIGHT_15.sprite, BUTTON_RIGHT_15_DISABLED.sprite, BUTTON_RIGHT_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites UP = new ButtonSprites(BUTTON_UP_15.sprite, BUTTON_UP_15_DISABLED.sprite, BUTTON_UP_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites DOWN = new ButtonSprites(BUTTON_DOWN_15.sprite, BUTTON_DOWN_15_DISABLED.sprite, BUTTON_DOWN_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites CROSS = new ButtonSprites(BUTTON_CROSS_15.sprite, BUTTON_CROSS_15_DISABLED.sprite, BUTTON_CROSS_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites CENTER = new ButtonSprites(BUTTON_CENTER_15.sprite, BUTTON_CENTER_15_DISABLED.sprite, BUTTON_CENTER_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites PAPER = new ButtonSprites(BUTTON_PAPER_15.sprite, BUTTON_PAPER_15_DISABLED.sprite, BUTTON_PAPER_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites WRENCH = new ButtonSprites(BUTTON_WRENCH_15.sprite, BUTTON_WRENCH_15_DISABLED.sprite, BUTTON_WRENCH_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites BURGER_15 = new ButtonSprites(BUTTON_BURGER_15.sprite, BUTTON_BURGER_15_DISABLED.sprite, BUTTON_BURGER_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites TRASH = new ButtonSprites(BUTTON_TRASH_15.sprite, BUTTON_TRASH_15_DISABLED.sprite, BUTTON_TRASH_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites OFFSET = new ButtonSprites(BUTTON_OFFSET_15.sprite, BUTTON_OFFSET_15_DISABLED.sprite, BUTTON_OFFSET_15_HIGHLIGHTED.sprite);
        public static final ButtonSprites OFFSET_WARNING = new ButtonSprites(BUTTON_OFFSET_WARNING_15.sprite, BUTTON_OFFSET_WARNING_15_DISABLED.sprite, BUTTON_OFFSET_WARNING_15_HIGHLIGHTED.sprite);

        // 20x20
        public static final ButtonSprites BLANK_20 = new ButtonSprites(BUTTON_BLANK_20.sprite, BUTTON_BLANK_20_DISABLED.sprite, BUTTON_BLANK_20_HIGHLIGHTED.sprite);
        public static final ButtonSprites BURGER_20 = new ButtonSprites(BUTTON_BURGER_20.sprite, BUTTON_BURGER_20_DISABLED.sprite, BUTTON_BURGER_20_HIGHLIGHTED.sprite);

        // 21x21
        public static final ButtonSprites BLANK_21 = new ButtonSprites(BUTTON_BLANK_20.sprite, BUTTON_BLANK_20_DISABLED.sprite, BUTTON_BLANK_20_HIGHLIGHTED.sprite);
        public static final ButtonSprites SEARCH = new ButtonSprites(BUTTON_SEARCH_21.sprite, BUTTON_SEARCH_21_DISABLED.sprite, BUTTON_SEARCH_21_HIGHLIGHTED.sprite);

        final Sprite sprite, spriteDisabled, spriteHighlighted;

        public ButtonSprites(Sprite sprite, Sprite spriteDisabled, Sprite spriteHighlighted) {
            this.sprite = sprite;
            this.spriteDisabled = spriteDisabled;
            this.spriteHighlighted = spriteHighlighted;
        }

    }

}
