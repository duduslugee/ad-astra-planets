package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.rocket.RocketDefinition;
import com.adastraplanets.rocket.RocketDefinitions;
import com.adastraplanets.rocket.TieredRocket;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.common_storage_lib.resources.fluid.FluidResource;
import earth.terrarium.common_storage_lib.resources.fluid.util.FluidAmounts;
import earth.terrarium.common_storage_lib.storage.base.CommonStorage;
import com.adastraplanets.api.event.RocketLaunchEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Checks that the shipped datapack content wires the Tier 5 rocket into Ad Astra correctly. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RocketGameTests {
    private static final String EMPTY = "empty";

    @GameTest(template = EMPTY)
    public static void tier5DefinitionIsLoaded(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.TIER_5_ROCKET.get());
        helper.assertTrue(definition != null, "tier_5_rocket has no rocket definition");
        TieredRocket rocket = helper.spawn(ModEntityTypes.TIER_5_ROCKET.get(), BlockPos.ZERO.above());
        helper.assertTrue(rocket.tier() == definition.tier(), "rocket tier does not follow its definition");
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void tier8NeedsTheHugePad(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.TIER_8_ROCKET.get());
        helper.assertTrue(definition != null, "tier_8_rocket has no rocket definition");
        helper.assertTrue(definition.tier() == 8, "tier_8_rocket should be tier 8, is " + definition.tier());
        helper.assertTrue(definition.launchPads().location().equals(com.adastraplanets.AdAstraPlanets.id("huge_launch_pads")),
                "tier_8_rocket should launch only from the huge pad, uses #" + definition.launchPads().location());
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void tier9NeedsTheHugePad(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.TIER_9_ROCKET.get());
        helper.assertTrue(definition != null, "tier_9_rocket has no rocket definition");
        helper.assertTrue(definition.tier() == 9, "tier_9_rocket should be tier 9, is " + definition.tier());
        helper.assertTrue(definition.launchPads().location().equals(com.adastraplanets.AdAstraPlanets.id("huge_launch_pads")),
                "tier_9_rocket should launch only from the huge pad, uses #" + definition.launchPads().location());
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void tier10NeedsTheHugePad(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.TIER_10_ROCKET.get());
        helper.assertTrue(definition != null, "tier_10_rocket has no rocket definition");
        helper.assertTrue(definition.tier() == 10, "tier_10_rocket should be tier 10, is " + definition.tier());
        helper.assertTrue(definition.launchPads().location().equals(com.adastraplanets.AdAstraPlanets.id("huge_launch_pads")),
                "tier_10_rocket should launch only from the huge pad, uses #" + definition.launchPads().location());
        helper.succeed();
    }

    /** The Ancients' Ship goes anywhere and launches with an empty tank. */
    @GameTest(template = EMPTY)
    public static void ancientShipNeedsNoFuel(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.ANCIENT_SHIP.get());
        helper.assertTrue(definition != null, "ancient_ship has no rocket definition");
        helper.assertTrue(definition.tier() >= 10, "ancient_ship should reach every body, is tier " + definition.tier());
        TieredRocket ship = helper.spawn(ModEntityTypes.ANCIENT_SHIP.get(), BlockPos.ZERO.above());
        helper.assertTrue(ship.hasEnoughFuel(), "ancient_ship should launch with an empty tank");
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void tier5TankFollowsDefinition(GameTestHelper helper) {
        RocketDefinition definition = RocketDefinitions.get(ModEntityTypes.TIER_5_ROCKET.get());
        helper.assertTrue(definition != null, "tier_5_rocket has no rocket definition");
        TieredRocket rocket = helper.spawn(ModEntityTypes.TIER_5_ROCKET.get(), BlockPos.ZERO.above());
        CommonStorage<FluidResource> tank = rocket.fluidContainer();
        long capacity = FluidAmounts.toPlatformAmount(definition.fuelCapacity());

        long water = tank.insert(FluidResource.of(Fluids.WATER), capacity, false);
        helper.assertTrue(water == 0, "tank accepted water");

        long fuel = tank.insert(FluidResource.of(ModFluids.FUEL.get()), capacity * 2, false);
        helper.assertTrue(fuel == capacity, "tank should hold exactly " + capacity + " but took " + fuel);
        helper.assertTrue(rocket.consumeFuel(true), "a full tank should be enough to launch");
        helper.succeed();
    }

    /** The pilot's eyes must sit inside the window (model y 40..50 px). */
    @GameTest(template = EMPTY)
    public static void tier5PilotLooksThroughWindow(GameTestHelper helper) {
        TieredRocket rocket = helper.spawn(ModEntityTypes.TIER_5_ROCKET.get(), BlockPos.ZERO.above());
        Player pilot = helper.makeMockPlayer(GameType.SURVIVAL);
        pilot.startRiding(rocket, true);
        rocket.positionRider(pilot);
        double eye = pilot.getEyeY() - rocket.getY();
        AdAstraPlanets.LOGGER.info("Tier 5 pilot eye height above rocket origin: {} blocks ({} px)", eye, eye * 16);
        helper.assertTrue(eye > 40 / 16.0 && eye < 50 / 16.0, "pilot eye at " + eye * 16 + " px, window spans 40..50 px");
        helper.succeed();
    }

    /** The whole pilot must fit inside the cabin lining (model y 19..54 px, half-width 8.5 px). */
    @GameTest(template = EMPTY)
    public static void tier5PilotFitsInCabin(GameTestHelper helper) {
        TieredRocket rocket = helper.spawn(ModEntityTypes.TIER_5_ROCKET.get(), BlockPos.ZERO.above());
        Player pilot = helper.makeMockPlayer(GameType.SURVIVAL);
        pilot.startRiding(rocket, true);
        rocket.positionRider(pilot);
        var box = pilot.getBoundingBox().move(-rocket.getX(), -rocket.getY(), -rocket.getZ());
        AdAstraPlanets.LOGGER.info("Tier 5 pilot spans y {}..{} px", box.minY * 16, box.maxY * 16);
        helper.assertTrue(box.minY >= 19 / 16.0 - 0.01, "pilot's feet below the cabin floor: " + box.minY * 16 + " px");
        helper.assertTrue(box.maxY <= 54 / 16.0, "pilot's head above the cabin ceiling: " + box.maxY * 16 + " px");
        helper.assertTrue(box.maxX <= 8.5 / 16.0 && box.minX >= -8.5 / 16.0, "pilot wider than the cabin");
        helper.succeed();
    }

    /** Vetoes every launch while registered. */
    public static final class LaunchVeto {
        @SubscribeEvent
        public void onLaunch(RocketLaunchEvent event) {
            event.setCanceled(true);
        }
    }

    @GameTest(template = EMPTY)
    public static void launchEventCanStopTheCountdown(GameTestHelper helper) {
        TieredRocket rocket = helper.spawn(ModEntityTypes.TIER_5_ROCKET.get(), BlockPos.ZERO.above());
        LaunchVeto veto = new LaunchVeto();
        NeoForge.EVENT_BUS.register(veto);
        try {
            rocket.initiateLaunchSequence();
            helper.assertTrue(!rocket.isLaunching(), "a cancelled RocketLaunchEvent must keep the rocket on the ground");
        } finally {
            NeoForge.EVENT_BUS.unregister(veto);
        }
        rocket.initiateLaunchSequence();
        // the veto is gone, but the rocket waits out the one-second cool-down before asking again
        helper.assertTrue(!rocket.isLaunching(), "a denied rocket should not retry within a second");
        helper.runAfterDelay(21, () -> {
            rocket.initiateLaunchSequence();
            helper.assertTrue(rocket.isLaunching(), "without a veto the countdown should start");
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY)
    public static void tier5RecipeIsRegistered(GameTestHelper helper) {
        var recipe = helper.getLevel().getRecipeManager().byKey(AdAstraPlanets.id("advanced_nasa_workbench/tier_5_rocket"));
        helper.assertTrue(recipe.isPresent(), "Tier 5 Advanced NASA Workbench recipe missing");
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void workbenchesBuildTheirOwnTiers(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        helper.assertTrue(recipes.byKey(AdAstraPlanets.id("stellar_nasa_workbench/tier_10_rocket")).isPresent(),
                "Tier 10 Stellar NASA Workbench recipe missing");
        helper.assertTrue(recipes.getAllRecipesFor(com.adastraplanets.workbench.Workbench.ADVANCED.recipeType()).size() == 3,
                "the Advanced NASA Workbench should build Tiers 5 to 7");
        helper.assertTrue(com.adastraplanets.workbench.Workbench.STELLAR.recipeTypes().size() == 3,
                "the Stellar NASA Workbench should also build the Advanced one's rockets and Ad Astra's");
        helper.setBlock(BlockPos.ZERO.above(), com.adastraplanets.registry.ModBlocks.STELLAR_NASA_WORKBENCH.get());
        helper.assertTrue(helper.getBlockEntity(BlockPos.ZERO.above()) instanceof com.adastraplanets.workbench.WorkbenchBlockEntity,
                "the Stellar NASA Workbench has no workbench block entity");
        helper.succeed();
    }

    @GameTest(template = EMPTY)
    public static void helioniteOreFeatureIsRegistered(GameTestHelper helper) {
        var key = ResourceKey.create(Registries.PLACED_FEATURE, AdAstraPlanets.id("helionite_ore"));
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE).containsKey(key),
                "Helionite placed feature missing");
        helper.succeed();
    }

    private RocketGameTests() {
    }
}
