package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;

public interface FeatureVisibilityController {

    boolean showButton();

    ToggleButtonWidget getButton();

    String getSaveName();

    void setVisibility(boolean visibility);

    boolean getVisibility();

}