package net.smyler.terramap.entity;

import net.minecraft.entity.Entity;
import net.smyler.terramap.world.PositionAbstract;


/**
 * A position backed by a Forge entity.
 *
 * @author Smyler
 */
public class EntityPosition extends PositionAbstract {

    private final Entity entity;

    public EntityPosition(Entity entity) {
        this.entity = entity;
    }

    @Override
    public double x() {
        return this.entity.posX;
    }

    @Override
    public double y() {
        return this.entity.posY;
    }

    @Override
    public double z() {
        return this.entity.posZ;
    }

    @Override
    public float yaw() {
        return this.entity.rotationYaw;
    }

    @Override
    public float pitch() {
        return this.entity.rotationPitch;
    }

}
