package fr.thesmyler.terramap.gui.widgets.markers.markers.entities;

import fr.thesmyler.smylibgui.SmyLibGui;
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
import net.minecraft.util.ResourceLocation;

/**
 * Map marker for any entity that implements IAnimals but not IMobs
 * The corresponding controller is CreatureMarkerController
 * 
 * @author SmylerMC
 *
 */
public class AnimalMarker extends AbstractLivingMarker {

	public AnimalMarker(MarkerController<?> controller, Entity entity) {
		super(controller, 10, 10, SmyLibGui.WIDGET_TEXTURES, 11, 69, 256, 256, entity);
		if(entity instanceof EntitySkeletonHorse) {
			this.width = 6;
			this.height = 15;
			this.u = 1;
			this.v = 44;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		} else if(entity instanceof EntityZombieHorse) {
			this.width = 6;
			this.height = 15;
			this.u = 8;
			this.v = 44;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityDonkey) {
			this.width = 6;
			this.height = 21;
			this.u = 1;
			this.v = 60;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityMule) {
			this.width = 6;
			this.height = 21;
			this.u = 8;
			this.v = 60;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityLlama) {
			this.width = 8;
			this.height = 11;
			this.u = 1;
			this.v = 110;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityHorse) {
			this.width = 6;
			this.height = 15;
			this.u = 15;
			this.v = 44;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityVillager) {
			this.width = 8;
			this.height = 11;
			this.u = 40;
			this.v = 10;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityIronGolem) {
			this.width = 8;
			this.height = 11;
			this.u = 1;
			this.v = 98;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntitySnowman) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = SNOW_MAN_TEXTURE;
		}else if(entity instanceof EntityBat) {
			this.width = this.height = 6;
			this.u = this.v = 6;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = BAT_TEXTURE;
		}else if(entity instanceof EntityPolarBear) {
			this.width = this.height = 7;
			this.u = this.v = 7;
			this.textureWidth = 128;
			this.textureHeight = 64;
			this.texture = POLAR_BEAR_TEXTURE;
		}else if(entity instanceof EntityChicken) {
			this.width = 4;
			this.height = 6;
			this.u = 7;
			this.v = 82;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityMooshroom) {
			this.width = this.height = 8;
			this.u = this.v = 6;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = MOOSHROOM_TEXTURE;
		}else if(entity instanceof EntityCow) {
			this.width = this.height = 8;
			this.u = this.v = 6;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = COW_TEXTURE;
		}else if(entity instanceof EntityPig) {
			this.width = this.height = 8;
			this.u = 28;
			this.v = 1;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityRabbit) {
			this.width = 5;
			this.height = 9;
			this.u = 1;
			this.v = 82;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntitySheep) {
			this.width = this.height = 6;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = SHEEP_TEXTURE;
		}else if(entity instanceof EntitySquid) {
			this.width = 12;
			this.height = 16;
			this.u = this.v = 12;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = SQUID_TEXTURE;
		}else if(entity instanceof EntityOcelot) {
			boolean tamed = ((EntityOcelot) entity).isTamed();
			this.width = this.height = 5;
			this.u = tamed ? 7: 1;
			this.v = 92;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityParrot) {
			this.width = 4;
			this.height = 8;
			this.u = 12;
			this.v = 82;
			this.textureWidth = this.textureHeight = 256;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}else if(entity instanceof EntityWolf) {
			this.width = this.height = 6;
			this.u = 13;
			this.v = 5;
			this.textureWidth = 128;
			this.textureHeight = 128;
			this.texture = AbstractLivingMarker.ENTITY_MARKERS_TEXTURE;
		}
	}
	
    private static final ResourceLocation SNOW_MAN_TEXTURE = new ResourceLocation("textures/entity/snowman.png");
    private static final ResourceLocation BAT_TEXTURE = new ResourceLocation("textures/entity/bat.png");
    private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");
    private static final ResourceLocation COW_TEXTURE = new ResourceLocation("textures/entity/cow/cow.png");
    private static final ResourceLocation MOOSHROOM_TEXTURE = new ResourceLocation("textures/entity/cow/mooshroom.png");
    private static final ResourceLocation SHEEP_TEXTURE = new ResourceLocation("textures/entity/sheep/sheep.png");
    private static final ResourceLocation SQUID_TEXTURE = new ResourceLocation("textures/entity/squid.png");


}
