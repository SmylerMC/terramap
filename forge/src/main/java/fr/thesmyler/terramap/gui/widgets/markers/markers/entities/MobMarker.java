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
 * @author SmylerMC
 *
 */
public class MobMarker extends AbstractLivingMarker {

    public MobMarker(MarkerController<?> controller, Entity entity) {
        super(controller, spriteFor(entity), entity);
    }

    private static Sprite spriteFor(Entity entity) {
        if (entity instanceof EntityBlaze) {
            return Sprite.builder()
                    .texture(BLAZE_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityCreeper) {
            return Sprite.builder()
                    .texture(CREEPER_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityElderGuardian) {
            return MARKER_ELDER_GUARDIAN;
        } else if (entity instanceof EntityEnderman) {
            return MARKER_ENDERMAN;
        } else if (entity instanceof EntityEndermite) {
            return Sprite.builder()
                    .texture(ENDERMITE_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(2d).yTop(2d)
                    .xRight(6d).yBottom(5d)
                    .build();
        } else if (entity instanceof EntityEvoker) {
            return Sprite.builder()
                    .texture(EVOKER_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(18d)
                    .build();
        } else if (entity instanceof EntityGhast) {
            return Sprite.builder()
                    .texture(GHAST_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(16d).yTop(16d)
                    .xRight(32d).yBottom(32d)
                    .build();
        } else if (entity instanceof EntityGuardian) {
            return MARKER_GUARDIAN;
        } else if (entity instanceof EntityHusk) {
            return Sprite.builder()
                    .texture(HUSK_ZOMBIE_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityMagmaCube) {
            return MARKER_MAGMA_CUBE;
        } else if (entity instanceof EntityShulker) {
            return Sprite.builder()
                    .texture(SHULKER_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(6d).yTop(58d)
                    .xRight(12d).yBottom(64d)
                    .build();
        } else if (entity instanceof EntitySilverfish) {
            return Sprite.builder()
                    .texture(SILVERFISH_TEXTURE)
                    .textureDimensions(128d, 64)
                    .xLeft(4d).yTop(4d)
                    .xRight(10d).yBottom(8d)
                    .build();
        } else if (entity instanceof EntitySkeleton) {
            return Sprite.builder()
                    .texture(SKELETON_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntitySlime) {
            return MARKER_SLIME;
        } else if (entity instanceof EntityCaveSpider) {
            return Sprite.builder()
                    .texture(CAVE_SPIDER_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(40d).yTop(12d)
                    .xRight(48d).yBottom(52d)
                    .build();
        } else if (entity instanceof EntitySpider) {
            return Sprite.builder()
                    .texture(SPIDER_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(40d).yTop(12d)
                    .xRight(48d).yBottom(52d)
                    .build();
        } else if (entity instanceof EntityStray) {
            return Sprite.builder()
                    .texture(STRAY_SKELETON_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityVex) {
            return Sprite.builder()
                    .texture(VEX_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityVindicator) {
            return Sprite.builder()
                    .texture(VINDICATOR_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(18d)
                    .build();
        } else if (entity instanceof EntityWitch) {
            return MARKER_WITCH;
        } else if (entity instanceof EntityWitherSkeleton) {
            return Sprite.builder()
                    .texture(WITHER_SKELETON_TEXTURE)
                    .textureDimensions(64d, 32d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityZombieVillager) {
            return MARKER_ZOMBIE_VILLAGER;
        } else if (entity instanceof EntityPigZombie) {
            return MARKER_PIGLIN_ZOMBIFIED;
        } else if (entity instanceof EntityZombie) {
            return Sprite.builder()
                    .texture(ZOMBIE_TEXTURE)
                    .textureDimensions(64d, 64d)
                    .xLeft(8d).yTop(8d)
                    .xRight(16d).yBottom(16d)
                    .build();
        } else if (entity instanceof EntityDragon) {
            return MARKER_ENDER_DRAGON;
        } else if (entity instanceof EntityWither) {
            return MARKER_WITHER;
        }
        return MARKER_TOKEN_RED;
    }

    private static final Identifier BLAZE_TEXTURE = new Identifier("minecraft", "textures/entity/blaze.png");
    private static final Identifier CAVE_SPIDER_TEXTURE = new Identifier("minecraft", "textures/entity/spider/cave_spider.png");
    private static final Identifier CREEPER_TEXTURE = new Identifier("minecraft", "textures/entity/creeper/creeper.png");
    private static final Identifier ENDERMITE_TEXTURE = new Identifier("minecraft", "textures/entity/endermite.png");
    private static final Identifier EVOKER_TEXTURE = new Identifier("minecraft", "textures/entity/illager/evoker.png");
    private static final Identifier GHAST_TEXTURE = new Identifier("minecraft", "textures/entity/ghast/ghast.png");
    private static final Identifier HUSK_ZOMBIE_TEXTURE = new Identifier("minecraft", "textures/entity/zombie/husk.png");
    private static final Identifier SHULKER_TEXTURE = new Identifier("minecraft", "textures/entity/shulker/shulker_purple.png");
    private static final Identifier SILVERFISH_TEXTURE = new Identifier("minecraft", "textures/entity/silverfish.png");
    private static final Identifier SKELETON_TEXTURE = new Identifier("minecraft", "textures/entity/skeleton/skeleton.png");
    private static final Identifier SPIDER_TEXTURE = new Identifier("minecraft", "textures/entity/spider/spider.png");
    private static final Identifier STRAY_SKELETON_TEXTURE = new Identifier("minecraft", "textures/entity/skeleton/stray.png");
    private static final Identifier VEX_TEXTURE = new Identifier("minecraft", "textures/entity/illager/vex.png");
    private static final Identifier VINDICATOR_TEXTURE = new Identifier("minecraft", "textures/entity/illager/vindicator.png");
    private static final Identifier WITHER_SKELETON_TEXTURE = new Identifier("minecraft", "textures/entity/skeleton/wither_skeleton.png");
    private static final Identifier ZOMBIE_TEXTURE = new Identifier("minecraft", "textures/entity/zombie/zombie.png");

}
