package com.adastraplanets.compat;

import com.adastraplanets.AdAstraPlanets;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * C2ME's experimental OpenCL module compiles world generation for the GPU, but only Minecraft's own density functions:
 * on this mod's (craters, volcanoes, grooves...) it crashes the world on creation instead of falling back to the CPU,
 * unless its {@code allowIncompatibilityFallback} option is on. So when the module is installed with that option off,
 * loading stops with a message naming both fixes instead of a crash later. Nothing happens otherwise.
 */
public final class C2meOpenClCheck {
    private static final String MODULE = "c2me_opts_accel_opencl";
    private static final String MESSAGE = "C2ME's OpenCL acceleration module can't generate Ad Astra: Planets' worlds and "
            + "would crash on world creation. Either remove c2me-opts-accel-opencl from your mods folder, or open "
            + "config/c2me.toml and set allowIncompatibilityFallback = true (in its openclAccel section, keeping the "
            + "file's version line).";
    private static final Pattern FALLBACK_ON = Pattern.compile("(?m)^\\s*allowIncompatibilityFallback\\s*=\\s*true\\b");

    public static void check() {
        if (!ModList.get().isLoaded(MODULE)) {
            return;
        }
        if (fallbackEnabled()) {
            AdAstraPlanets.LOGGER.info("C2ME's OpenCL module is installed with allowIncompatibilityFallback on: "
                    + "this mod's worlds generate on the CPU");
            return;
        }
        // thrown during common setup, it stops loading on FML's error screen with this message (plain English: that
        // screen does not read mods' lang files); registries are already built by then, so nothing else breaks
        throw new IllegalStateException(MESSAGE);
    }

    /** Whether C2ME's config (config/c2me.toml) turns allowIncompatibilityFallback on. */
    private static boolean fallbackEnabled() {
        Path config = FMLPaths.CONFIGDIR.get().resolve("c2me.toml");
        try {
            return Files.isRegularFile(config) && FALLBACK_ON.matcher(Files.readString(config)).find();
        } catch (IOException exception) {
            AdAstraPlanets.LOGGER.warn("Could not read {}", config, exception);
            return false;
        }
    }

    private C2meOpenClCheck() {
    }
}
