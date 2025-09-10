package net.smyler.terramap.gui.widgets.markers;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.concurrent.OnceExecutor;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.terramap.Terramap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.minecraft.client.Minecraft.getMinecraft;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Preconditions.checkState;

/**
 * Marker styling utility functions.
 *
 * @author Smyler
 */
public final class MarkerStyling {

    private static final OnceExecutor LOG_BIPED_ONCE = new OnceExecutor();

    public static boolean hasBipedModel(EntityLiving entity) {
        Render<?> render = getMinecraft().getRenderManager().getEntityRenderObject(entity);
        if (!(render instanceof RenderLiving)) {
            return false;
        }
        RenderLiving<?> renderLiving = (RenderLiving<?>) render;
        ModelBase model = renderLiving.getMainModel();
        return model instanceof ModelBiped;
    }

    public static EntityMarkerStyle fromModelBiped(EntityLiving entity) {
        try {
            Render<?> renderAbstract = getMinecraft().getRenderManager().getEntityRenderObject(entity);
            checkArgument(renderAbstract instanceof RenderLiving, "Non-living entity given to " + MarkerStyling.class.getSimpleName() + ".fromBipedModel()");
            RenderLiving<?> render = (RenderLiving<?>) renderAbstract;
            ModelBase modelBase = render.getMainModel();
            checkState(modelBase instanceof ModelBiped, "Entity has a non-biped model");
            ModelBiped model = (ModelBiped) modelBase;
            checkState(!model.bipedHead.cubeList.isEmpty(), "Biped model has no head");

            Identifier texture = getEntityTexture(renderAbstract, entity);
            Sprite sprite = getSpriteForModelBoxFace(texture, model, model.bipedHead.cubeList.get(0));

            return new EntityMarkerStyle(sprite);
        } catch (InvocationTargetException | IllegalAccessException | IllegalStateException e) {
            LOG_BIPED_ONCE.execute(() -> {
                Terramap.instance().logger().error("Failed to get map marker style for a biped entity. This may indicate compatibility issues with other mods.");
                Terramap.instance().logger().catching(e);
            });
            return new EntityMarkerStyle();
        }
    }

    private static Sprite getSpriteForModelBoxFace(Identifier texture, ModelBase model, ModelBox box) throws IllegalAccessException {
        TexturedQuad[] quads = getModelBoxTexturedQuads(box);
        if (quads.length != 6) {
            return null;
        }
        TexturedQuad quad = quads[4];
        if (quad.nVertices != 4) {
            return null;
        }
        return Sprite.builder()
                .texture(texture, model.textureWidth, model.textureHeight)
                .xLeft(quad.vertexPositions[1].texturePositionX * model.textureWidth)
                .yTop(quad.vertexPositions[1].texturePositionY * model.textureHeight)
                .xRight(quad.vertexPositions[3].texturePositionX * model.textureWidth)
                .yBottom(quad.vertexPositions[3].texturePositionY * model.textureHeight)
                .build();
    }

    private static final Field REFLECT_FIELD_MODELBOX_TEXTUREDQUADS;
    private static final Method REFLECT_METHOD_RENDER_GETENTITYTEXTURE;

    static {
        REFLECT_FIELD_MODELBOX_TEXTUREDQUADS = findObfuscatedField(ModelBox.class, "field_78254_i");
        REFLECT_METHOD_RENDER_GETENTITYTEXTURE = findObfuscatedMethod(Render.class, "func_110775_a", ResourceLocation.class, Entity.class);
    }

    private static TexturedQuad[] getModelBoxTexturedQuads(ModelBox box) throws IllegalAccessException {
        if (REFLECT_FIELD_MODELBOX_TEXTUREDQUADS == null) {
            throw new IllegalAccessException("Reflection failed to find field for ModelBox.texturedQuads");
        }
        return (TexturedQuad[]) REFLECT_FIELD_MODELBOX_TEXTUREDQUADS.get(box);
    }

    private static Identifier getEntityTexture(Render<?> render, Entity entity) throws InvocationTargetException, IllegalAccessException {
        if (REFLECT_METHOD_RENDER_GETENTITYTEXTURE == null) {
            throw new IllegalAccessException("Reflection failed to find field for Render.getEntityTexture(Entity)");
        }
        ResourceLocation location = (ResourceLocation) REFLECT_METHOD_RENDER_GETENTITYTEXTURE.invoke(render, entity);
        return Identifier.parse(location.toString());
    }

    private static @Nullable Field findObfuscatedField(@NotNull Class<?> clazz, @NotNull String srgName) {
        try {
            return ObfuscationReflectionHelper.findField(clazz, srgName);
        } catch (Exception e) {
            Terramap.instance().logger().error("failed to find obfuscated field {} in class {} using reflection", srgName, clazz.getName());
            Terramap.instance().logger().catching(e);
            return null;
        }
    }

    private static @Nullable Method findObfuscatedMethod(@NotNull Class<?> clazz, @NotNull String srgName, @NotNull Class<?> returnType, @NotNull Class<?>... paramTypes) {
        try {
            return ObfuscationReflectionHelper.findMethod(clazz, srgName, returnType, paramTypes);
        } catch (Exception e) {
            Terramap.instance().logger().error("failed to find obfuscated method {} in class {} using reflection", srgName, clazz.getName());
            Terramap.instance().logger().catching(e);
            return null;
        }
    }

    private MarkerStyling() {
        throw new IllegalStateException("Utility class");
    }

}
