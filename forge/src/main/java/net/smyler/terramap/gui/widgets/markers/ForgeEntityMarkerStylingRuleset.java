package net.smyler.terramap.gui.widgets.markers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;

import static net.smyler.terramap.gui.sprites.TerramapSprites.*;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_CAT;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_CHICKEN;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_DONKEY;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_IRON_GOLEM;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_LLAMA;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_MULE;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_OCELOT;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_PARROT;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_PIG;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_RABBIT;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_SKELETON_HORSE;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_VILLAGER;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_WOLF;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_ZOMBIE_HORSE;
import static net.smyler.terramap.gui.widgets.markers.MarkerStyling.hasModelPredicate;

public class ForgeEntityMarkerStylingRuleset extends EntityMarkerStylingRuleset {

    private static final Identifier VANILLA_TEXTURE_ENTITY = new Identifier("minecraft", "textures/entity");
    public static final EntityMarkerStylingRuleset INSTANCE = new ForgeEntityMarkerStylingRuleset();

    public ForgeEntityMarkerStylingRuleset() {
        super(MARKER_TOKEN_GREY);

        // Top level classes (mobs may be animals in the class hierarchy)
        this.add(IAnimals.class, MARKER_TOKEN_GREEN);
        this.add(IMob.class, MARKER_TOKEN_RED);

        // Grabs the face texture from well-known models, often overridden below
        this.add(EntityLiving.class, hasModelPredicate(ModelBiped.class), MarkerStyling::fromModelBiped);
        this.add(EntityLiving.class, hasModelPredicate(ModelQuadruped.class), MarkerStyling::fromModelQuadruped);

        // For some entities, grab the texture directly
        this.add(EntitySnowman.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("snowman.png"))
                .textureDimensions(64d, 64d)
                .xLeft(8d).yTop(8d)
                .width(8d).height(8d)
                .build());
        this.add(EntityBat.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("bat.png"))
                .textureDimensions(64d, 64d)
                .xLeft(6d).yTop(6d)
                .width(6d).height(6d)
                .build());
        this.add(EntitySquid.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("squid.png"))
                .textureDimensions(64d, 32d)
                .xLeft(12d).yTop(12d)
                .width(12d).height(14d)
                .build());
        this.add(EntityCreeper.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("creeper/creeper.png"))
                .textureDimensions(64d, 32d)
                .xLeft(8d).yTop(8d)
                .xRight(16d).yBottom(16d)
                .build());
        this.add(EntitySpider.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("spider/spider.png"))
                .textureDimensions(64d, 32d)
                .xLeft(40d).yTop(12d)
                .xRight(48d).yBottom(20d)
                .build());
        this.add(EntitySilverfish.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("silverfish.png"))
                .textureDimensions(128d, 64)
                .xLeft(4d).yTop(4d)
                .xRight(10d).yBottom(8d)
                .build());
        this.add(EntityCaveSpider.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("spider/cave_spider.png"))
                .textureDimensions(64d, 32d)
                .xLeft(40d).yTop(12d)
                .xRight(48d).yBottom(20d)
                .build());
        this.add(EntityEvoker.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/evoker.png"))
                .textureDimensions(64d, 64d)
                .xLeft(8d).yTop(8d)
                .xRight(16d).yBottom(18d)
                .build());
        this.add(EntityVindicator.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("illager/vindicator.png"))
                .textureDimensions(64d, 64d)
                .xLeft(8d).yTop(8d)
                .xRight(16d).yBottom(18d)
                .build());
        this.add(EntityEndermite.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("endermite.png"))
                .textureDimensions(64d, 32d)
                .xLeft(2d).yTop(2d)
                .xRight(6d).yBottom(5d)
                .build());
        this.add(EntityShulker.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("shulker/shulker_purple.png"))
                .textureDimensions(64d, 64d)
                .xLeft(6d).yTop(58d)
                .xRight(12d).yBottom(64d)
                .build());
        this.add(EntityBlaze.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("blaze.png"))
                .textureDimensions(64d, 32d)
                .xLeft(8d).yTop(8d)
                .xRight(16d).yBottom(16d)
                .build());
        this.add(EntityGhast.class, Sprite.builder()
                .texture(VANILLA_TEXTURE_ENTITY.resolve("ghast/ghast.png"))
                .textureDimensions(64d, 32d)
                .xLeft(16d).yTop(16d)
                .xRight(32d).yBottom(32d)
                .build());

        // And most entities have a dedicated marker sprites from the resources
        this.add(AbstractHorse.class, MARKER_HORSE);
        this.add(EntitySkeletonHorse.class, MARKER_SKELETON_HORSE);
        this.add(EntityZombieHorse.class, MARKER_ZOMBIE_HORSE);
        this.add(EntityDonkey.class, MARKER_DONKEY);
        this.add(EntityMule.class, MARKER_MULE);
        this.add(EntityLlama.class, MARKER_LLAMA);  // Llamas are horses in the class hierarchy, so this needs to come after horse
        this.add(EntityVillager.class, MARKER_VILLAGER);
        this.add(EntityIronGolem.class, MARKER_IRON_GOLEM);
        this.add(EntityWolf.class, MARKER_WOLF);
        this.add(EntityChicken.class, MARKER_CHICKEN);
        this.add(EntityPig.class, MARKER_PIG);
        this.add(EntityRabbit.class, MARKER_RABBIT);
        this.add(EntityOcelot.class, MARKER_OCELOT);
        this.add(EntityOcelot.class, EntityTameable::isTamed, MARKER_CAT);
        this.add(EntityParrot.class, MARKER_PARROT);
        this.add(EntityWitch.class, MARKER_WITCH);
        this.add(EntitySlime.class, MARKER_SLIME);
        this.add(EntityZombieVillager.class, MARKER_ZOMBIE_VILLAGER);
        this.add(EntityElderGuardian.class, MARKER_ELDER_GUARDIAN);
        this.add(EntityGuardian.class, MARKER_GUARDIAN);
        this.add(EntityMagmaCube.class, MARKER_MAGMA_CUBE);
        this.add(EntityPigZombie.class, MARKER_PIGLIN_ZOMBIFIED);
        this.add(EntityEnderman.class, MARKER_ENDERMAN);
        this.add(EntityDragon.class, MARKER_ENDER_DRAGON);
        this.add(EntityWither.class, MARKER_WITHER);
    }

}
