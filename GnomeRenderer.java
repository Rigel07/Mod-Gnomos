package com.gnomos.client;

import com.gnomos.GnomosMod;
import com.gnomos.entity.GnomeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GnomeRenderer extends MobRenderer<GnomeEntity, GnomeModel> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[GnomeEntity.COLORS.length];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(GnomosMod.MODID, "textures/entity/gnome_" + GnomeEntity.COLORS[i] + ".png");
        }
    }

    public GnomeRenderer(EntityRendererProvider.Context context) {
        super(context, new GnomeModel(context.bakeLayer(GnomeModel.LAYER)), 0.25F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(GnomeEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.43F, 0.43F, 0.43F);
    }

    @Override
    public ResourceLocation getTextureLocation(GnomeEntity entity) {
        return TEXTURES[entity.getVariant()];
    }
}
