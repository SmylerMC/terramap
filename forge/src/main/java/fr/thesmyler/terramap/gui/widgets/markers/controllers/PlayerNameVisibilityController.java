package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED;

public class PlayerNameVisibilityController implements FeatureVisibilityController {

    public static final String ID = "player_names";

    private final MainPlayerMarkerController main;
    private final OtherPlayerMarkerController other;
    private final ToggleButtonWidget button;

    public PlayerNameVisibilityController(MainPlayerMarkerController main, OtherPlayerMarkerController other) {
        this.main = main;
        this.other = other;
        this.button = new ToggleButtonWidget(10, 14, 14,
                BUTTON_VISIBILITY_ON_15, BUTTON_VISIBILITY_OFF_15,
                BUTTON_VISIBILITY_ON_15_DISABLED, BUTTON_VISIBILITY_OFF_15_DISABLED,
                BUTTON_VISIBILITY_ON_15_HIGHLIGHTED, BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED,
                this.main.doesShowNames(),
                b -> {
                    this.main.setShowNames(b);
                    this.other.setShowNames(b);
                }
                );
        this.button.setTooltip(getGameClient().translator().format("terramap.terramapscreen.markercontrollers.buttons.name"));
    }

    @Override
    public boolean showButton() {
        return true;
    }

    @Override
    public ToggleButtonWidget getButton() {
        return button;
    }

    @Override
    public String getSaveName() {
        return ID;
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.main.setShowNames(visibility);
        this.other.setShowNames(visibility);
        this.button.setState(visibility);
    }

    @Override
    public boolean isVisible() {
        return this.main.doesShowNames();
    }

}
