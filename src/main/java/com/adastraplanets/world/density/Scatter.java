package com.adastraplanets.world.density;

/**
 * Round landforms (craters, volcanoes) scattered over a grid of cells, at most one per cell so they rarely overlap.
 * Positions and sizes come from a hash of the cell and a per-world salt, so they are stable and cheap to evaluate.
 */
final class Scatter {
    /** A landform's height in blocks, given the distance from its centre (in radii) and its radius (in blocks). */
    @FunctionalInterface
    interface Profile {
        double height(double distance, double radius);
    }

    /** Sum of every nearby landform's height at (x, z), in blocks. */
    static double sum(int x, int z, long salt, int cellSize, float chance, float minRadius, float maxRadius, Profile profile) {
        int cellX = Math.floorDiv(x, cellSize);
        int cellZ = Math.floorDiv(z, cellSize);
        double total = 0;
        for (int i = cellX - 1; i <= cellX + 1; i++) {
            for (int j = cellZ - 1; j <= cellZ + 1; j++) {
                long hash = mix(salt ^ (i * 341873128712L + j * 132897987541L));
                if (unit(hash) >= chance) {
                    continue;
                }
                hash = mix(hash);
                double centreX = (i + 0.15 + unit(hash) * 0.7) * cellSize;
                hash = mix(hash);
                double centreZ = (j + 0.15 + unit(hash) * 0.7) * cellSize;
                hash = mix(hash);
                // small ones are more common than large ones
                double size = unit(hash);
                double radius = minRadius + (maxRadius - minRadius) * size * size;
                double distance = Math.sqrt((x - centreX) * (x - centreX) + (z - centreZ) * (z - centreZ)) / radius;
                total += profile.height(distance, radius);
            }
        }
        return total;
    }

    /** A per-world salt, read from a seeded noise (the only way the world seed reaches a density function). */
    static long salt(net.minecraft.world.level.levelgen.DensityFunction.NoiseHolder seeded) {
        return seeded.noise() == null ? 0L : Double.doubleToLongBits(seeded.getValue(0.123, 0, 0.456));
    }

    static long mix(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    static double unit(long hash) {
        return (hash >>> 11) * 0x1.0p-53;
    }

    private Scatter() {
    }
}
