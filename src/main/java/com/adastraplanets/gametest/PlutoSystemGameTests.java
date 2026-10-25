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

/** The Pluto system: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PlutoSystemGameTests {
    @GameTest(template = "empty")
    public static void charonIsATier9Destination(GameTestHelper helper) {
        Planet charon = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("charon")));
        helper.assertTrue(charon != null, "Charon is not a planet");
        helper.assertTrue(charon.tier() == 9, "Charon should be tier 9, is " + charon.tier());
        helper.assertTrue(charon.orbit().isPresent(), "Charon has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("mordrine_ore")), "No placed feature mordrine_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void plutoIsATier9Destination(GameTestHelper helper) {
        Planet pluto = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("pluto")));
        helper.assertTrue(pluto != null, "Pluto is not a planet");
        helper.assertTrue(pluto.tier() == 9, "Pluto should be tier 9, is " + pluto.tier());
        helper.assertTrue(pluto.orbit().isPresent(), "Pluto has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("cordium_ore")), "No placed feature cordium_ore");
        helper.succeed();
    }

    private PlutoSystemGameTests() {
    }
}
