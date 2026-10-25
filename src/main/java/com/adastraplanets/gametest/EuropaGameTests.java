package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModDataMaps;
import com.adastraplanets.world.GeyserSettings;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Europa's data (planet entry, geyser settings) and its code-driven pieces (geysers, hydrothermal vents). */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EuropaGameTests {
    @GameTest(template = "empty")
    public static void europaIsATier5Destination(GameTestHelper helper) {
        Planet europa = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id("europa")));
        helper.assertTrue(europa != null, "Europa is not a planet");
        helper.assertTrue(europa.tier() == 5, "Europa should be tier 5, is " + europa.tier());
        helper.assertTrue(europa.solarSystem().equals(ResourceLocation.fromNamespaceAndPath("ad_astra", "solar_system")),
                "Europa should be in the Solar System");
        helper.assertTrue(europa.orbit().isPresent(), "Europa has no orbit for space stations");
        helper.succeed();
    }

    /** An armour stand on a geyser is thrown upward once the geyser erupts. */
    @GameTest(template = "empty_tall", timeoutTicks = 400)
    public static void geyserThrowsEntitiesUp(GameTestHelper helper) {
        BlockPos geyser = new BlockPos(4, 1, 4);
        helper.setBlock(geyser, ModBlocks.GEYSER.get());
        GeyserSettings settings = ModBlocks.GEYSER.get().builtInRegistryHolder().getData(ModDataMaps.GEYSERS);
        helper.assertTrue(settings != null, "The geyser has no entry in the geysers data map");
        ArmorStand stand = helper.spawn(EntityType.ARMOR_STAND, geyser.above());
        double start = stand.getY();
        helper.succeedWhen(() -> helper.assertTrue(stand.getY() > start + 3, "Not thrown up yet"));
    }

    /** The configured vent, run in a flooded pit, builds a chimney with a vent on top and Abyssium around its base. */
    @GameTest(template = "empty_tall")
    public static void hydrothermalVentGrowsAbyssium(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(0, 0, 0, 8, 15, 8)) {
            helper.setBlock(pos, pos.getY() <= 3 ? ModBlocks.ABYSSAL_ROCK.get() : Blocks.WATER);
        }
        ConfiguredFeature<?, ?> vent = helper.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .get(AdAstraPlanets.id("hydrothermal_vent"));
        helper.assertTrue(vent != null, "No hydrothermal_vent configured feature");
        boolean placed = vent.place(helper.getLevel(), helper.getLevel().getChunkSource().getGenerator(), helper.getLevel().getRandom(),
                helper.absolutePos(new BlockPos(4, 10, 4)));
        helper.assertTrue(placed, "The vent was not placed");

        int vents = 0;
        int ore = 0;
        for (BlockPos pos : BlockPos.betweenClosed(0, 0, 0, 8, 15, 8)) {
            var state = helper.getBlockState(pos);
            if (state.is(ModBlocks.HYDROTHERMAL_VENT.get())) {
                vents++;
            } else if (state.is(ModBlocks.ABYSSIUM_ORE.get())) {
                ore++;
            }
        }
        helper.assertTrue(vents == 1, "Expected one vent mouth, found " + vents);
        helper.assertTrue(ore >= 2 && ore <= 4, "Expected 2 to 4 Abyssium ores around the vent, found " + ore);
        helper.succeed();
    }

    /** The configured geyser, run on a flat ice floor, sets the geyser into it and frosts the ice around. */
    @GameTest(template = "empty_tall")
    public static void geyserFeatureFrostsTheIce(GameTestHelper helper) {
        for (BlockPos pos : BlockPos.betweenClosed(0, 0, 0, 8, 1, 8)) {
            helper.setBlock(pos, ModBlocks.CRUST_ICE.get());
        }
        ConfiguredFeature<?, ?> geyser = helper.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .get(AdAstraPlanets.id("geyser"));
        helper.assertTrue(geyser != null, "No geyser configured feature");
        boolean placed = geyser.place(helper.getLevel(), helper.getLevel().getChunkSource().getGenerator(), helper.getLevel().getRandom(),
                helper.absolutePos(new BlockPos(4, 1, 4)));
        helper.assertTrue(placed, "The geyser was not placed");
        helper.assertBlockPresent(ModBlocks.GEYSER.get(), new BlockPos(4, 1, 4));
        int frost = 0;
        for (BlockPos pos : BlockPos.betweenClosed(0, 1, 0, 8, 1, 8)) {
            if (helper.getBlockState(pos).is(ModBlocks.RIME.get())) {
                frost++;
            }
        }
        helper.assertTrue(frost > 0, "No frost around the geyser");
        helper.succeed();
    }

    private EuropaGameTests() {
    }
}
