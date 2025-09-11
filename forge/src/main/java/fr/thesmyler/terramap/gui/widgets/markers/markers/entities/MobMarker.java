package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import net.smyler.terramap.gui.widgets.markers.MarkerStyling;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.terramap.gui.widgets.markers.EntityMarkerStylingRuleset;

import static net.smyler.terramap.gui.sprites.TerramapSprites.*;
import static net.smyler.terramap.gui.widgets.markers.MarkerStyling.hasModelPredicate;

/**
 * Map marker for any entity that implements IMob
 * The corresponding controller is MobMarkerController
 * 
 * @author Smyler
 */
public class MobMarker extends AbstractLivingMarker {

    private static final EntityMarkerStylingRuleset rules = new EntityMarkerStylingRuleset(MARKER_TOKEN_GREY);

    public MobMarker(MarkerController<?> controller, Entity entity) {
        super(controller, spriteFor(entity), entity);
    }

    private static Sprite spriteFor(Entity entity) {
        return rules.getStyleFor(entity).sprite();
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
            .xRight(48d).yBottom(20d)
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

    private static final Sprite SPIDER = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("spider/spider.png"))
            .textureDimensions(64d, 32d)
            .xLeft(40d).yTop(12d)
            .xRight(48d).yBottom(20d)
            .build();

    private static final Sprite VINDICATOR = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/vindicator.png"))
            .textureDimensions(64d, 64d)
            .xLeft(8d).yTop(8d)
            .xRight(16d).yBottom(18d)
            .build();

    static {
        // Fallback to a red dot for anything that implements IMob
        rules.add(IMob.class, MARKER_TOKEN_RED);

        // Grabs the face texture from well-known models, often overridden below
        rules.add(EntityLiving.class, hasModelPredicate(ModelBiped.class), MarkerStyling::fromModelBiped);
        rules.add(EntityLiving.class, hasModelPredicate(ModelQuadruped.class), MarkerStyling::fromModelQuadruped);

        // Common mobs
        rules.add(EntityCreeper.class, CREEPER);
        rules.add(EntitySpider.class, SPIDER);
        rules.add(EntityWitch.class, MARKER_WITCH);

        // Less common mobs (biome specific etc)
        rules.add(EntitySilverfish.class, SILVERFISH);
        rules.add(EntityCaveSpider.class, CAVE_SPIDER);
        rules.add(EntitySlime.class, MARKER_SLIME);
        rules.add(EntityZombieVillager.class, MARKER_ZOMBIE_VILLAGER);

        // Water mobs
        rules.add(EntityElderGuardian.class, MARKER_ELDER_GUARDIAN);
        rules.add(EntityGuardian.class, MARKER_GUARDIAN);

        // Nether mobs
        rules.add(EntityBlaze.class, BLAZE);
        rules.add(EntityGhast.class, GHAST);
        rules.add(EntityMagmaCube.class, MARKER_MAGMA_CUBE);
        rules.add(EntityPigZombie.class, MARKER_PIGLIN_ZOMBIFIED);

        // Ender mobs
        rules.add(EntityEnderman.class, MARKER_ENDERMAN);
        rules.add(EntityEndermite.class, ENDERMITE);
        rules.add(EntityShulker.class, SHULKER);

        // Illagers
        rules.add(EntityEvoker.class, EVOKER);
        rules.add(EntityVindicator.class, VINDICATOR);

        // Bosses
        rules.add(EntityDragon.class, MARKER_ENDER_DRAGON);
        rules.add(EntityWither.class, MARKER_WITHER);
    }

}
