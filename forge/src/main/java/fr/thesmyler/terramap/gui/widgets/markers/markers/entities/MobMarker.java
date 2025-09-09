package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;

import static net.smyler.terramap.gui.sprites.TerramapSprites.*;

/**
 * Map marker for any entity that implements IMob
 * The corresponding controller is MobMarkerController
 * 
 * @author Smyler
 *
 */
public class MobMarker extends AbstractLivingMarker {

    public MobMarker(MarkerController<?> controller, Entity entity) {
        super(controller, spriteFor(entity), entity);
    }

    private static Sprite spriteFor(Entity entity) {
        if (entity instanceof EntityBlaze) {
            return BLAZE;
        } else if (entity instanceof EntityCreeper) {
            return CREEPER;
        } else if (entity instanceof EntityElderGuardian) {
            return MARKER_ELDER_GUARDIAN;
        } else if (entity instanceof EntityEnderman) {
            return MARKER_ENDERMAN;
        } else if (entity instanceof EntityEndermite) {
            return ENDERMITE;
        } else if (entity instanceof EntityEvoker) {
            return EVOKER;
        } else if (entity instanceof EntityGhast) {
            return GHAST;
        } else if (entity instanceof EntityGuardian) {
            return MARKER_GUARDIAN;
        } else if (entity instanceof EntityHusk) {
            return HUSK_ZOMBIE;
        } else if (entity instanceof EntityMagmaCube) {
            return MARKER_MAGMA_CUBE;
        } else if (entity instanceof EntityShulker) {
            return SHULKER;
        } else if (entity instanceof EntitySilverfish) {
            return SILVERFISH;
        } else if (entity instanceof EntitySkeleton) {
            return SKELETON;
        } else if (entity instanceof EntitySlime) {
            return MARKER_SLIME;
        } else if (entity instanceof EntityCaveSpider) {
            return CAVE_SPIDER;
        } else if (entity instanceof EntitySpider) {
            return SPIDER;
        } else if (entity instanceof EntityStray) {
            return STRAY_SKELETON;
        } else if (entity instanceof EntityVex) {
            return VEX;
        } else if (entity instanceof EntityVindicator) {
            return VINDICATOR;
        } else if (entity instanceof EntityWitch) {
            return MARKER_WITCH;
        } else if (entity instanceof EntityWitherSkeleton) {
            return WITHER_SKELETON;
        } else if (entity instanceof EntityZombieVillager) {
            return MARKER_ZOMBIE_VILLAGER;
        } else if (entity instanceof EntityPigZombie) {
            return MARKER_PIGLIN_ZOMBIFIED;
        } else if (entity instanceof EntityZombie) {
            return ZOMBIE;
        } else if (entity instanceof EntityDragon) {
            return MARKER_ENDER_DRAGON;
        } else if (entity instanceof EntityWither) {
            return MARKER_WITHER;
        }
        return MARKER_TOKEN_RED;
    }

    private static final Identifier VANILLA_TEXTURE_ENTITY = new Identifier("minecraft", "textures/entity");

    private static final Sprite BLAZE = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("blaze.png"))
            .textureDimensions(64d, 32d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite CAVE_SPIDER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("spider/cave_spider.png"))
            .textureDimensions(64d, 32d)
            .xLeft(40d).yTop(12d)
            .xRight(48d).yBottom(52d)
            .build();

    private static final Sprite CREEPER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("creeper/creeper.png"))
            .textureDimensions(64d, 32d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite ENDERMITE = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("endermite.png"))
            .textureDimensions(64d, 32d)
            .xLeft(2d).yTop(2d)
            .xRight(6d).yBottom(5d)
            .build();

    private static final Sprite EVOKER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/evoker.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(18d)
            .build();

    private static final Sprite GHAST = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("ghast/ghast.png"))
            .textureDimensions(64d, 32d)
            .xLeft(16d).yTop(16d)
            .xRight(32d).yBottom(32d)
            .build();

    private static final Sprite HUSK_ZOMBIE = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("zombie/husk.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite SHULKER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("shulker/shulker_purple.png"))
            .textureDimensions(64d, 64d)
            .xLeft(6d).yTop(58d)
            .xRight(12d).yBottom(64d)
            .build();

    private static final Sprite SILVERFISH = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("silverfish.png"))
            .textureDimensions(128d, 64)
            .xLeft(4d).yTop(4d)
            .xRight(10d).yBottom(8d)
            .build();

    private static final Sprite SKELETON = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("skeleton/skeleton.png"))
            .textureDimensions(64d, 32d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite SPIDER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("spider/spider.png"))
            .textureDimensions(64d, 32d)
            .xLeft(40d).yTop(12d)
            .xRight(48d).yBottom(52d)
            .build();

    private static final Sprite STRAY_SKELETON = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("skeleton/stray.png"))
            .textureDimensions(64d, 32d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite VEX = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/vex.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite VINDICATOR = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/vindicator.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(18d)
            .build();

    private static final Sprite WITHER_SKELETON = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("skeleton/wither_skeleton.png"))
            .textureDimensions(64d, 32d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

    private static final Sprite ZOMBIE = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("zombie/zombie.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(16d)
            .build();

}
