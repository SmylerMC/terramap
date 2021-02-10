package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import org.lwjgl.opengl.GL11;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AbstractPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarkers;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractPlayerMarker extends AbstractMovingMarkers {

	private int downScaleFactor;

	public AbstractPlayerMarker(MarkerController<?> controller, TerramapPlayer player, int downscaleFactor) {
		super(controller, 16 / downscaleFactor, 16 / downscaleFactor);
		this.downScaleFactor = downscaleFactor;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		boolean drawName = this.showName(hovered);
		int textureSize = 128 / this.downScaleFactor;
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x +1, y +1, x + this.getWidth() + 1, y + this.getHeight() + 1, 0x50000000);

		// Draw the direction arrow
		if(this.showDirection(hovered) && Float.isFinite(this.azimuth)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + this.width / 2, y + this.height / 2, 0);
			GlStateManager.rotate(this.azimuth, 0, 0, 1);
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.shadeModel(7425);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buff = tess.getBuffer();
			buff.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_COLOR);
			buff.pos(0, -this.height*1.2, 0).color(1f, 0, 0, 0.7f).endVertex();
			buff.pos(-this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).endVertex();
			buff.pos(0, -this.height * 0.8, 0).color(0.5f, 0, 0, 1f).endVertex();
			buff.pos(this.width/2, -this.height * 0.7, 0).color(0.8f, 0, 0, 0.9f).endVertex();
			tess.draw();
			GlStateManager.shadeModel(7424);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getSkin());
		GlStateManager.color(1, 1, 1, this.getTransparency());
		Gui.drawModalRectWithCustomSizedTexture(x, y, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 80 / this.downScaleFactor, this.getHeight(), this.getWidth(), this.getHeight(), textureSize, textureSize);

		if(drawName) {
			int halfSize = this.width / 2;
			String name = this.getDisplayName().getFormattedText();
			int strWidth = parent.getFont().getStringWidth(name);
			int nameY = y - parent.getFont().FONT_HEIGHT - 2;
			Gui.drawRect(x + halfSize - strWidth / 2 - 2, y - parent.getFont().FONT_HEIGHT - 4, x + strWidth / 2 + halfSize + 2, y - 1, 0x50000000);
			parent.getFont().drawCenteredString(x + halfSize, nameY, name, 0xFFFFFFFF, true);
		}

		GlStateManager.color(1, 1, 1, 1);
	}

	protected abstract ResourceLocation getSkin();

	protected abstract float getTransparency();

	protected boolean showName(boolean hovered) {
		if(this.getController() instanceof AbstractPlayerMarkerController) {
			AbstractPlayerMarkerController<?> controller = (AbstractPlayerMarkerController<?>) this.getController();
			return controller.doesShowNames() || hovered;
		}
		return hovered;
	}

	protected boolean showDirection(boolean hovered) {
		if(this.getController() instanceof AbstractPlayerMarkerController) {
			AbstractPlayerMarkerController<?> controller = (AbstractPlayerMarkerController<?>) this.getController();
			return controller.doesShowDirection();
		}
		return true;
	}

	@Override
	public int getDeltaX() {
		return - this.getWidth() / 2;
	}

	@Override
	public int getDeltaY() {
		return - this.getHeight() / 2;
	}

	@Override
	public boolean canBeTracked() {
		return true;
	}

}
