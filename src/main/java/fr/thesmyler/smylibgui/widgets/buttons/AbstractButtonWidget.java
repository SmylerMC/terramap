package fr.thesmyler.smylibgui.widgets.buttons;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.AbstractSolidWidget;

//TODO Add the possibility to bind a keybinding to a button
public abstract class AbstractButtonWidget extends AbstractSolidWidget {

    protected Runnable onClick;
    protected Runnable onDoubleClick;

    public AbstractButtonWidget(float x, float y, int z, float width, float height, @Nullable Runnable onClick, @Nullable Runnable onDoubleClick) {
        super(x, y, z, width, height);
        this.onClick = onClick;
        this.onDoubleClick = onDoubleClick;
    }

    public AbstractButtonWidget(float x, float y, int z, float width, float height, @Nullable Runnable onClick) {
        this(x, y, z, width, height, onClick, onClick);
    }

    public AbstractButtonWidget(float x, float y, int z, float width, float height) {
        this(x, y, z, width, height, null);
        this.disable();
    }

    @Override
    public abstract void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent);

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        SmyLibGui.getSoundSystem().playClickSound();
        parent.setFocus(null); // We don't want to keep the focus
        if(this.onClick != null && mouseButton == 0) {
            this.onClick.run();
        }
        return false;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        SmyLibGui.getSoundSystem().playClickSound();
        parent.setFocus(null);
        if(mouseButton == 0) {
            if(this.onDoubleClick != null) {
                this.onDoubleClick.run();
            } else if(this.onClick != null){
                this.onClick.run();
            }
        }
        return false;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public AbstractButtonWidget setOnClick(Runnable onClick) {
        this.onClick = onClick;
        return this;
    }

    public Runnable getOnDoubleClick() {
        return onDoubleClick;
    }

    public AbstractButtonWidget setOnDoubleClick(Runnable onDoubleClick) {
        this.onDoubleClick = onDoubleClick;
        return this;
    }

    public AbstractButtonWidget setX(float x) {
        this.x = x;
        return this;
    }

    public AbstractButtonWidget setY(float y) {
        this.y = y;
        return this;
    }

}
