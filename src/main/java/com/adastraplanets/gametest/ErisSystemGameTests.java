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

/** The Eris system: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ErisSystemGameTests {
    @GameTest(template = "empty")
    public static void dysnomiaIsATier10Destination(GameTestHelper helper) {
        Planet dysnomia = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("dysnomia")));
        helper.assertTrue(dysnomia != null, "Dysnomia is not a planet");
        helper.assertTrue(dysnomia.tier() == 10, "Dysnomia should be tier 10, is " + dysnomia.tier());
        helper.assertTrue(dysnomia.orbit().isPresent(), "Dysnomia has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("shardex_ore")), "No placed feature shardex_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void erisIsATier10Destination(GameTestHelper helper) {
        Planet eris = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("eris")));
        helper.assertTrue(eris != null, "Eris is not a planet");
        helper.assertTrue(eris.tier() == 10, "Eris should be tier 10, is " + eris.tier());
        helper.assertTrue(eris.orbit().isPresent(), "Eris has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("albedon_ore")), "No placed feature albedon_ore");
        helper.succeed();
    }

    private ErisSystemGameTests() {
    }
}
