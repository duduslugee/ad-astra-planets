package com.adastraplanets.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * A system's Light Core. Recipes use it as a catalyst: it comes back out of the crafting grid, so the same core can
 * still open the Last Door. Set into a core socket, it is spent.
 */
public class LightCoreItem extends Item {
    public LightCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copyWithCount(1);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.adastraplanets.light_core").withStyle(ChatFormatting.GRAY));
    }
}
