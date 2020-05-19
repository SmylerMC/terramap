package fr.smyler.terramap.gui.widgets.poi;

import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityPOI extends PointOfInterest {

	protected Entity entity;

	public EntityPOI(Entity e) {
		this.entity = e;
		
		//Special case for players as we need the skin
		if(this.entity instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer)this.entity;
			this.texture = player.getLocationSkin();
		}
	}

	public void updatePosition(GeographicProjection projection) {
		double x = this.entity.posX;
		double z = this.entity.posZ;
		double coords[] = projection.toGeo(x, z);
		this.longitude = coords[0];
		this.latitude = coords[1];
	}

	@Override
	public void draw(int x, int y) {
		
		//This is a special case for players as we need to draw both the skull and the hat
		if(this.entity instanceof EntityPlayer) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
			GlStateManager.color(255, 255, 255, 255);
			Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 16, 16, 16, 16, 128, 128);
			Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 80, 16, 16, 16, 128, 128);

		} else {
			super.draw(x, y);
		}
	}
}
