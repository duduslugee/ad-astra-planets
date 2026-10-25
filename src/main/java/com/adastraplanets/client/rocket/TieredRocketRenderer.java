package com.adastraplanets.client.rocket;

import com.adastraplanets.rocket.TieredRocket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TieredRocketRenderer extends GeoEntityRenderer<TieredRocket> {
    public TieredRocketRenderer(EntityRendererProvider.Context context, ResourceLocation rocketId) {
        super(context, new TieredRocketModel<>(rocketId));
        this.shadowRadius = 0.5F;
    }

    @Override
    protected void applyRotations(TieredRocket rocket, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        float yaw = Mth.rotLerp(partialTick, rocket.yRotO, rocket.getYRot());
        // Same rumble as Ad Astra's rockets during countdown and flight.
        if (!Minecraft.getInstance().isPaused() && (rocket.isLaunching() || rocket.hasLaunched())) {
            yaw += (float) (rocket.level().random.nextGaussian() * 0.3);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
    }
}
