package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Saturn system: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SaturnSystemGameTests {
    @GameTest(template = "empty")
    public static void enceladusIsATier6Destination(GameTestHelper helper) {
        Planet enceladus = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("enceladus")));
        helper.assertTrue(enceladus != null, "Enceladus is not a planet");
        helper.assertTrue(enceladus.tier() == 6, "Enceladus should be tier 6, is " + enceladus.tier());
        helper.assertTrue(enceladus.orbit().isPresent(), "Enceladus has no orbit");
        var placed = helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        helper.assertTrue(placed.containsKey(AdAstraPlanets.id("nivalum_ore")), "No placed feature nivalum_ore");
        helper.assertTrue(placed.containsKey(AdAstraPlanets.id("enceladus_geyser")), "No placed feature enceladus_geyser");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void rheaIsATier6Destination(GameTestHelper helper) {
        Planet rhea = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("rhea")));
        helper.assertTrue(rhea != null, "Rhea is not a planet");
        helper.assertTrue(rhea.tier() == 6, "Rhea should be tier 6, is " + rhea.tier());
        helper.assertTrue(rhea.orbit().isPresent(), "Rhea has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("ferrolith_ore")), "No placed feature ferrolith_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void iapetusIsATier6Destination(GameTestHelper helper) {
        Planet iapetus = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("iapetus")));
        helper.assertTrue(iapetus != null, "Iapetus is not a planet");
        helper.assertTrue(iapetus.tier() == 6, "Iapetus should be tier 6, is " + iapetus.tier());
        helper.assertTrue(iapetus.orbit().isPresent(), "Iapetus has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("vesperine_ore")), "No placed feature vesperine_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void titanIsATier6Destination(GameTestHelper helper) {
        Planet titan = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("titan")));
        helper.assertTrue(titan != null, "Titan is not a planet");
        helper.assertTrue(titan.tier() == 6, "Titan should be tier 6, is " + titan.tier());
        helper.assertTrue(titan.orbit().isPresent(), "Titan has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("tholinar_ore")), "No placed feature tholinar_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void saturnIsATier6VoidWorld(GameTestHelper helper) {
        Planet saturn = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("saturn")));
        helper.assertTrue(saturn != null, "Saturn is not a planet");
        helper.assertTrue(saturn.tier() == 6, "Saturn should be tier 6, is " + saturn.tier());
        helper.assertTrue(saturn.orbit().isPresent(), "Saturn has no orbit");
        var access = helper.getLevel().registryAccess();
        helper.assertTrue(access.registryOrThrow(Registries.PLACED_FEATURE).containsKey(AdAstraPlanets.id("cassinium_ore")),
                "No placed feature cassinium_ore");
        var type = access.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(
                ResourceKey.create(Registries.DIMENSION_TYPE, AdAstraPlanets.id("saturn")));
        helper.assertTrue(type.is(com.adastraplanets.registry.ModTags.DimensionTypes.VOID_WORLDS),
                "Saturn is not a void world (landings would not be moved onto the columns)");
        helper.succeed();
    }

    /** Falling below a gas giant kills with the core's own damage, which, like the void, spares no one. */
    @GameTest(template = "empty")
    public static void corePressureIsRegistered(GameTestHelper helper) {
        var type = helper.getLevel().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(com.adastraplanets.registry.ModDamageTypes.CORE_PRESSURE);
        helper.assertTrue(type.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY), "Core pressure spares the invulnerable");
        helper.assertTrue(type.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR), "Core pressure spares armour");
        helper.succeed();
    }

    /** Liquid methane is a real fluid with a bucket: a placed source block holds it and a bucket picks it up. */
    @GameTest(template = "empty")
    public static void liquidMethaneFillsABucket(GameTestHelper helper) {
        var pos = new net.minecraft.core.BlockPos(1, 2, 1);
        helper.setBlock(pos, com.adastraplanets.registry.ModFluids.LIQUID_METHANE_BLOCK.get());
        var state = helper.getBlockState(pos).getFluidState();
        helper.assertTrue(state.is(com.adastraplanets.registry.ModTags.Fluids.METHANE), "Placed block holds no methane");
        helper.assertTrue(state.isSource(), "Placed methane is not a source");
        helper.assertTrue(com.adastraplanets.registry.ModFluids.LIQUID_METHANE.get().getBucket()
                == com.adastraplanets.registry.ModFluids.LIQUID_METHANE_BUCKET.get(), "Methane has no bucket");
        helper.succeed();
    }
}
