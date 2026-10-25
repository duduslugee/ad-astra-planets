package com.adastraplanets.compat.curios;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.item.ArmorStats;
import com.adastraplanets.registry.ModDataMaps;
import com.adastraplanets.registry.ModSuit;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * The Ancients' Suit worn in the liner's Curios slots ({@code data/adastraplanets/curios/}), so the armor slots stay free
 * for other mods' armor: each piece gives its armor_stats there too. Only loaded when Curios is installed.
 */
public final class CuriosSuit {
    public static void register() {
        ICurioItem piece = new ICurioItem() {
            @Override
            public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slot, ResourceLocation id, ItemStack stack) {
                Multimap<Holder<Attribute>, AttributeModifier> modifiers = LinkedHashMultimap.create();
                ArmorStats stats = stack.getItemHolder().getData(ModDataMaps.ARMOR_STATS);
                if (stats == null) {
                    return modifiers;
                }
                if (stats.armor() != 0) {
                    modifiers.put(Attributes.ARMOR, new AttributeModifier(id.withSuffix(".armor"), stats.armor(), AttributeModifier.Operation.ADD_VALUE));
                }
                if (stats.toughness() != 0) {
                    modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id.withSuffix(".toughness"), stats.toughness(), AttributeModifier.Operation.ADD_VALUE));
                }
                if (stats.knockbackResistance() != 0) {
                    modifiers.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id.withSuffix(".knockback_resistance"),
                            stats.knockbackResistance(), AttributeModifier.Operation.ADD_VALUE));
                }
                return modifiers;
            }
        };
        ModSuit.PIECES.forEach(item -> CuriosApi.registerCurio(item.get(), piece));
    }

    /** The stack in a Curios slot (empty when the entity has no such slot). */
    public static ItemStack worn(LivingEntity entity, String slot) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.getStacksHandler(slot))
                .filter(handler -> handler.getSlots() > 0)
                .map(handler -> handler.getStacks().getStackInSlot(0))
                .orElse(ItemStack.EMPTY);
    }

    static ResourceLocation id(String path) {
        return AdAstraPlanets.id(path);
    }

    private CuriosSuit() {
    }
}
