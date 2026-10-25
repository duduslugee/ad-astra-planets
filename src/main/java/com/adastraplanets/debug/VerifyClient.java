package com.adastraplanets.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** The client half of the structure check: creates a fresh world from the title screen, and closes the game after. */
public final class VerifyClient {
    private static boolean started;

    public static void init() {
        StructureVerifier.onFinished = () -> Minecraft.getInstance().execute(() -> Minecraft.getInstance().stop());
    }

    @SubscribeEvent
    public static void onTitle(ScreenEvent.Init.Post event) {
        if (started || !(event.getScreen() instanceof TitleScreen)) {
            return;
        }
        started = true;
        // the world must keep running while the window is in the background
        Minecraft.getInstance().options.pauseOnLostFocus = false;
        String name = "verify-" + System.currentTimeMillis();
        LevelSettings settings = new LevelSettings(name, GameType.CREATIVE, false, Difficulty.PEACEFUL, true, new GameRules(),
                WorldDataConfiguration.DEFAULT);
        Minecraft.getInstance().createWorldOpenFlows().createFreshLevel(name, settings, new WorldOptions(20261022L, false, false),
                WorldPresets::createNormalWorldDimensions, event.getScreen());
    }

    private VerifyClient() {
    }
}
