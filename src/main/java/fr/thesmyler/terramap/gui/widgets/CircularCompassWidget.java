package fr.thesmyler.terramap.gui.widgets;

import org.lwjgl.opengl.GL11;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Animation;
import fr.thesmyler.smylibgui.util.Animation.AnimationState;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.util.Mat2d;
import fr.thesmyler.terramap.util.Vec2d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class CircularCompassWidget implements IWidget {
	
	private float azimuth = 0f;
	private float x, y, size;
	private final int z;
	private Color backgroundColor = Color.DARKER_OVERLAY;
	private Color northColor = Color.RED;
	private Color northDarkColor = Color.RED.withRed(0.4f);
	private Color southColor = Color.WHITE;
	private Color southColorDark = new Color(0.4f, 0.4f, 0.4f);
	private Runnable onClick;
	private boolean visible = true;
	private boolean fadeAwayOnZero = false;
	private String tooltip = null;
	
	private Animation fader = new Animation(1000);

	public CircularCompassWidget(float x, float y, int z, float size) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
		
		Color background = this.backgroundColor;
    	Color north = this.northColor;
    	Color northDark = this.northDarkColor;
    	Color south = this.southColor;
    	Color southColor = this.southColorDark;
    	
    	if(this.fadeAwayOnZero) {
    		background = this.fader.fadeColor(background);
    		north = this.fader.fadeColor(north);
    		northDark = this.fader.fadeColor(northDark);
    		south = this.fader.fadeColor(south);
    		southColor = this.fader.fadeColor(southColor);
    	}
    	
		int vertexCount = (int) (2*Math.PI*this.size);
		float radius = this.size / 2;
		double[] vertices = new double[vertexCount*2];
		Vec2d pos = new Vec2d(0, -radius);
		Mat2d rot = Mat2d.forRotation(-Math.PI*2 / vertexCount);
		for(int i = 0; i < vertexCount; i++) {
			vertices[2*i] = pos.x;
			vertices[2*i + 1] = pos.y;
			pos = rot.prod(pos);
		}
    	
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + radius, y + radius, 0);
		RenderUtil.drawPolygon(background, vertices);
		//RenderUtil.drawClosedStrokeLine(Color.BLACK, 1f, vertices);
    	GlStateManager.rotate(this.azimuth, 0, 0, 1);
    	Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
    	GlStateManager.color(1f, 1f, 1f, 1f);
    	GlStateManager.shadeModel(7425);
		GlStateManager.enableAlpha();
    	GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    	builder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
    	builder.pos(0d, -radius, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).endVertex();
    	builder.pos(-radius/3, radius * 0.1, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).endVertex();
    	builder.pos(0d, 0d, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).endVertex();
    	builder.pos(radius/3, radius * 0.1, 0d).color(northDark.redf(), northDark.greenf(), northDark.bluef(), northDark.alphaf()).endVertex();
    	tessellator.draw();
    	builder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
    	builder.pos(0, 0, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).endVertex();
    	builder.pos(-radius/3, radius * 0.1, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).endVertex();
    	builder.pos(0, radius, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).endVertex();
    	builder.pos(radius/3, radius * 0.1, 0).color(southColor.redf(), southColor.greenf(), southColor.bluef(), southColor.alphaf()).endVertex();
    	tessellator.draw();
    	GlStateManager.shadeModel(7424);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}
	
	@Override
	public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
		if(this.fadeAwayOnZero) {
        	if(this.fader.getState() == AnimationState.STOPPED) {
        		if(this.fader.getProgress() <= 0f && this.azimuth != 0f)
        			this.fader.start(AnimationState.ENTER);
        		if(this.fader.getProgress() >= 1f && this.azimuth == 0f) 
        			this.fader.start(AnimationState.LEAVE);
        	}
			this.fader.update();
		}
	}

	@Override
	public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
		float dX = mouseX - this.size / 2;
		float dY = mouseY - this.size / 2;
		if(dX*dX + dY*dY < this.size*this.size / 4) {
			if(this.onClick != null) this.onClick.run();
			return false;
		}
		return true;
	}

	@Override
	public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public float getWidth() {
		return this.size;
	}

	@Override
	public float getHeight() {
		return this.size;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Runnable getOnClick() {
		return onClick;
	}

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}
	
	@Override
	public boolean isVisible(WidgetContainer parent) {
		if(this.fadeAwayOnZero && this.fader.getProgress() <= 0f) return false;
		return this.visible;
	}
	
	public void setVisible(boolean yesNo) {
		this.visible = yesNo;
	}

	public Color getNorthColor() {
		return northColor;
	}

	public void setNorthColor(Color northColor) {
		this.northColor = northColor;
	}

	public Color getNorthDarkColor() {
		return northDarkColor;
	}

	public void setNorthDarkColor(Color northDarkColor) {
		this.northDarkColor = northDarkColor;
	}

	public Color getSouthColor() {
		return southColor;
	}

	public void setSouthColor(Color southColor) {
		this.southColor = southColor;
	}

	public Color getSouthColorDark() {
		return southColorDark;
	}

	public void setSouthColorDark(Color southColorDark) {
		this.southColorDark = southColorDark;
	}

	public boolean isFadeAwayOnZero() {
		return fadeAwayOnZero;
	}

	public void setFadeAwayOnZero(boolean fadeAwayOnZero) {
		this.fadeAwayOnZero = fadeAwayOnZero;
	}

	@Override
	public String getTooltipText() {
		return this.tooltip;
	}

	@Override
	public long getTooltipDelay() {
		return IWidget.super.getTooltipDelay();
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

}
