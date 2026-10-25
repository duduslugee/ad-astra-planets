package com.adastraplanets.environment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

/**
 * What one piece of environmental liner protects against, from the {@code adastraplanets:liner_protection} item data
 * map ({@code data/<namespace>/data_maps/item/liner_protection.json}):
 *
 * <pre>
 * { "values": { "adastraplanets:liner_hood_1": { "thermal": 1, "pressure": 1 } } }
 * </pre>
 *
 * A worn set protects at the level of its weakest piece; a missing piece counts as zero.
 */
public record LinerProtection(int thermal, int pressure) {
    public static final LinerProtection NONE = new LinerProtection(0, 0);

    public static final Codec<LinerProtection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("thermal").forGetter(LinerProtection::thermal),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("pressure").forGetter(LinerProtection::pressure)
    ).apply(instance, LinerProtection::new));

    /** The protection of a whole set: each kind at its weakest piece. */
    public static LinerProtection weakest(Iterable<LinerProtection> pieces) {
        int thermal = Integer.MAX_VALUE;
        int pressure = Integer.MAX_VALUE;
        boolean any = false;
        for (LinerProtection piece : pieces) {
            any = true;
            thermal = Math.min(thermal, piece.thermal());
            pressure = Math.min(pressure, piece.pressure());
        }
        return any ? new LinerProtection(thermal, pressure) : NONE;
    }
}
