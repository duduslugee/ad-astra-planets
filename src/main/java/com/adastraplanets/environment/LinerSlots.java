package com.adastraplanets.environment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Optional;

/**
 * The four liner slots, which Curios provides ({@code data/adastraplanets/curios/}). Without Curios the slots do not
 * exist, so the liner system switches itself off instead of making every planet deadly.
 */
public final class LinerSlots {
    public static final List<String> SLOTS = List.of("liner_head", "liner_chest", "liner_legs", "liner_feet");

    private static final boolean CURIOS = ModList.get().isLoaded("curios");

    public static boolean available() {
        return CURIOS;
    }

    /** The stack in each liner slot, in {@link #SLOTS} order; empty when Curios is missing or the entity has no slots. */
    public static Optional<List<ItemStack>> worn(LivingEntity entity) {
        // kept behind the check so Curios classes are never touched without Curios
        return CURIOS ? com.adastraplanets.compat.curios.CuriosLinerSlots.worn(entity, SLOTS) : Optional.empty();
    }

    private LinerSlots() {
    }
}
