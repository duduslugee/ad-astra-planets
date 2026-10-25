package com.adastraplanets.world;

import com.adastraplanets.registry.ModTags;
import earth.terrarium.adastra.api.events.AdAstraEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ad Astra freezes water on cold planets. In dimension types tagged {@code adastraplanets:insulated_oceans} water
 * that cannot see the sky (an ocean under an ice shell, a lake in a cave) stays liquid; water open to the sky still
 * freezes as usual.
 */
public final class InsulatedOceans {
    public static void register() {
        AdAstraEvents.EnvironmentTickEvent.register(InsulatedOceans::tick);
    }

    private static boolean tick(ServerLevel level, BlockPos pos, BlockState state, short temperature) {
        return state.getFluidState().isEmpty()
                || !level.dimensionTypeRegistration().is(ModTags.DimensionTypes.INSULATED_OCEANS)
                || level.canSeeSky(pos.above());
    }

    private InsulatedOceans() {
    }
}
