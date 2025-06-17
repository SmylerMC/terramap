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
            return new Sprite(BLAZE_TEXTURE, 64d, 32d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityCreeper) {
            return new Sprite(CREEPER_TEXTURE, 64d, 32d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityElderGuardian) {
            return MARKER_ELDER_GUARDIAN;
        } else if (entity instanceof EntityEnderman) {
            return MARKER_ENDERMAN;
        } else if (entity instanceof EntityEndermite) {
            return new Sprite(ENDERMITE_TEXTURE, 64d, 32d, 2d, 2d, 6d, 5d);
        } else if (entity instanceof EntityEvoker) {
            return new Sprite(EVOKER_TEXTURE, 64d, 64d, 8d, 8d, 16d, 18d);
        } else if (entity instanceof EntityGhast) {
            return new Sprite(GHAST_TEXTURE, 64d, 32d, 16d, 16d, 32d, 32d);
        } else if (entity instanceof EntityGuardian) {
            return MARKER_GUARDIAN;
        } else if (entity instanceof EntityHusk) {
            return new Sprite(HUSK_ZOMBIE_TEXTURE, 64d, 64d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityMagmaCube) {
            return MARKER_MAGMA_CUBE;
        } else if (entity instanceof EntityShulker) {
            return new Sprite(SHULKER_TEXTURE, 64d, 64d, 6d, 58d, 12d, 64d);
        } else if (entity instanceof EntitySilverfish) {
            return new Sprite(SILVERFISH_TEXTURE, 128d, 64, 4d, 4d, 10d, 8d);
        } else if (entity instanceof EntitySkeleton) {
            return new Sprite(SKELETON_TEXTURE, 64d, 32d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntitySlime) {
            return MARKER_SLIME;
        } else if (entity instanceof EntityCaveSpider) {
            return new Sprite(CAVE_SPIDER_TEXTURE, 64d, 32d, 40d, 12d, 48d, 52d);
        } else if (entity instanceof EntitySpider) {
            return new Sprite(SPIDER_TEXTURE, 64d, 32d, 40d, 12d, 48d, 52d);
        } else if (entity instanceof EntityStray) {
            return new Sprite(STRAY_SKELETON_TEXTURE, 64d, 32d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityVex) {
            return new Sprite(VEX_TEXTURE, 64d, 64d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityVindicator) {
            return new Sprite(VINDICATOR_TEXTURE, 64d, 64d, 8d, 8d, 16d, 18d);
        } else if (entity instanceof EntityWitch) {
            return MARKER_WITCH;
        } else if (entity instanceof EntityWitherSkeleton) {
            return new Sprite(WITHER_SKELETON_TEXTURE, 64d, 32d, 8d, 8d, 16d, 16d);
        } else if (entity instanceof EntityZombieVillager) {
            return MARKER_ZOMBIE_VILLAGER;
        } else if (entity instanceof EntityPigZombie) {
            return MARKER_PIGLIN_ZOMBIFIED;
        } else if (entity instanceof EntityZombie) {
            return new Sprite(ZOMBIE_TEXTURE, 64d, 64d, 8d, 8d, 16d, 16d);
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
