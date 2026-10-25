package com.adastraplanets.item;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModDataMaps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/**
 * The protection of a piece of this mod's armor, from the {@code adastraplanets:armor_stats} item data map
 * ({@code data/<namespace>/data_maps/item/armor_stats.json}), so a datapack sets it rather than the code:
 *
 * <pre>
 * { "values": { "adastraplanets:ancient_helmet": { "armor": 3, "toughness": 3, "knockback_resistance": 0.1 } } }
 * </pre>
 */
public record ArmorStats(double armor, double toughness, double knockbackResistance) {
    public static final Codec<ArmorStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("armor", 0.0).forGetter(ArmorStats::armor),
            Codec.DOUBLE.optionalFieldOf("toughness", 0.0).forGetter(ArmorStats::toughness),
            Codec.DOUBLE.optionalFieldOf("knockback_resistance", 0.0).forGetter(ArmorStats::knockbackResistance)
    ).apply(instance, ArmorStats::new));

    /** Adds a worn piece's stats to its attribute modifiers. */
    public static void apply(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ArmorItem armor)) {
            return;
        }
        ArmorStats stats = stack.getItemHolder().getData(ModDataMaps.ARMOR_STATS);
        if (stats == null) {
            return;
        }
        EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(armor.getEquipmentSlot());
        String piece = armor.getType().getName();
        if (stats.armor() != 0) {
            event.addModifier(Attributes.ARMOR, modifier("armor." + piece, stats.armor()), slot);
        }
        if (stats.toughness() != 0) {
            event.addModifier(Attributes.ARMOR_TOUGHNESS, modifier("toughness." + piece, stats.toughness()), slot);
        }
        if (stats.knockbackResistance() != 0) {
            event.addModifier(Attributes.KNOCKBACK_RESISTANCE, modifier("knockback_resistance." + piece, stats.knockbackResistance()), slot);
        }
    }

    private static AttributeModifier modifier(String name, double amount) {
        ResourceLocation id = AdAstraPlanets.id("armor_stats." + name);
        return new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE);
    }
}
