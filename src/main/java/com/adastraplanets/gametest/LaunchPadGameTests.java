package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.LargeLaunchPadBlock;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.registry.ModItems;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.blocks.properties.LaunchPadPartProperty;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The 5x5 and 7x7 launch pads: structure, compatibility with every rocket, and teardown. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LaunchPadGameTests {
    private static final String TEMPLATE = "empty_large";
    private static final BlockPos CENTRE = new BlockPos(3, 1, 3);

    private static BlockPos placePad(GameTestHelper helper) {
        BlockPos centre = helper.absolutePos(CENTRE);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                helper.getLevel().setBlock(centre.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }
        ModBlocks.LARGE_LAUNCH_PAD.get().placeParts(helper.getLevel(), centre, ModBlocks.LARGE_LAUNCH_PAD.get().defaultBlockState());
        return centre;
    }

    private static InteractionResult useOn(GameTestHelper helper, Item item, BlockPos absolute) {
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        ItemStack stack = new ItemStack(item);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
        return stack.useOn(new UseOnContext(helper.getLevel(), player, InteractionHand.MAIN_HAND, stack, hit));
    }

    @GameTest(template = TEMPLATE)
    public static void largePadFillsFiveByFive(GameTestHelper helper) {
        BlockPos centre = placePad(helper);
        int parts = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                var state = helper.getLevel().getBlockState(centre.offset(dx, 0, dz));
                helper.assertTrue(state.is(ModBlocks.LARGE_LAUNCH_PAD.get()), "missing pad part at " + dx + "," + dz);
                boolean middle = dx == 0 && dz == 0;
                helper.assertTrue((state.getValue(LaunchPadBlock.PART) == LaunchPadPartProperty.CENTER) == middle,
                        "only the middle part may be Ad Astra's CENTER");
                parts++;
            }
        }
        helper.assertTrue(parts == 25, "expected 25 parts");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void tier5RocketStaysOnLargePad(GameTestHelper helper) {
        BlockPos centre = placePad(helper);
        helper.assertTrue(useOn(helper, ModItems.TIER_5_ROCKET.get(), centre).consumesAction(), "tier 5 rocket was not placed");
        helper.runAfterDelay(20, () -> {
            // a rocket's collision parts share its entity type: search by class, for the rocket itself
            var rockets = helper.getLevel().getEntitiesOfClass(com.adastraplanets.rocket.TieredRocket.class, new AABB(centre).inflate(2),
                    rocket -> rocket.getType() == ModEntityTypes.TIER_5_ROCKET.get());
            helper.assertTrue(rockets.size() == 1, "tier 5 rocket should still be on the pad");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void adAstraRocketWorksOnLargePad(GameTestHelper helper) {
        BlockPos centre = placePad(helper);
        Item tier4 = earth.terrarium.adastra.common.registry.ModItems.TIER_4_ROCKET.get();
        helper.assertTrue(useOn(helper, tier4, centre).consumesAction(), "Ad Astra's tier 4 rocket was not placed");
        helper.runAfterDelay(20, () -> {
            var rockets = helper.getLevel().getEntitiesOfClass(Rocket.class, new AABB(centre).inflate(2));
            helper.assertTrue(rockets.size() == 1, "Ad Astra's rocket should still be on the pad");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void breakingAnyPartRemovesThePad(GameTestHelper helper) {
        BlockPos centre = placePad(helper);
        helper.getLevel().destroyBlock(centre.offset(-2, 0, 2), false);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                helper.assertTrue(!helper.getLevel().getBlockState(centre.offset(dx, 0, dz)).is(ModBlocks.LARGE_LAUNCH_PAD.get()),
                        "pad part left behind at " + dx + "," + dz);
            }
        }
        helper.succeed();
    }

    /** The 7x7 pad fills 49 parts with one CENTER, and also counts as a large pad (it takes any smaller rocket). */
    @GameTest(template = TEMPLATE)
    public static void hugePadFillsSevenBySeven(GameTestHelper helper) {
        var pad = ModBlocks.HUGE_LAUNCH_PAD.get();
        BlockPos centre = helper.absolutePos(CENTRE);
        pad.placeParts(helper.getLevel(), centre, pad.defaultBlockState());
        int parts = 0;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                var state = helper.getLevel().getBlockState(centre.offset(dx, 0, dz));
                helper.assertTrue(state.is(pad), "missing pad part at " + dx + "," + dz);
                helper.assertTrue((state.getValue(LaunchPadBlock.PART) == LaunchPadPartProperty.CENTER) == (dx == 0 && dz == 0),
                        "only the middle part may be Ad Astra's CENTER");
                parts++;
            }
        }
        helper.assertTrue(parts == 49, "expected 49 parts");
        helper.assertTrue(pad.defaultBlockState().is(com.adastraplanets.registry.ModTags.Blocks.LARGE_LAUNCH_PADS),
                "the huge pad should count as a large pad");
        helper.succeed();
    }

    private LaunchPadGameTests() {
    }
}
