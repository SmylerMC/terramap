package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;

public interface FeatureVisibilityController {

    boolean showButton();

    ToggleButtonWidget getButton();

    String getSaveName();

    void setVisibility(boolean visibility);

    boolean isVisible();

}