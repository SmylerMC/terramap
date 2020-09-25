package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.ResourceLocation;

public class AgressiveMobMarker extends AbstractCreatureMarker {

	public AgressiveMobMarker(MarkerController<?> controller, Entity entity) {
		super(controller, 0, 0, null, 0, 0, entity);
		if(entity instanceof EntityBlaze) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = BLAZE_TEXTURE;
		} else if(entity instanceof EntityCreeper) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = CREEPER_TEXTURE;
		} else if(entity instanceof EntityElderGuardian) {
			this.width = this.height = 12;
			this.u = this.v = 16;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = GUARDIAN_ELDER_TEXTURE; //TODO The eye is missing
		} else if(entity instanceof EntityEnderman) { //TODO TEST
			this.texture = SmyLibGui.WIDGET_TEXTURES;
			this.width = 8;
			this.height = 8;
			this.u = 16;
			this.v = 94;
			this.textureHeight = this.textureWidth = 256;
		} else if(entity instanceof EntityEndermite) {
			this.width = 4;
			this.height = 3;
			this.u = this.v = 2;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = ENDERMITE_TEXTURE;
		} else if(entity instanceof EntityEvoker) {
			this.width = 8;
			this.height = 10;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = EVOKER_TEXTURE;
		} else if(entity instanceof EntityGhast) {
			this.width = this.height = 16;
			this.u = this.v = 16;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = GHAST_TEXTURE;
		} else if(entity instanceof EntityGuardian) {
			this.width = this.height = 12;
			this.u = this.v = 16;
			this.textureWidth = this.textureHeight = 64;
			this.texture = GUARDIAN_TEXTURE; //TODO The eye is missing
		} else if(entity instanceof EntityHusk) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = this.textureHeight = 64;
			this.texture = HUSK_ZOMBIE_TEXTURE;
		} else if(entity instanceof EntityMagmaCube) { //TODO Test
			this.texture = SmyLibGui.WIDGET_TEXTURES;
			this.width = this.height = 8;
			this.u = 34;
			this.v = 94;
			this.textureHeight = this.textureWidth = 256;
		} else if(entity instanceof EntityShulker) {
			this.width = this.height = 6;
			this.u = 6;
			this.v = 58;
			this.textureWidth = this.textureHeight = 64;
			this.texture = SHULKER_TEXTURE;
		} else if(entity instanceof EntitySilverfish) {
			this.width = 6;
			this.height = 4;
			this.u = this.v = 4;
			this.textureWidth = 128;
			this.textureHeight = 64;
			this.texture = SILVERFISH_TEXTURE;
		} else if(entity instanceof EntitySkeleton) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = SKELETON_TEXTURE;
		} else if(entity instanceof EntitySlime) { //TODO Test
			this.texture = SmyLibGui.WIDGET_TEXTURES;
			this.width = this.height = 8;
			this.u = 25;
			this.v = 94;
			this.textureHeight = this.textureWidth = 256;
		} else if(entity instanceof EntityCaveSpider) { //TODO Test
			this.width = 8;
			this.height = 8;
			this.u = 40;
			this.v = 12;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = CAVE_SPIDER_TEXTURE;
		} else if(entity instanceof EntitySpider) {
			this.width = 8;
			this.height = 8;
			this.u = 40;
			this.v = 12;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = SPIDER_TEXTURE;
		} else if(entity instanceof EntityStray) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = STRAY_SKELETON_TEXTURE;
		} else if(entity instanceof EntityVex) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = this.textureHeight = 64;
			this.texture = VEX_TEXTURE;
		} else if(entity instanceof EntityVindicator) {
			this.width = 8;
			this.height = 10;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = VINDICATOR_TEXTURE;
		} else if(entity instanceof EntityWitch) {
			this.width = 8;
			this.height = 12;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 128;
			this.texture = WITCH_TEXTURE; //TODO missing hat
		} else if(entity instanceof EntityWitherSkeleton) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 32;
			this.texture = WITHER_SKELETON_TEXTURE;
		} else if(entity instanceof EntityZombieVillager) {
			this.width = 8;
			this.height = 12;
			this.u = this.v = 8;
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.texture = ZOMBIE_VILLAGER_TEXTURE;
		} else if(entity instanceof EntityZombie) {
			this.width = this.height = 8;
			this.u = this.v = 8;
			this.textureWidth = this.textureHeight = 64;
			this.texture = ZOMBIE_TEXTURE;
		} else {
			this.texture = SmyLibGui.WIDGET_TEXTURES;
			this.width = 10;
			this.height = 10;
			this.u = 22;
			this.v = 69;
			this.textureHeight = this.textureWidth = 256;
		}
	}
	
	private static final ResourceLocation BLAZE_TEXTURE = new ResourceLocation("textures/entity/blaze.png");
    private static final ResourceLocation CAVE_SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/cave_spider.png");
	private static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
    private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
    private static final ResourceLocation ENDERMITE_TEXTURE = new ResourceLocation("textures/entity/endermite.png");
    private static final ResourceLocation EVOKER_TEXTURE = new ResourceLocation("textures/entity/illager/evoker.png");
    private static final ResourceLocation GHAST_TEXTURE = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation HUSK_ZOMBIE_TEXTURE = new ResourceLocation("textures/entity/zombie/husk.png");
    private static final ResourceLocation SHULKER_TEXTURE = new ResourceLocation("textures/entity/shulker/shulker_purple.png");
    private static final ResourceLocation SILVERFISH_TEXTURE = new ResourceLocation("textures/entity/silverfish.png");
    private static final ResourceLocation SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
    private static final ResourceLocation STRAY_SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/stray.png");
    private static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
    private static final ResourceLocation VINDICATOR_TEXTURE = new ResourceLocation("textures/entity/illager/vindicator.png");
    private static final ResourceLocation WITCH_TEXTURE = new ResourceLocation("textures/entity/witch.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURE = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

}
