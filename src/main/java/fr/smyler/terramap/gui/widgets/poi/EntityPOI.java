package fr.smyler.terramap.gui.widgets.poi;

import io.github.terra121.projection.GeographicProjection;
import net.minecraft.entity.Entity;

public class EntityPOI extends PointOfInterest {

	protected Entity entity;
	
	public EntityPOI(Entity e) {
		this.entity = e;
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
		super.draw(x, y);
	}
}
