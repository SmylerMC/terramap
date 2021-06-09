package fr.thesmyler.terramap.util;

public final class WebMercatorBounds {
    
    public final int lowerX, lowerY, upperX, upperY;

    public WebMercatorBounds(int lowerX, int lowerY, int upperX, int upperY) {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;
    }

    public boolean contains(TilePos pos) {
        return 
                this.lowerX <= pos.xPosition
                && this.lowerY <= pos.yPosition
                && pos.xPosition <= this.upperX
                && pos.yPosition <= this.upperY;
    }
    
}
