package com.adastraplanets.config;

import com.adastraplanets.AdAstraPlanets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/** Datapack conditions backed by {@link AdAstraPlanetsConfig}, usable in any {@code neoforge:conditions} list. */
public final class ConfigConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, AdAstraPlanets.MOD_ID);

    public static final Supplier<MapCodec<Option>> OPTION = CONDITIONS.register("option", () -> Option.CODEC);
    public static final Supplier<MapCodec<RocketEnabled>> ROCKET_ENABLED = CONDITIONS.register("rocket_enabled", () -> RocketEnabled.CODEC);

    /** {@code {"type": "adastraplanets:option", "option": "worldgen.helionite_ore"}} */
    public record Option(String option) implements ICondition {
        public static final MapCodec<Option> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("option").forGetter(Option::option)
        ).apply(instance, Option::new));

        @Override
        public boolean test(IContext context) {
            return AdAstraPlanetsConfig.option(this.option);
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }

    /** {@code {"type": "adastraplanets:rocket_enabled", "rocket": "adastraplanets:tier_5_rocket"}} */
    public record RocketEnabled(ResourceLocation rocket) implements ICondition {
        public static final MapCodec<RocketEnabled> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("rocket").forGetter(RocketEnabled::rocket)
        ).apply(instance, RocketEnabled::new));

        @Override
        public boolean test(IContext context) {
            return AdAstraPlanetsConfig.rocketEnabled(this.rocket);
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }

    private ConfigConditions() {
    }
}
