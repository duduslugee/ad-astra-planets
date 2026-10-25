package com.adastraplanets.world;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModDamageTypes;
import com.adastraplanets.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * On worlds with no ground (dimension types tagged {@code adastraplanets:void_worlds}, the gas giants), whatever falls
 * below the bottom of the world is crushed by the pressure of the planet's core, with its own death message naming the
 * planet ("Steve was crushed by the pressure of Saturn's core") instead of Minecraft's "fell out of the world".
 */
public final class CorePressure {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(CorePressure::tick);
    }

    private static void tick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || !(entity.level() instanceof ServerLevel level)
                || !entity.isAlive() || entity.getY() >= level.getMinBuildHeight()
                || !level.dimensionTypeRegistration().is(ModTags.DimensionTypes.VOID_WORLDS)) {
            return;
        }
        ResourceLocation dimension = level.dimension().location();
        Component planet = Component.translatable("planet." + dimension.getNamespace() + "." + dimension.getPath());
        Holder<DamageType> type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ModDamageTypes.CORE_PRESSURE);
        entity.hurt(new Source(type, planet), Float.MAX_VALUE);
    }

    /** The damage, carrying the planet's name for the death message. */
    private static final class Source extends DamageSource {
        private final Component planet;

        Source(Holder<DamageType> type, Component planet) {
            super(type);
            this.planet = planet;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity killed) {
            return Component.translatable("death.attack." + AdAstraPlanets.MOD_ID + ".core_pressure", killed.getDisplayName(), this.planet);
        }
    }

    private CorePressure() {
    }
}
