package fr.thesmyler.terramap.util.geo;

public final class WebMercatorBounds {
    
    public final int lowerX, lowerY, upperX, upperY;

    public WebMercatorBounds(int lowerX, int lowerY, int upperX, int upperY) {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;
    }

    public boolean contains(TilePos pos) {
        double x = pos.getX();
        double y = pos.getY();
        return 
                this.lowerX <= x
                && this.lowerY <= y
                && x <= this.upperX
                && y <= this.upperY;
    }
    
}
