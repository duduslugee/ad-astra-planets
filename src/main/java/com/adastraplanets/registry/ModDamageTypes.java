package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

/** Damage types defined in data/adastraplanets/damage_type/. */
public final class ModDamageTypes {
    /** Too little pressure for the worn liner (vacuum, thin air). */
    public static final ResourceKey<DamageType> DECOMPRESSION = ResourceKey.create(Registries.DAMAGE_TYPE, AdAstraPlanets.id("decompression"));
    /** Too much pressure for the worn liner (dense atmospheres). */
    public static final ResourceKey<DamageType> CRUSHING = ResourceKey.create(Registries.DAMAGE_TYPE, AdAstraPlanets.id("crushing"));
    /** Falling below the bottom of a gas giant (see CorePressure). */
    public static final ResourceKey<DamageType> CORE_PRESSURE = ResourceKey.create(Registries.DAMAGE_TYPE, AdAstraPlanets.id("core_pressure"));

    public static DamageSource source(ServerLevel level, ResourceKey<DamageType> type) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
    }

    private ModDamageTypes() {
    }
}
