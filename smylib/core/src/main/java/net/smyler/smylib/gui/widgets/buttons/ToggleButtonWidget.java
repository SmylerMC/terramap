package net.smyler.smylib.gui.widgets.buttons;

import java.util.function.Consumer;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.sprites.Sprite;

import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;


public class ToggleButtonWidget extends AbstractButtonWidget {

    protected boolean value;
    private final Sprite onEnabledSprite;
    private final Sprite offEnabledSprite;
    private final Sprite onDisabledSprite;
    private final Sprite offDisabledSprite;
    private final Sprite onFocusedSprite;
    private final Sprite offFocusedSprite;
    protected Consumer<Boolean> onChange;

    public ToggleButtonWidget(
            float x, float y, int z,
            Sprite onEnabledSprite, Sprite offEnabledSprite,
            Sprite onDisabledSprite, Sprite offDisabledSprite,
            Sprite onFocusedSprite, Sprite offFocusedSprite,
            boolean startValue,
            Consumer<Boolean> onChange
    ) {
        super(x, y, z, (float)onEnabledSprite.width(), (float)onEnabledSprite.height(), null);
        this.onEnabledSprite = onEnabledSprite;
        this.offEnabledSprite = offEnabledSprite;
        this.onDisabledSprite = onDisabledSprite;
        this.offDisabledSprite = offDisabledSprite;
        this.onFocusedSprite = onFocusedSprite;
        this.offFocusedSprite = offFocusedSprite;
        this.onClick = this::toggle;
        this.onDoubleClick = this::toggle;
        this.value = startValue;
        this.onChange = onChange;
    }

    public ToggleButtonWidget(float x, float y, int z, boolean startValue, Consumer<Boolean> onChange) {
        this(
                x, y, z,
                BUTTON_TOGGLE_ON, BUTTON_TOGGLE_OFF,
                BUTTON_TOGGLE_ON_DISABLED, BUTTON_TOGGLE_OFF_DISABLED,
                BUTTON_TOGGLE_ON_HIGHLIGHTED, BUTTON_TOGGLE_OFF_HIGHLIGHTED,
                startValue, onChange
        );
    }

    public ToggleButtonWidget(float x, float y, int z, boolean startValue) {
        this(
                x, y, z,
                BUTTON_TOGGLE_ON, BUTTON_TOGGLE_OFF,
                BUTTON_TOGGLE_ON_DISABLED, BUTTON_TOGGLE_OFF_DISABLED,
                BUTTON_TOGGLE_ON_HIGHLIGHTED, BUTTON_TOGGLE_OFF_HIGHLIGHTED,
                startValue, null
        );
    }

    public ToggleButtonWidget(
            int z,
            Sprite onEnabledSprite, Sprite offEnabledSprite,
            Sprite onDisabledSprite, Sprite offDisabledSprite,
            Sprite onFocusedSprite, Sprite offFocusedSprite,
            boolean startValue,
            Consumer<Boolean> onChange
    ) {
        this(
                0, 0, z,
                onEnabledSprite, offEnabledSprite,
                onDisabledSprite, offDisabledSprite,
                onFocusedSprite, offFocusedSprite,
                startValue,
                onChange
        );
    }

    public ToggleButtonWidget(int z, boolean startValue, Consumer<Boolean> onChange) {
        this(
                0f, 0f, z,
                BUTTON_TOGGLE_ON, BUTTON_TOGGLE_OFF,
                BUTTON_TOGGLE_ON_DISABLED, BUTTON_TOGGLE_OFF_DISABLED,
                BUTTON_TOGGLE_ON_HIGHLIGHTED, BUTTON_TOGGLE_OFF_HIGHLIGHTED,
                startValue, onChange
        );
    }

    public ToggleButtonWidget(int z, boolean startValue) {
        this(
                0f, 0f, z,
                BUTTON_TOGGLE_ON, BUTTON_TOGGLE_OFF,
                BUTTON_TOGGLE_ON_DISABLED, BUTTON_TOGGLE_OFF_DISABLED,
                BUTTON_TOGGLE_ON_HIGHLIGHTED, BUTTON_TOGGLE_OFF_HIGHLIGHTED,
                startValue, null
        );
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Sprite sprite;
        if(!this.isEnabled()) {
            if(this.getState()) {
                sprite = this.onDisabledSprite;
            } else {
                sprite = this.offDisabledSprite;
            }
        } else if(hovered || hasFocus) {
            if(this.getState()) {
                sprite = this.onFocusedSprite;
            } else {
                sprite = this.offFocusedSprite;
            }
        } else {
            if(this.getState()) {
                sprite = this.onEnabledSprite;
            } else {
                sprite = this.offEnabledSprite;
            }
        }
        context.drawSprite(x, y, sprite);
    }

    public void toggle() {
        this.value = !this.value;
        if(this.onChange != null) this.onChange.accept(this.value);
    }

    public boolean getState() {
        return this.value;
    }

    public void setState(boolean state) {
        this.value = state;
    }

    public ToggleButtonWidget setOnChange(Consumer<Boolean> action) {
        this.onChange = action;
        return this;
    }

}
