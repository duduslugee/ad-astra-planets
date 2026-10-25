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

/** The Neptune system: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NeptuneSystemGameTests {
    @GameTest(template = "empty")
    public static void tritonIsATier8Destination(GameTestHelper helper) {
        Planet triton = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("triton")));
        helper.assertTrue(triton != null, "Triton is not a planet");
        helper.assertTrue(triton.tier() == 8, "Triton should be tier 8, is " + triton.tier());
        helper.assertTrue(triton.orbit().isPresent(), "Triton has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("nitralis_ore")), "No placed feature nitralis_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void neptuneIsATier8Destination(GameTestHelper helper) {
        Planet neptune = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("neptune")));
        helper.assertTrue(neptune != null, "Neptune is not a planet");
        helper.assertTrue(neptune.tier() == 8, "Neptune should be tier 8, is " + neptune.tier());
        helper.assertTrue(neptune.orbit().isPresent(), "Neptune has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("tempestine_ore")), "No placed feature tempestine_ore");
        helper.succeed();
    }

    private NeptuneSystemGameTests() {
    }
}
