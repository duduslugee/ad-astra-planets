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

/** The Uranus system: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class UranusSystemGameTests {
    @GameTest(template = "empty")
    public static void mirandaIsATier7Destination(GameTestHelper helper) {
        Planet miranda = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("miranda")));
        helper.assertTrue(miranda != null, "Miranda is not a planet");
        helper.assertTrue(miranda.tier() == 7, "Miranda should be tier 7, is " + miranda.tier());
        helper.assertTrue(miranda.orbit().isPresent(), "Miranda has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("mosaicon_ore")), "No placed feature mosaicon_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void titaniaIsATier7Destination(GameTestHelper helper) {
        Planet titania = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("titania")));
        helper.assertTrue(titania != null, "Titania is not a planet");
        helper.assertTrue(titania.tier() == 7, "Titania should be tier 7, is " + titania.tier());
        helper.assertTrue(titania.orbit().isPresent(), "Titania has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("chasmal_ore")), "No placed feature chasmal_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void oberonIsATier7Destination(GameTestHelper helper) {
        Planet oberon = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("oberon")));
        helper.assertTrue(oberon != null, "Oberon is not a planet");
        helper.assertTrue(oberon.tier() == 7, "Oberon should be tier 7, is " + oberon.tier());
        helper.assertTrue(oberon.orbit().isPresent(), "Oberon has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("obscurex_ore")), "No placed feature obscurex_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void uranusIsATier7Destination(GameTestHelper helper) {
        Planet uranus = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("uranus")));
        helper.assertTrue(uranus != null, "Uranus is not a planet");
        helper.assertTrue(uranus.tier() == 7, "Uranus should be tier 7, is " + uranus.tier());
        helper.assertTrue(uranus.orbit().isPresent(), "Uranus has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("azurane_ore")), "No placed feature azurane_ore");
        helper.succeed();
    }

    private UranusSystemGameTests() {
    }
}
