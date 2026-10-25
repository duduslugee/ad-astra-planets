package com.adastraplanets.client.rocket;

import com.adastraplanets.rocket.TieredRocketItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TieredRocketItemRenderer extends GeoItemRenderer<TieredRocketItem> {
    public TieredRocketItemRenderer(ResourceLocation rocketId) {
        super(new TieredRocketModel<>(rocketId));
    }

    /**
     * GeckoLib centres item models on the block (0.5, 0.51, 0.5). Ad Astra's rocket items render with the rocket's
     * feet at the origin instead; skipping the offset lets our item model's display transforms match theirs, so the
     * icon and the held-over-head pose line up with Ad Astra's rockets.
     */
    @Override
    public void preRender(PoseStack poseStack, TieredRocketItem animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());
        this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}
