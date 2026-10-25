package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.rocket.TieredRocket;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, AdAstraPlanets.MOD_ID);

    private static final Map<ResourceLocation, DeferredHolder<EntityType<?>, EntityType<TieredRocket>>> ROCKETS = new LinkedHashMap<>();

    // Hitbox size is fixed at registration; everything else about a rocket comes from its RocketDefinition.
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_5_ROCKET = rocket("tier_5_rocket", 1.4F, 9.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_6_ROCKET = rocket("tier_6_rocket", 2.0F, 12.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_7_ROCKET = rocket("tier_7_rocket", 2.0F, 14.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_8_ROCKET = rocket("tier_8_rocket", 2.0F, 15.0F);
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_9_ROCKET = rocket("tier_9_rocket", 2.0F, 17.5F);
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> TIER_10_ROCKET = rocket("tier_10_rocket", 2.0F, 17.5F);
    /** The Ancients' Ship, found in the Last Door's Hangar: any body, no fuel (its rocket definition). */
    public static final DeferredHolder<EntityType<?>, EntityType<TieredRocket>> ANCIENT_SHIP = rocket("ancient_ship", 1.6F, 11.5F);

    /** Registers a rocket entity (built in, or from config/adastraplanets/content.json); its item has the same id. */
    public static DeferredHolder<EntityType<?>, EntityType<TieredRocket>> rocket(String id, float width, float height) {
        DeferredHolder<EntityType<?>, EntityType<TieredRocket>> holder = ENTITY_TYPES.register(id,
                () -> EntityType.Builder.<TieredRocket>of((type, level) -> new TieredRocket(type, level, () -> ModItems.rocketItem(id)),
                                MobCategory.MISC)
                        .fireImmune()
                        .clientTrackingRange(10)
                        .sized(width, height)
                        .build(id));
        ROCKETS.put(AdAstraPlanets.id(id), holder);
        return holder;
    }

    /** Every rocket this addon registered, in registration order. */
    public static Map<ResourceLocation, DeferredHolder<EntityType<?>, EntityType<TieredRocket>>> rockets() {
        return Collections.unmodifiableMap(ROCKETS);
    }

    private ModEntityTypes() {
    }
}
