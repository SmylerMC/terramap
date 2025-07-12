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
            return SNOW_MAN;
        } else if (entity instanceof EntityBat) {
            return BAT;
        } else if (entity instanceof EntityPolarBear) {
            return POLAR_BEAR;
        } else if (entity instanceof EntityChicken) {
            return MARKER_CHICKEN;
        } else if (entity instanceof EntityMooshroom) {
            return MOOSHROOM;
        } else if (entity instanceof EntityCow) {
            return COW;
        } else if (entity instanceof EntityPig) {
            return MARKER_PIG;
        } else if (entity instanceof EntityRabbit) {
            return MARKER_RABBIT;
        } else if (entity instanceof EntitySheep) {
            return SHEEP;
        } else if (entity instanceof EntitySquid) {
            return SQUID;
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

    private static final Identifier VANILLA_TEXTURE_ENTITY = new Identifier("minecraft", "textures/entity");

    private static final Sprite SNOW_MAN = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("snowman.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .width(8d).height(8d)
            .build();

    private static final Sprite BAT = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("bat.png"))
            .textureDimensions(64d, 64d)
            .xLeft(6d).yTop(6d)
            .width(6d).height(6d)
            .build();

    private static final Sprite POLAR_BEAR = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("bear/polarbear.png"))
            .textureDimensions(128d, 64d)
            .xLeft(7d).yTop(7d)
            .width(7d).height(7d)
            .build();

    private static final Sprite COW = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("cow/cow.png"))
            .textureDimensions(64d, 32d)
            .xLeft(6d).yTop(6d)
            .width(8d).height(8d)
            .build();

    private static final Sprite MOOSHROOM = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("cow/mooshroom.png"))
            .textureDimensions(64d, 32d)
            .xLeft(6d).yTop(6d)
            .width(8d).height(8d)
            .build();

    private static final Sprite SHEEP = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("sheep/sheep.png"))
            .textureDimensions(64d, 32d)
            .xLeft(6d).yTop(6d)
            .width(8d).height(8d)
            .build();

    private static final Sprite SQUID = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("squid.png"))
            .textureDimensions(64d, 32d)
            .xLeft(12d).yTop(12d)
            .width(12d).height(14d)
            .build();

}
