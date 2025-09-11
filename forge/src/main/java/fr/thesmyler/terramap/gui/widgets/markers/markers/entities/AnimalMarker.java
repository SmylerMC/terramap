package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.*;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.terramap.gui.widgets.markers.EntityMarkerStylingRuleset;
import net.smyler.terramap.gui.widgets.markers.MarkerStyling;

import static net.smyler.terramap.gui.sprites.TerramapSprites.*;
import static net.smyler.terramap.gui.widgets.markers.MarkerStyling.hasModelPredicate;

/**
 * Map marker for any entity that implements IAnimals but not IMobs
 * The corresponding controller is CreatureMarkerController
 * 
 * @author Smyler
 *
 */
public class AnimalMarker extends AbstractLivingMarker {

    private static final EntityMarkerStylingRuleset rules = new EntityMarkerStylingRuleset(MARKER_TOKEN_GREY);

    public AnimalMarker(MarkerController<?> controller, Entity entity) {
        super(controller, spriteFor(entity), entity);

    }

    private static Sprite spriteFor(Entity entity) {
        return rules.getStyleFor(entity).sprite();
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

    private static final Sprite SQUID = Sprite.builder()
            .texture(VANILLA_TEXTURE_ENTITY.resolve("squid.png"))
            .textureDimensions(64d, 32d)
            .xLeft(12d).yTop(12d)
            .width(12d).height(14d)
            .build();

    static {
        // Top level classes
        rules.add(IAnimals.class, MARKER_TOKEN_GREEN);

        // Grabs the face texture from well-known models, often overridden below
        rules.add(EntityLiving.class, hasModelPredicate(ModelBiped.class), MarkerStyling::fromModelBiped);
        rules.add(EntityLiving.class, hasModelPredicate(ModelQuadruped.class), MarkerStyling::fromModelQuadruped);

        // Horses
        rules.add(AbstractHorse.class, MARKER_HORSE);
        rules.add(EntitySkeletonHorse.class, MARKER_SKELETON_HORSE);
        rules.add(EntityZombieHorse.class, MARKER_ZOMBIE_HORSE);
        rules.add(EntityDonkey.class, MARKER_DONKEY);
        rules.add(EntityMule.class, MARKER_MULE);

        rules.add(EntityVillager.class, MARKER_VILLAGER);

        // Neutral entities
        rules.add(EntityIronGolem.class, MARKER_IRON_GOLEM);
        rules.add(EntitySnowman.class, SNOW_MAN);
        rules.add(EntityLlama.class, MARKER_LLAMA);  // So Llamas are horses according to the game, #poo
        rules.add(EntityWolf.class, MARKER_WOLF);

        // Farm animals
        rules.add(EntityChicken.class, MARKER_CHICKEN);
        rules.add(EntityPig.class, MARKER_PIG);
        rules.add(EntityRabbit.class, MARKER_RABBIT);

        // Cats and ocelot
        rules.add(EntityOcelot.class, MARKER_OCELOT);
        rules.add(EntityOcelot.class, EntityTameable::isTamed, MARKER_CAT);

        rules.add(EntityBat.class, BAT);
        rules.add(EntityParrot.class, MARKER_PARROT);

        rules.add(EntitySquid.class, SQUID);
    }

}
