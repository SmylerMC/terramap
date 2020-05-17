package fr.smyler.terramap.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile.InvalidTileCoordinatesException;
import fr.smyler.terramap.maps.utils.TerramapUtils;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.EarthBiomeProvider;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

//TODO Max zoom level
//TODO Min zoom level
//TODO Better zoom
//TODO Custom scaling
public class GuiTiledMap extends GuiScreen {

	/*
	 * The position of the map on the GUI
	 */
	
	private final float GUI_SIZING = 1;
	protected int x;
	protected int y;
	
	protected boolean visible;
	protected boolean hovered;
	
	protected TiledMap<?> map;
	
	protected double focusLatitude;
	protected double focusLongitude;
	protected boolean debug = false; //Show tiles borders or not
	
	protected double zoomLevel;
	protected double zoomMotion = 0;
	
	public GuiTiledMap(TiledMap<?> map) {
		this.visible = true;
		this.hovered = false;
		this.map = map;
		this.zoomLevel = map.getZoomLevel();
		this.setZoom(13);
		this.focusLatitude = 0;
		this.focusLongitude = 0;
	}
	
	@Override
	public void initGui() {
		Minecraft mc = Minecraft.getMinecraft();
		this.initGui(0, 0, mc.displayWidth, mc.displayHeight);
	}
	
	public void initGui(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		GeographicProjection proj = ((EarthBiomeProvider)Minecraft.getMinecraft().getIntegratedServer().getWorld(0).getBiomeProvider()).projection;
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		double coords[] = proj.toGeo(p.posX, p.posZ);
		this.focusLatitude = coords[1];
		this.focusLongitude = coords[0];
	}
    
