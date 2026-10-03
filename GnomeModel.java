package com.gnomos.client;

import com.gnomos.GnomosMod;
import com.gnomos.entity.GnomeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/** Gnomo: cabeza grande, gorro puntiagudo (con la punta caída), barba, mochila y brazos cortos. */
public class GnomeModel extends EntityModel<GnomeEntity> implements ArmedModel {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(GnomosMod.MODID, "gnome"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart hatTip;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public GnomeModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.hatTip = this.head.getChild("hat_tip");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 22).addBox(-3.0F, -6.0F, -2.0F, 6.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));
        body.addOrReplaceChild("backpack",
                CubeListBuilder.create().texOffs(36, 22).addBox(-2.0F, -5.0F, 2.0F, 4.0F, 4.0F, 2.0F),
                PartPose.ZERO);

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F)
                        .texOffs(48, 12).addBox(-4.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F)
                        .texOffs(48, 12).addBox(3.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F)
                        .texOffs(56, 0).addBox(-1.0F, -3.0F, -4.0F, 2.0F, 2.0F, 1.0F)
                        .texOffs(20, 22).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 2.0F)
                        .texOffs(24, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 2.0F, 8.0F)
                        .texOffs(0, 12).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 3.0F, 6.0F)
                        .texOffs(24, 12).addBox(-2.0F, -14.0F, -2.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        head.addOrReplaceChild("hat_tip",
                CubeListBuilder.create().texOffs(40, 12).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -14.0F, 0.0F, 0.35F, 0.0F, 0.0F));

        CubeListBuilder arm = CubeListBuilder.create().texOffs(48, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
        root.addOrReplaceChild("right_arm", arm, PartPose.offset(-4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("left_arm", arm, PartPose.offset(4.0F, 15.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(56, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F);
        root.addOrReplaceChild("right_leg", leg, PartPose.offset(-1.5F, 20.0F, 0.0F));
        root.addOrReplaceChild("left_leg", leg, PartPose.offset(1.5F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(GnomeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float rad = (float) Math.PI / 180.0F;
        head.yRot = netHeadYaw * rad;
        head.xRot = headPitch * rad;
        hatTip.xRot = 0.35F + Mth.sin(ageInTicks * 0.08F) * 0.06F;

        float swing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        rightLeg.xRot = swing;
        leftLeg.xRot = -swing;
        rightArm.xRot = -swing * 0.6F - 0.35F;
        leftArm.xRot = swing * 0.6F;
        rightArm.zRot = 0.05F;
        leftArm.zRot = -0.05F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        (arm == HumanoidArm.RIGHT ? rightArm : leftArm).translateAndRotate(poseStack);
    }
}
