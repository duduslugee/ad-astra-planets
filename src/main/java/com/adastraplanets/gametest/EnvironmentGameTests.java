package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.environment.EnvironmentHazards;
import com.adastraplanets.environment.EnvironmentRequirement;
import com.adastraplanets.environment.Environments;
import com.adastraplanets.environment.LinerProtection;
import com.adastraplanets.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/** Environmental liner: what places demand and what worn sets give. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EnvironmentGameTests {
    private static ResourceKey<Level> dimension(String namespace, String path) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    @GameTest(template = "empty")
    public static void planetsDemandLinerByTier(GameTestHelper helper) {
        helper.assertTrue(Environments.settings() != null, "No environment settings loaded");
        helper.assertTrue(Environments.requirement(Level.OVERWORLD).demandsNothing(), "Earth should demand nothing");
        helper.assertTrue(Environments.requirement(Level.NETHER).demandsNothing(), "The Nether is not a planet");
        EnvironmentRequirement moon = Environments.requirement(dimension("ad_astra", "moon"));
        helper.assertTrue(moon.thermal() == 1 && moon.pressure() == 1, "The Moon should demand level I (default)");
        EnvironmentRequirement venus = Environments.requirement(dimension("ad_astra", "venus"));
        helper.assertTrue(venus.pressure() == 2 && venus.pressureKind() == EnvironmentRequirement.PressureKind.HIGH,
                "Venus should demand level II against high pressure");
        EnvironmentRequirement europa = Environments.requirement(dimension(AdAstraPlanets.MOD_ID, "europa"));
        helper.assertTrue(europa.thermal() == 3 && europa.pressure() == 3, "Europa should demand level III");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void linerSetProtectsAtItsWeakestPiece(GameTestHelper helper) {
        List<ItemStack> full = ModItems.LINER_PIECES.stream().map(piece -> new ItemStack(ModItems.liner(piece, 3))).toList();
        LinerProtection protection = EnvironmentHazards.protection(full);
        helper.assertTrue(protection.thermal() == 3 && protection.pressure() == 3, "A level III set should give III, gives " + protection);

        List<ItemStack> mixed = List.of(new ItemStack(ModItems.liner("hood", 3)), new ItemStack(ModItems.liner("vest", 1)),
                new ItemStack(ModItems.liner("leggings", 3)), new ItemStack(ModItems.liner("boots", 3)));
        helper.assertTrue(EnvironmentHazards.protection(mixed).pressure() == 1, "A mixed set should give its weakest level");

        List<ItemStack> missing = List.of(new ItemStack(ModItems.liner("hood", 5)), ItemStack.EMPTY,
                new ItemStack(ModItems.liner("leggings", 5)), new ItemStack(ModItems.liner("boots", 5)));
        helper.assertTrue(EnvironmentHazards.protection(missing).thermal() == 0, "A set missing a piece should give nothing");
        helper.succeed();
    }

    private EnvironmentGameTests() {
    }
}
