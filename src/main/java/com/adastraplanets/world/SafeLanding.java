package com.adastraplanets.world;

import com.adastraplanets.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * On worlds with no ground (dimension types tagged {@code adastraplanets:void_worlds}, such as Jupiter's floating
 * islands) a lander coming down where the rocket took off may find nothing but void. This moves the landing to the
 * nearest column that has ground, read from the terrain generator (no chunk needs to be loaded).
 */
public final class SafeLanding {
    /** How far (in blocks) to look for ground, and the step between columns tried. */
    private static final int SEARCH_RADIUS = 768;
    private static final int STEP = 8;

    public static Vec3 adjust(ServerLevel level, Vec3 pos) {
        if (!level.dimensionTypeRegistration().is(ModTags.DimensionTypes.VOID_WORLDS)) {
            return pos;
        }
        int x0 = (int) Math.floor(pos.x);
        int z0 = (int) Math.floor(pos.z);
        if (hasGround(level, x0, z0)) {
            return pos;
        }
        // rings of growing radius around the take-off point; the first column with ground wins
        for (int radius = STEP; radius <= SEARCH_RADIUS; radius += STEP) {
            for (int offset = -radius; offset <= radius; offset += STEP) {
                int[][] candidates = {{x0 + offset, z0 - radius}, {x0 + offset, z0 + radius}, {x0 - radius, z0 + offset}, {x0 + radius, z0 + offset}};
                for (int[] candidate : candidates) {
                    if (hasGround(level, candidate[0], candidate[1])) {
                        return new Vec3(candidate[0] + 0.5, pos.y, candidate[1] + 0.5);
                    }
                }
            }
        }
        return pos;
    }

    private static boolean hasGround(ServerLevel level, int x, int z) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        int height = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, level, level.getChunkSource().randomState());
        return height > level.getMinBuildHeight();
    }

    private SafeLanding() {
    }
}
