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

/** Phobos and Deimos: tier 2 destinations, each with the ore that feeds the Tier 5 rocket. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MarsMoonsGameTests {
    @GameTest(template = "empty")
    public static void marsMoonsAreTier2Destinations(GameTestHelper helper) {
        for (String moon : new String[]{"phobos", "deimos"}) {
            Planet planet = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id(moon)));
            helper.assertTrue(planet != null, moon + " is not a planet");
            helper.assertTrue(planet.tier() == 2, moon + " should be tier 2, is " + planet.tier());
            helper.assertTrue(planet.orbit().isPresent(), moon + " has no orbit");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void marsMoonOresAreConfigured(GameTestHelper helper) {
        var placed = helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        for (String ore : new String[]{"umbrite_ore", "pallorite_ore"}) {
            helper.assertTrue(placed.containsKey(AdAstraPlanets.id(ore)), "No placed feature " + ore);
        }
        helper.succeed();
    }

    private MarsMoonsGameTests() {
    }
}
