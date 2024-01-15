package fr.thesmyler.terramap.gui.widgets.markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MainPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.RightClickMarkerController;

public abstract class MarkerControllerManager {

    private static final Map<MapContext, List<Class<? extends MarkerController<?>>>> CONTROLLER_CLASSES;

    static {
        CONTROLLER_CLASSES = new HashMap<>();
        for(MapContext c: MapContext.values()) {
            CONTROLLER_CLASSES.put(c, new ArrayList<>());
        }

    }

    public static void registerController(Class<? extends MarkerController<?>> controller, MapContext context) {
        try {
            controller.newInstance();
            CONTROLLER_CLASSES.get(context).add(controller);
        } catch(Exception e) {
            TerramapMod.logger.error("Failed to create a test marker controller instance for " + controller.getCanonicalName());
            TerramapMod.logger.error("This marker controller class will not be registered. See stack trace for details.");
            TerramapMod.logger.catching(e);
        }
    }

    public static MarkerController<?>[] createControllers(MapContext context) {
        MarkerController<?>[] controllers = new MarkerController[CONTROLLER_CLASSES.get(context).size()];
        int i = 0;
        for(Class<? extends MarkerController<?>> clazz: CONTROLLER_CLASSES.get(context)) {
            try {
                controllers[i++] = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                TerramapMod.logger.error("Failed to create a marker controller, things will be unstable!");
                TerramapMod.logger.error("Failed to instantiate " + clazz.getCanonicalName());
                TerramapMod.logger.catching(e);
            }
        }
        return controllers;
    }

    public static void registerBuiltInControllers() {
        MarkerControllerManager.registerController(RightClickMarkerController.class, MapContext.FULLSCREEN);
        MarkerControllerManager.registerController(MainPlayerMarkerController.class, MapContext.FULLSCREEN);
        MarkerControllerManager.registerController(MainPlayerMarkerController.class, MapContext.MINIMAP);
        MarkerControllerManager.registerController(OtherPlayerMarkerController.class, MapContext.FULLSCREEN);
        MarkerControllerManager.registerController(OtherPlayerMarkerController.class, MapContext.MINIMAP);
        MarkerControllerManager.registerController(AnimalMarkerController.class, MapContext.FULLSCREEN);
        MarkerControllerManager.registerController(AnimalMarkerController.class, MapContext.MINIMAP);
        MarkerControllerManager.registerController(MobMarkerController.class, MapContext.FULLSCREEN);
        MarkerControllerManager.registerController(MobMarkerController.class, MapContext.MINIMAP);
    }

}
