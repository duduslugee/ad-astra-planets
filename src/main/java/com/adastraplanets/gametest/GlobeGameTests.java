package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.entity.GlobeBlockEntity;
import com.adastraplanets.registry.ModGlobes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** Every world has a globe, and a pushed globe turns, then slows to a stop. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GlobeGameTests {
    @GameTest(template = "empty")
    public static void everyWorldHasAGlobe(GameTestHelper helper) {
        for (String world : ModGlobes.BUILT_IN.keySet()) {
            helper.assertTrue(ModGlobes.blocks().containsKey(world + "_globe"), "No globe for " + world);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void pushedGlobeSpinsThenStops(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModGlobes.blocks().get("jupiter_globe").get());
        GlobeBlockEntity globe = helper.getBlockEntity(pos);
        globe.spin();
        helper.runAfterDelay(5, () -> {
            float turned = globe.yRot(1F);
            helper.assertTrue(turned != 0F, "the globe did not turn after a push");
            helper.runAfterDelay(120, () -> {
                float stopped = globe.yRot(1F);
                helper.runAfterDelay(5, () -> {
                    helper.assertTrue(globe.yRot(1F) == stopped, "the globe never stopped");
                    helper.succeed();
                });
            });
        });
    }

    private GlobeGameTests() {
    }
}
