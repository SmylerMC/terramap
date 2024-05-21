package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED;


public class PlayerDirectionsVisibilityController implements FeatureVisibilityController {

    public static final String ID = "player_directions";

    private final MainPlayerMarkerController main;
    private final OtherPlayerMarkerController other;
    private final ToggleButtonWidget button;

    public PlayerDirectionsVisibilityController(MainPlayerMarkerController main, OtherPlayerMarkerController other) {
        this.main = main;
        this.other = other;
        this.button = new ToggleButtonWidget(10, 14, 14,
                BUTTON_VISIBILITY_ON_15.sprite, BUTTON_VISIBILITY_OFF_15.sprite,
                BUTTON_VISIBILITY_ON_15_DISABLED.sprite, BUTTON_VISIBILITY_OFF_15_DISABLED.sprite,
                BUTTON_VISIBILITY_ON_15_HIGHLIGHTED.sprite, BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED.sprite,
                this.main.doesShowDirection(),
                b -> {
                    this.main.setShowDirection(b);
                    this.other.setShowDirection(b);
                }
                );
        this.button.setTooltip(getGameClient().translator().format("terramap.terramapscreen.markercontrollers.buttons.direction"));
    }

    @Override
    public boolean showButton() {
        return true;
    }

    @Override
    public ToggleButtonWidget getButton() {
        return this.button;
    }

    @Override
    public String getSaveName() {
        return ID;
    }

    @Override
    public void setVisibility(boolean visibility) {
        this.main.setShowDirection(visibility);
        this.other.setShowDirection(visibility);
        this.button.setState(visibility);
    }

    @Override
    public boolean isVisible() {
        return this.main.doesShowDirection();
    }

}
