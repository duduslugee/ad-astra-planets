package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModDataMaps;
import com.adastraplanets.world.GeyserSettings;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Jupiter system's moons beyond Europa: destinations, ores and their own features. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class JupiterSystemGameTests {
    @GameTest(template = "empty")
    public static void ioIsATier5Destination(GameTestHelper helper) {
        Planet io = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("io")));
        helper.assertTrue(io != null, "Io is not a planet");
        helper.assertTrue(io.tier() == 5, "Io should be tier 5, is " + io.tier());
        helper.assertTrue(io.orbit().isPresent(), "Io has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("igneum_ore")), "No placed feature igneum_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ganymedeIsATier5Destination(GameTestHelper helper) {
        Planet ganymede = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("ganymede")));
        helper.assertTrue(ganymede != null, "Ganymede is not a planet");
        helper.assertTrue(ganymede.tier() == 5, "Ganymede should be tier 5, is " + ganymede.tier());
        helper.assertTrue(ganymede.orbit().isPresent(), "Ganymede has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("aurorium_ore")), "No placed feature aurorium_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void callistoIsATier5Destination(GameTestHelper helper) {
        Planet callisto = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("callisto")));
        helper.assertTrue(callisto != null, "Callisto is not a planet");
        helper.assertTrue(callisto.tier() == 5, "Callisto should be tier 5, is " + callisto.tier());
        helper.assertTrue(callisto.orbit().isPresent(), "Callisto has no orbit");
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE)
                .containsKey(AdAstraPlanets.id("eonyx_ore")), "No placed feature eonyx_ore");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void jupiterIsALandableGasGiant(GameTestHelper helper) {
        Planet jupiter = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("jupiter")));
        helper.assertTrue(jupiter != null, "Jupiter is not a planet");
        helper.assertTrue(jupiter.tier() == 5, "Jupiter should be tier 5, is " + jupiter.tier());
        var requirement = com.adastraplanets.environment.Environments.requirement(jupiter.dimension());
        helper.assertTrue(requirement.pressure() == 3 && requirement.pressureKind() == com.adastraplanets.environment.EnvironmentRequirement.PressureKind.HIGH,
                "Jupiter should demand level III against high pressure");
        var placed = helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
        helper.assertTrue(placed.containsKey(AdAstraPlanets.id("stormium_ore")), "No placed feature stormium_ore");
        helper.assertTrue(placed.containsKey(AdAstraPlanets.id("jupiter_step_slabs")), "No placed feature jupiter_step_slabs");
        // pure void under the islands: landings there must be moved onto ground
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DIMENSION_TYPE, AdAstraPlanets.id("jupiter")))
                .is(com.adastraplanets.registry.ModTags.DimensionTypes.VOID_WORLDS), "Jupiter should be tagged as a void world");
        helper.succeed();
    }

    /** Sulfur vents erupt like geysers but with their own particles, read from the geyser data map. */
    @GameTest(template = "empty")
    public static void sulfurVentsHaveTheirOwnPlume(GameTestHelper helper) {
        GeyserSettings vent = ModBlocks.SULFUR_VENT.get().builtInRegistryHolder().getData(ModDataMaps.GEYSERS);
        helper.assertTrue(vent != null, "The sulfur vent has no entry in the geysers data map");
        helper.assertTrue(vent.particle().getType() == ParticleTypes.DUST, "The sulfur plume should be dust, is " + vent.particle().getType());
        GeyserSettings geyser = ModBlocks.GEYSER.get().builtInRegistryHolder().getData(ModDataMaps.GEYSERS);
        helper.assertTrue(geyser != null && geyser.particle() == ParticleTypes.CLOUD, "Europa's geyser should keep its default cloud");
        helper.succeed();
    }

    private JupiterSystemGameTests() {
    }
}
