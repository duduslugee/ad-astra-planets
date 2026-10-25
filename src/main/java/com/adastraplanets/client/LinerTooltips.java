package com.adastraplanets.client;

import com.adastraplanets.environment.LinerProtection;
import com.adastraplanets.registry.ModDataMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Shows what a liner piece protects against, read from the (synced) liner_protection data map. */
public final class LinerTooltips {
    public static void add(ItemTooltipEvent event) {
        LinerProtection protection = event.getItemStack().getItemHolder().getData(ModDataMaps.LINER_PROTECTION);
        if (protection == null) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.adastraplanets.liner.thermal", protection.thermal()).withStyle(ChatFormatting.GOLD));
        event.getToolTip().add(Component.translatable("tooltip.adastraplanets.liner.pressure", protection.pressure()).withStyle(ChatFormatting.AQUA));
        event.getToolTip().add(Component.translatable("tooltip.adastraplanets.liner.set").withStyle(ChatFormatting.GRAY));
    }

    private LinerTooltips() {
    }
}
