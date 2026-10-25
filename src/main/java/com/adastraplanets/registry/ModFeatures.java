package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.world.feature.GeyserFeature;
import com.adastraplanets.world.feature.HydrothermalVentFeature;
import com.adastraplanets.world.feature.StepSlabsFeature;
import net.minecraft.core.registries.Registries;
import com.adastraplanets.world.density.BandsFunction;
import com.adastraplanets.world.density.CratersFunction;
import com.adastraplanets.world.density.GroovesFunction;
import com.adastraplanets.world.density.RingBasinsFunction;
import com.adastraplanets.world.density.VolcanoesFunction;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Feature and density function types whose shapes need code; where and how they generate is set by configured/placed feature JSON. */
public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, AdAstraPlanets.MOD_ID);

    public static final DeferredHolder<Feature<?>, HydrothermalVentFeature> HYDROTHERMAL_VENT = FEATURES.register("hydrothermal_vent",
            () -> new HydrothermalVentFeature(HydrothermalVentFeature.Configuration.CODEC));
    public static final DeferredHolder<Feature<?>, StepSlabsFeature> STEP_SLABS = FEATURES.register("step_slabs",
            () -> new StepSlabsFeature(StepSlabsFeature.Configuration.CODEC));
    public static final DeferredHolder<Feature<?>, GeyserFeature> GEYSER = FEATURES.register("geyser",
            () -> new GeyserFeature(GeyserFeature.Configuration.CODEC));

    public static final DeferredRegister<net.minecraft.world.level.levelgen.structure.StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, AdAstraPlanets.MOD_ID);
    /** Jigsaw structures laid on the highest ground of their footprint (see LevelledJigsawStructure). */
    public static final DeferredHolder<net.minecraft.world.level.levelgen.structure.StructureType<?>,
            net.minecraft.world.level.levelgen.structure.StructureType<com.adastraplanets.feature.LevelledJigsawStructure>> LEVELLED_JIGSAW =
            STRUCTURE_TYPES.register("levelled_jigsaw", () -> () -> com.adastraplanets.feature.LevelledJigsawStructure.CODEC);

    public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES =
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, AdAstraPlanets.MOD_ID);

    /** Impact craters for airless moons (see CratersFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<CratersFunction>> CRATERS =
            DENSITY_FUNCTION_TYPES.register("craters", () -> CratersFunction.MAP_CODEC);
    /** Alternating bands along an axis, e.g. a gas giant's zones and belts (see BandsFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<BandsFunction>> BANDS =
            DENSITY_FUNCTION_TYPES.register("bands", () -> BandsFunction.MAP_CODEC);
    /** Multi-ring impact basins (see RingBasinsFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<RingBasinsFunction>> RING_BASINS =
            DENSITY_FUNCTION_TYPES.register("ring_basins", () -> RingBasinsFunction.MAP_CODEC);
    /** Parallel groove sets, each lane its own direction (see GroovesFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<GroovesFunction>> GROOVES =
            DENSITY_FUNCTION_TYPES.register("grooves", () -> GroovesFunction.MAP_CODEC);
    /** Hexagonal columns, each its own height step (see HexColumnsFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<com.adastraplanets.world.density.HexColumnsFunction>> HEX_COLUMNS =
            DENSITY_FUNCTION_TYPES.register("hex_columns", () -> com.adastraplanets.world.density.HexColumnsFunction.MAP_CODEC);
    /** Volcanoes with lava-filled calderas (see VolcanoesFunction). */
    public static final DeferredHolder<com.mojang.serialization.MapCodec<? extends DensityFunction>, com.mojang.serialization.MapCodec<VolcanoesFunction>> VOLCANOES =
            DENSITY_FUNCTION_TYPES.register("volcanoes", () -> VolcanoesFunction.MAP_CODEC);

    private ModFeatures() {
    }
}
