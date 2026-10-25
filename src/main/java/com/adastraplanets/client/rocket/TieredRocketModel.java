package com.adastraplanets.client.rocket;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/** Shared by a rocket's entity and item renderers, so both use the same Blockbench export. */
public class TieredRocketModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public TieredRocketModel(ResourceLocation rocketId) {
        this.model = rocketId.withPath(path -> "geo/entity/" + path + ".geo.json");
        this.texture = rocketId.withPath(path -> "textures/entity/" + path + ".png");
        this.animation = rocketId.withPath(path -> "animations/entity/" + path + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return this.animation;
    }
}
