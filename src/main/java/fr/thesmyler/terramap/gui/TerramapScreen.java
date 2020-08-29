package fr.thesmyler.terramap.gui;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.Scrollbar;
import fr.thesmyler.smylibgui.widgets.SlidingPannelWidget;
import fr.thesmyler.smylibgui.widgets.SlidingPannelWidget.PannelTarget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.TiledMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class TerramapScreen extends Screen {

	private static final double LIMIT_LATITUDE = Math.toDegrees(2 * Math.atan(Math.pow(Math.E, Math.PI)) - Math.PI/2);

	private GuiScreen parent;
	private TiledMap<?>[] backgrounds;

	// Widgets
	private MapWidget map; 
	private TexturedButtonWidget closeButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CROSS);
	private TexturedButtonWidget zoomInButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PLUS);
	private TexturedButtonWidget zoomOutButton = new TexturedButtonWidget(50, IncludedTexturedButtons.MINUS);
	private TexturedButtonWidget centerButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CENTER);
	private TexturedButtonWidget styleButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PAPER);
	private TextWidget zoomText;
	private SlidingPannelWidget infoPannel = new SlidingPannelWidget(70, 200);
	private TexturedButtonWidget pannelButton = new TexturedButtonWidget(200, 5, 10, IncludedTexturedButtons.RIGHT, this::toggleInfoPannel);
	private TextWidget mouseGeoLocationText;
	private TextWidget mouseMCLocationText;
	private TextWidget distortionText;
	private TextWidget playerGeoLocationText;
	private TextFieldWidget searchBox = new TextFieldWidget(10, new FontRendererContainer(Minecraft.getMinecraft().fontRenderer));
	private SlidingPannelWidget stylePannel = new SlidingPannelWidget(80, 200); 
	private Scrollbar styleScrollbar = new Scrollbar(100);


	public TerramapScreen(GuiScreen parent, TiledMap<?>[] maps) {
		this.parent = parent;
		this.backgrounds = maps;
		this.map = new MapWidget(10, this.backgrounds[0]);
	}

	@Override
	public void initScreen() {

		this.removeAllWidgets();
		this.map.setX(0).setY(0).setWidth(this.getWidth()).setHeight(this.getHeight());
		this.addWidget(this.map);

		// Map control buttons
		this.closeButton.setX(this.width - this.closeButton.getWidth() - 5).setY(5);
		this.closeButton.setOnClick(() -> {
			Minecraft.getMinecraft().displayGuiScreen(this.parent);
		});
		this.closeButton.setTooltip("Close map"); //TODO Localize
		this.closeButton.enable();
		this.addWidget(this.closeButton);
		this.zoomInButton.setX(this.closeButton.getX()).setY(this.closeButton.getY() + closeButton.getHeight() + 15);
		this.zoomInButton.setOnClick(() -> this.map.zoom(1));
		this.zoomInButton.setTooltip("Zoom in"); //TODO Localize
		this.zoomInButton.enable();
		this.addWidget(this.zoomInButton);
		this.zoomText = new TextWidget(49, this.getFont());
		this.zoomText.setAnchorX(this.zoomInButton.getX() + this.zoomInButton.getWidth() / 2 + 1).setAnchorY(this.zoomInButton.getY() +  this.zoomInButton.getHeight() + 2);
		this.zoomText.setAlignment(TextAlignment.CENTER).setBackgroundColor(0xA0000000).setPadding(3);
		this.addWidget(this.zoomText);
		this.zoomOutButton.setX(this.zoomInButton.getX()).setY(this.zoomText.getY() + zoomText.getHeight() + 2);
		this.zoomOutButton.setOnClick(() -> this.map.zoom(-1));
		this.zoomOutButton.setTooltip("Zoom out"); //TODO Localize
		this.zoomOutButton.enable();
		this.addWidget(this.zoomOutButton);
		this.centerButton.setX(this.zoomOutButton.getX()).setY(this.zoomOutButton.getY() + this.zoomOutButton.getHeight() + 15);
		this.centerButton.setTooltip("Center on player"); //TODO Localize
		this.addWidget(this.centerButton);
		this.styleButton.setX(this.centerButton.getX()).setY(this.centerButton.getY() + this.centerButton.getHeight() + 5);
		this.styleButton.setOnClick(() -> this.stylePannel.show());
		this.styleButton.setTooltip("Change map style"); //TODO Localize
		this.styleButton.enable();
		this.addWidget(this.styleButton);

		// Info pannel
		this.infoPannel.removeAllWidgets();
		this.infoPannel.setWidth(220).setHeight(this.getHeight());
		this.infoPannel.setShowX(0).setShowY(0).setHiddenX(-infoPannel.getWidth() + 25).setHiddenY(0);
		this.pannelButton.setTooltip("Collapse information and search pannel"); //TODO Localize
		this.infoPannel.addWidget(pannelButton);
		this.mouseGeoLocationText = new TextWidget(49, this.getFont());
		this.mouseGeoLocationText.setAnchorX(5).setAnchorY(5).setAlignment(TextAlignment.RIGHT);
		this.infoPannel.addWidget(this.mouseGeoLocationText);
		this.mouseMCLocationText = new TextWidget(49, this.getFont());
		this.mouseMCLocationText.setAnchorX(5).setAnchorY(this.mouseGeoLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPannel.addWidget(this.mouseMCLocationText);
		this.playerGeoLocationText = new TextWidget(49, this.getFont());
		this.playerGeoLocationText = new TextWidget(49, this.getFont());
		this.playerGeoLocationText.setAnchorX(5).setAnchorY(this.mouseMCLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPannel.addWidget(this.playerGeoLocationText);
		this.distortionText = new TextWidget(49, this.getFont());
		this.distortionText.setAnchorX(5).setAnchorY(this.playerGeoLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPannel.addWidget(this.distortionText);
		this.searchBox.setX(5).setY(this.distortionText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setWidth(167);
		this.searchBox.enableRightClickMenu();
		this.searchBox.setText("Work in progress").disable();
		this.searchBox.setOnPressEnterCallback(this::search);
		this.infoPannel.addWidget(this.searchBox);
		TexturedButtonWidget searchButton = new TexturedButtonWidget(50, IncludedTexturedButtons.SEARCH);
		searchButton.setX(this.searchBox.getX() + this.searchBox.getWidth() + 2).setY(this.searchBox.getY() - 1);
		searchButton.setOnClick(() -> this.search(this.searchBox.getText()));
//		searchButton.enable();
		this.infoPannel.addWidget(searchButton);
		this.infoPannel.setHeight(this.searchBox.getY() + this.searchBox.getHeight() + 5);
		this.addWidget(this.infoPannel);

		// Style pannel
		this.stylePannel.setWidth(200).setHeight(this.getHeight());
		this.stylePannel.setHiddenX(this.getWidth()).setHiddenY(0).setShowX(this.getWidth() - this.stylePannel.getWidth()).setShowY(0);
		this.stylePannel.setCloseOnClickOther(false);
		this.stylePannel.removeAllWidgets();
		this.styleScrollbar.setX(this.stylePannel.width - 15).setY(0).setHeight(this.getHeight());
		this.stylePannel.addWidget(this.styleScrollbar);
		StyleScreen s = new StyleScreen();
		this.styleScrollbar.setViewPort((double) this.height / (s.getHeight() - 10));
		if(this.styleScrollbar.getViewPort() >= 1) this.styleScrollbar.setProgress(0);
		this.stylePannel.addWidget(s);
		this.addWidget(this.stylePannel);
	}

	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		this.zoomInButton.setEnabled(this.map.getZoom() < this.map.getMaxZoom());
		this.zoomOutButton.setEnabled(this.map.getZoom() > this.map.getMinZoom());
		this.zoomText.setText("" + Math.round(this.map.getZoom()));

		double mouseLat = this.map.getMouseLatitude();
		double mouseLon = this.map.getMouseLongitude();
		String displayLat = GeoServices.formatGeoCoordForDisplay(mouseLat);
		String displayLon = GeoServices.formatGeoCoordForDisplay(mouseLon);
		if(mouseLat > LIMIT_LATITUDE || mouseLat < -LIMIT_LATITUDE) {
			displayLat = "-";
			displayLon = "-";
		}
		String formatX = "-";
		String formatZ = "-";
		String playerFormatLon = "-";
		String playerFormatLat = "-";

		//TODO Localize
		this.mouseGeoLocationText.setText("Mouse location: " + displayLat + "° " + displayLon + "°");
		this.mouseMCLocationText.setText("X:" + formatX + " Z:" + formatZ);
		this.playerGeoLocationText.setText("Player position: " + playerFormatLat + "° " + playerFormatLon + "°");
		this.distortionText.setText("Distortion: -");
	}

	private void toggleInfoPannel() {
		int x = this.pannelButton.getX();
		int y = this.pannelButton.getY();
		int z = this.pannelButton.getZ();
		TexturedButtonWidget newButton;
		if(this.infoPannel.getTarget().equals(PannelTarget.OPENNED)) {
			this.infoPannel.hide();
			newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.RIGHT, this::toggleInfoPannel);
		} else {
			this.infoPannel.show();
			newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.LEFT, this::toggleInfoPannel);
		}
		newButton.setTooltip(this.pannelButton.getTooltipText());
		this.infoPannel.removeWidget(this.pannelButton);
		this.pannelButton = newButton;
		this.infoPannel.addWidget(this.pannelButton);
	}

	private boolean search(String text) {
		TerramapMod.logger.info("Geo search: " + text);
		//TODO Search
		return true; // Let the search box loose focus
	}

	private class StyleScreen extends Screen {

		int mapsSize = 175;

		StyleScreen() {
			super(0, 0, 0, 0, 0, BackgroundType.NONE);
			MapWidget lw = null;
			for(TiledMap<?> map: TerramapScreen.this.backgrounds) {
				MapWidget w = new MapPreview(50, map);
				w.setWidth(mapsSize).setHeight(mapsSize);
				if(lw == null) {
					w.setX(0).setY(0);
				} else {
					w.setX(0).setY(lw.getY() + lw.getHeight() + 5);
				}
				this.addWidget(w);
				lw = w;
			}
			this.height = lw.getY() + lw.getHeight() + 10;
			this.width = this.mapsSize;
		}

		@Override
		public void onUpdate(Screen parent) {
			for(IWidget w: this.widgets) {
				if(w instanceof MapPreview) {
					MapPreview map = (MapPreview)w;
					map.setZoom(TerramapScreen.this.map.getZoom());
					map.setCenterPosition(TerramapScreen.this.map.getCenterPosition());
				}
			}
			super.onUpdate(parent);
		}



		@Override
		public boolean onMouseWheeled(int mouseX, int mouseY, int amount, Screen parent) {
			if(TerramapScreen.this.styleScrollbar.getViewPort() < 1) {
				if(amount > 0) TerramapScreen.this.styleScrollbar.scrollUp();
				else TerramapScreen.this.styleScrollbar.scrollDown();
			}
			return super.onMouseWheeled(mouseX, mouseY, amount, parent);
		}

		@Override
		public int getX() {
			return 5;
		}

		@Override
		public int getY() {
			return 5 - (int) Math.round((this.height - TerramapScreen.this.getHeight()) * TerramapScreen.this.styleScrollbar.getProgress());
		}

	}

	private class MapPreview extends MapWidget {

		public MapPreview(int z, TiledMap<?> map) {
			super(z, map);
			this.setInteractive(false);
			this.setRightClickMenuEnabled(false);
			this.setCopyrightVisibility(false);
			this.setScaleVisibility(false);
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
			int color = 0xFF808080;
			int textColor = hovered? 0xFFC0C0FF: 0xFFFFFFFF;
			String text = this.background.getMap().getName();
			GuiScreen.drawRect(x, y, x + this.width, y + 4, color);
			GuiScreen.drawRect(x, y + this.height - parent.getFont().FONT_HEIGHT - 4, x + this.width, y + this.height, color);
			GuiScreen.drawRect(x, y, x + 4, y + this.height, color);
			GuiScreen.drawRect(x + this.width - 4, y, x + this.width, y + this.height, color);
			parent.getFont().drawCenteredString(x + this.width/2, y + this.height - parent.getFont().FONT_HEIGHT - 2, text, textColor, true);

		}

		@Override
		public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
			if(mouseButton == 0) {
				TerramapScreen.this.map.setBackground(this.background.getMap());
				TerramapScreen.this.stylePannel.hide();
			}
			return false;
		}

		@Override
		public String getTooltipText() {
			return this.background.getMap().getName();
		}

		@Override
		public long getTooltipDelay() {
			return 0;
		}

	}

}
