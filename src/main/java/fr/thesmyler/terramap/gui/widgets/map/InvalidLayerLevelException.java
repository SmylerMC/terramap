package fr.thesmyler.terramap.gui.widgets.map;

public class InvalidLayerLevelException extends RuntimeException {

    private static final long serialVersionUID = 733854261970510282L;

    public InvalidLayerLevelException(Object o) {
        super(o.toString());
    }

}
