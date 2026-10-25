package com.adastraplanets.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Reads the liner slots through Curios; only loaded when Curios is installed (see LinerSlots). */
public final class CuriosLinerSlots {
    public static Optional<List<ItemStack>> worn(LivingEntity entity, List<String> slots) {
        return CuriosApi.getCuriosInventory(entity).map(inventory -> {
            List<ItemStack> stacks = new ArrayList<>(slots.size());
            for (String slot : slots) {
                stacks.add(inventory.getStacksHandler(slot)
                        .filter(handler -> handler.getSlots() > 0)
                        .map(handler -> handler.getStacks().getStackInSlot(0))
                        .orElse(ItemStack.EMPTY));
            }
            return stacks;
        });
    }

    private CuriosLinerSlots() {
    }
}