	//FIXME Zooming to much kills it
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawMap(mouseX, mouseY, partialTicks);
		Gui.drawRect(0, 0, 200, 150, 0xAA000000);
		String dispLat = "" + (float)Math.round(this.focusLatitude * 100000) / 100000;
		String dispLong = "" + (float)Math.round(this.focusLatitude * 100000) / 100000;
		this.drawString(this.fontRenderer, "Map position: " + dispLat + " " + dispLong, 10, 10, 0xFFFFFF);
		this.drawString(this.fontRenderer, "Zoom level: " + this.zoomLevel, 10, 20 + this.fontRenderer.FONT_HEIGHT, 0xFFFFFF);
		this.drawString(this.fontRenderer, "Cache queue: " + TerramapMod.cacheManager.getQueueSize(), 10, 30 + + this.fontRenderer.FONT_HEIGHT * 2, 0xFFFFFF );
		this.drawString(this.fontRenderer, "Loaded tiles: " + this.map.getLoadedCount() + "/" + this.map.getMaxLoad(), 10, 40 + this.fontRenderer.FONT_HEIGHT * 3, 0xFFFFFF);
	}
	
	private void drawMap(int mouseX, int mouseY, float partialTicks) {

		if((int)this.zoomLevel != this.map.getZoomLevel()) {
			TerramapMod.logger.info("Zooms are differents: GUI: " + this.zoomLevel + " | Map: " + this.map.getZoomLevel());
		}
		double renderFactor = this.getSizeFactor((int) this.zoomLevel);
		int renderSize = WebMercatorUtils.TILE_DIMENSIONS;
		
		long upperLeftX = (long)(
				(double)(WebMercatorUtils.getXFromLongitude(this.focusLongitude, (int)this.zoomLevel))
				- ((double)this.width) / 2f / renderFactor);
		long upperLeftY = (long)(
				(double)WebMercatorUtils.getYFromLatitude(this.focusLatitude, (int)this.zoomLevel)
				- (double)this.height / 2f / renderFactor);
		
		//TODO handle keybord input in a method
		this.handleMouseInput();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
			this.debug = !this.debug;
		}

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();

		int maxTileXY = (int) map.getSizeInTiles();
		long maxX = (long) (upperLeftX + this.width / renderFactor);
		long maxY = (long) (upperLeftY + this.height / renderFactor);

		int lowerTX = (int) Math.floor((double)upperLeftX / (double)renderSize);
		int lowerTY = (int) Math.floor((double)upperLeftY / (double)renderSize);

		for(int tX = lowerTX; tX * renderSize < maxX; tX++) {
			
			for(int tY = lowerTY; tY * renderSize < maxY; tY++) {

				RasterWebTile tile;
				
				try {
					tile = map.getTile(TerramapUtils.modulus(tX, maxTileXY), tY, (int) this.zoomLevel);
				} catch(InvalidTileCoordinatesException e) { continue ;}
				//This is the tile we would like to render, but it is not possible if it hasn't been cached yet
				RasterWebTile bestTile = tile;
				boolean lowerResRender = false;
				
				if(!TerramapMod.cacheManager.isCached(tile)) {
					lowerResRender = true;
					if(!TerramapMod.cacheManager.isBeingCached(tile))
						TerramapMod.cacheManager.cacheAsync(tile);
					while(tile.getZoom() > 0 && !TerramapMod.cacheManager.isCached(tile)) {
						tile = this.map.getTile(tile.getX() /2, tile.getY() /2, tile.getZoom()-1);
					}
				}
				
				int dispX = Math.round(this.x + tX * renderSize - upperLeftX);
				int displayWidth = (int) Math.min(renderSize, maxX - tX * renderSize);

				int displayHeight = (int) Math.min(renderSize, maxY - tY * renderSize);
				int dispY = Math.round(this.y + tY * renderSize - upperLeftY);
				
				int renderSizedSize = renderSize;
				
				int dX = 0;
				int dY = 0;
				
				if(lowerResRender) {
					int sizeFactor = (1 <<(bestTile.getZoom() - tile.getZoom()));
					
					int xInBiggerTile = (int) (bestTile.getX() - sizeFactor * tile.getX());
					int yInBiggerTile = (int) (bestTile.getY() - sizeFactor * tile.getY());
					
					double factorX = (double)xInBiggerTile / (double)sizeFactor;
					double factorY = (double)yInBiggerTile / (double)sizeFactor;
					renderSizedSize *= sizeFactor;
					dX += (int) (factorX * renderSizedSize);
					dY += (int) (factorY * renderSizedSize);
				}

				if(tX == lowerTX) {
					dX += this.x-dispX;
					dispX = this.x;
				}

				if(tY == lowerTY) {
					dY += this.y-dispY;
					dispY = this.y;
				}

				textureManager.bindTexture(tile.getTexture());
				drawModalRectWithCustomSizedTexture(
						dispX,
						dispY,
						dX, dY,
						displayWidth,
						displayHeight,
						renderSizedSize,
						renderSizedSize);
				
				if(this.debug) {
					final int RED = 0xFFFF0000;
					final int WHITE = 0xFFFFFFFF;
					this.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY,
							lowerResRender? RED : WHITE);
					this.drawHorizontalLine(
							dispX,
							dispX + displayWidth - 1,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
					this.drawVerticalLine(
							dispX,
							dispY,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
					this.drawVerticalLine(
							dispX + displayWidth - 1,
							dispY,
							dispY + displayHeight - 1,
							lowerResRender? RED : WHITE);
				}
				GlStateManager.color(255, 255, 255, 255);

				
			}
			
		}
		
	}
	
	
	@Override
    public void updateScreen(){
		
		//TODO WIP
		//handle zooming
		//this.zoom(this.zoomMotion);
		this.zoomMotion /= 1.75;
		
	}
    
    
	
    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
	@Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick){
		TerramapMod.logger.info("click move");
    }
				
	
    /**
     * Handles mouse input.
     */
    @Override
	public void handleMouseInput(){
    	
    	//Moving
    	if(Mouse.isButtonDown(0)) {
    		
    		//TODO This should adapt to the zoom level
    		int dX = Mouse.getDX();
    		int dY = Mouse.getDY();
    		
    		this.setPosition(
    				this.focusLongitude - dX/Math.pow(2, this.zoomLevel) * this.GUI_SIZING/2,
    				this.focusLatitude - dY/Math.pow(2, this.zoomLevel) * this.GUI_SIZING/2);
    	}
    	
    	//Scrolling
        int i = Mouse.getDWheel();
        float z;
        if (i != 0){
        	
            if (i > 0) z = 0.1f;
            else z = -0.1f;

            this.zoomMotion += z * 5;
            this.zoom(z * 10);
        }
    }
    
    @Override
    public void onGuiClosed() {
    	this.map.unloadAll();
    }
    
    public void zoom(double val) {
    	
    	double nzoom = this.zoomLevel + val;
    	//int newRenderSize = this.getTileRenderSize(nzoom, this.map.getZoomLevel());
    	
    	if(nzoom < 0 || this.getMaxMapSize((int)nzoom) < this.height) {
    		return;
    	}
    	
    	//int oldRenderSize = this.getTileRenderSize(this.map.getZoomLevel());
    	
    	//int mouseX = Mouse.getX() - this.x;
    	//int mouseY = Mouse.getY() - this.y;
    	
    	//TODO TEMP
    	//IRLW.logger.info(mouseX);
    	//IRLW.logger.info(mouseY);
//    	long newUpperLeftX = (long) ((double)this.upperLeftX / oldRenderSize * newRenderSize);
    	//long newUpperLeftY = (long) ((double)this.upperLeftY / oldRenderSize * newRenderSize);
    	//long newUpperLeftX = (long) ((double)this.upperLeftX / oldRenderSize * newRenderSize);
    	//newUpperLeftX -=  (double)mouseX/this.width/oldRenderSize * newRenderSize;
//    	long newUpperLeftY = (long) ((double)(this.upperLeftY - mouseY) / oldRenderSize * newRenderSize);
//    	IRLW.logger.info(newUpperLeftX);
//    	IRLW.logger.info(newUpperLeftY);
    	//if(this.setPosition(newUpperLeftX, newUpperLeftY)){   
    	TerramapMod.cacheManager.clearQueue(); // We are displaying new tiles, we don't need what we needed earlier
    		this.zoomLevel = nzoom;
    	//}
    	this.setTiledMapZoom();
    	
    	//FIXME
    }
    

    public void setZoomToMinimum() {
    	
    	this.focusLongitude = 0;
    	this.focusLatitude = 0;
    	
    	this.setZoom((float) (Math.log(this.height * this.GUI_SIZING / WebMercatorUtils.TILE_DIMENSIONS) / Math.log(2)));
    }
    
    private void setTiledMapZoom() {
    	this.map.setZoomLevel((int)this.zoomLevel);
    }
    
    private double getSizeFactor(double mapZoom, int tileZoom) {
    	//TODO WIP, Math.pow and double are not precise enough
    	return Minecraft.getMinecraft().gameSettings.guiScale;
    	//return 1f/this.GUI_SIZING;
    	//return (Math.pow(2, mapZoom - tileZoom) / this.GUI_SIZING);
    	//return (Math.pow(2, mapZoom));
    }
    
    private double getSizeFactor(int tileZoom) {
    	return this.getSizeFactor(this.zoomLevel, tileZoom);     
    }
    
    private long getMaxMapSize(int zoom) {
    	//TODO May overflow ?
    	return (long) (WebMercatorUtils.getDimensionsInTile(zoom) * this.getSizeFactor(zoom) * WebMercatorUtils.TILE_DIMENSIONS); //TODO Adapt according to Minecraft's GUI sizing setting
    }
    
    /* === Getters and Setters from this point === */

    public void setSize(int width, int height) {
    	this.width = width;
    	this.height = height;
    }
    
    public boolean setPosition(double lon, double lat) {
    	
    	return this.setLongitude(lon) && this.setLatitude(lat);
    	
    	//TODO remake with new system
    	/*
		if(y >= 0 && y + this.height < this.getMaxMapSize(this.map.getZoomLevel())) {
			this.upperLeftX = x % this.map.getSizeInPixels();
			this.upperLeftY = y;
			return true;
		}
		return false;
		*/
    	
    }
    
    public boolean setLongitude(double lon) {
    	this.focusLongitude = lon;
    	return true;
    	//TODO Check if the new position is legal
    }
	
    public boolean setLatitude(double lat) {
    	this.focusLatitude = lat;
    	return true;
    	//TODO Check if the new position is legal
    }
    
    private void setZoom(float zoom) {
    	this.zoomLevel = zoom;
    	this.setTiledMapZoom();
    }

}
