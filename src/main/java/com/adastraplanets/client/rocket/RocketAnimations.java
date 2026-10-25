package com.adastraplanets.client.rocket;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.object.BakedAnimations;

/** Looks up whether a rocket's animation file (animations/entity/<rocket>.animation.json) holds an animation. */
public final class RocketAnimations {
    public static boolean has(EntityType<?> type, String animation) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        BakedAnimations animations = GeckoLibCache.getBakedAnimations().get(id.withPath(path -> "animations/entity/" + path + ".animation.json"));
        return animations != null && animations.getAnimation(animation) != null;
    }

    private RocketAnimations() {
    }
}
