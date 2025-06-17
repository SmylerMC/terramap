package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.terramap.gui.sprites.TerramapSprites;

import static net.smyler.terramap.gui.sprites.TerramapSprites.*;

/**
 * Map marker for any entity that implements IAnimals but not IMobs
 * The corresponding controller is CreatureMarkerController
 * 
 * @author SmylerMC
 *
 */
public class AnimalMarker extends AbstractLivingMarker {

    public AnimalMarker(MarkerController<?> controller, Entity entity) {
        super(controller, spriteFor(entity), entity);
    }

    private static Sprite spriteFor(Entity entity) {
        if (entity instanceof EntitySkeletonHorse) {
            return MARKER_SKELETON_HORSE;
        } else if (entity instanceof EntityZombieHorse) {
            return MARKER_ZOMBIE_HORSE;
        } else if (entity instanceof EntityDonkey) {
            return MARKER_DONKEY;
        } else if (entity instanceof EntityMule) {
            return MARKER_MULE;
        } else if (entity instanceof EntityLlama) {
            return MARKER_LLAMA;
        } else if (entity instanceof EntityHorse) {
            return MARKER_HORSE;
        } else if (entity instanceof EntityVillager) {
            return MARKER_VILLAGER;
        } else if (entity instanceof EntityIronGolem) {
            return MARKER_IRON_GOLEM;
        } else if (entity instanceof EntitySnowman) {
            return new Sprite(SNOW_MAN_TEXTURE, 64d, 64d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityBat) {
            return new Sprite(BAT_TEXTURE, 64d, 64d, 6d, 6d, 12d, 12d);
        } else if (entity instanceof EntityPolarBear) {
            return new Sprite(POLAR_BEAR_TEXTURE, 128d, 64d, 7d, 7d, 14d, 14d);
        } else if (entity instanceof EntityChicken) {
            return MARKER_CHICKEN;
        } else if (entity instanceof EntityMooshroom) {
            return new Sprite(MOOSHROOM_TEXTURE, 64d, 32d, 6d, 6d, 14d, 14d);
        } else if (entity instanceof EntityCow) {
            return new Sprite(COW_TEXTURE, 64d, 32d, 6d, 6d, 14d, 14d);
        } else if (entity instanceof EntityPig) {
            return MARKER_PIG;
        } else if (entity instanceof EntityRabbit) {
            return MARKER_RABBIT;
        } else if (entity instanceof EntitySheep) {
            return new Sprite(SHEEP_TEXTURE, 64d, 32d, 6d, 6d, 14d, 14d);
        } else if (entity instanceof EntitySquid) {
            return new Sprite(SQUID_TEXTURE, 64d, 32d, 12d, 12d, 24d, 26d);
        } else if (entity instanceof EntityOcelot) {
            boolean isTamed = ((EntityOcelot) entity).isTamed();
            if (isTamed) {
                return MARKER_CAT;
            } else {
                return MARKER_OCELOT;
            }
        } else if (entity instanceof EntityParrot) {
            return MARKER_PARROT;
        } else if (entity instanceof EntityWolf) {
            return MARKER_WOLF;
        }
        return TerramapSprites.MARKER_TOKEN_GREEN;
    }

    private static final Identifier SNOW_MAN_TEXTURE = new Identifier("minecraft", "textures/entity/snowman.png");
    private static final Identifier BAT_TEXTURE = new Identifier("minecraft", "textures/entity/bat.png");
    private static final Identifier POLAR_BEAR_TEXTURE = new Identifier("minecraft", "textures/entity/bear/polarbear.png");
    private static final Identifier COW_TEXTURE = new Identifier("minecraft", "textures/entity/cow/cow.png");
    private static final Identifier MOOSHROOM_TEXTURE = new Identifier("minecraft", "textures/entity/cow/mooshroom.png");
    private static final Identifier SHEEP_TEXTURE = new Identifier("minecraft", "textures/entity/sheep/sheep.png");
    private static final Identifier SQUID_TEXTURE = new Identifier("minecraft", "textures/entity/squid.png");

}
