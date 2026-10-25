package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

/**
 * The Ancients' Suit, kept behind the Last Door: a full set needs neither oxygen nor an environmental liner (through
 * tags: Ad Astra's space-resistant and oxygen-supplying armor, this mod's environment-proof armor). Its protection
 * comes from the armor_stats data map; the material only gives it its look and sound.
 */
public final class ModSuit {
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, AdAstraPlanets.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANCIENT = MATERIALS.register("ancient", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                for (ArmorItem.Type type : ArmorItem.Type.values()) {
                    map.put(type, 0);
                }
            }), 0, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(AdAstraPlanets.id("ancient"))), 0.0F, 0.0F));

    public static final DeferredItem<ArmorItem> HELMET = piece("ancient_helmet", ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> CHESTPLATE = piece("ancient_chestplate", ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> LEGGINGS = piece("ancient_leggings", ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> BOOTS = piece("ancient_boots", ArmorItem.Type.BOOTS);

    public static final List<DeferredItem<ArmorItem>> PIECES = List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS);

    private static DeferredItem<ArmorItem> piece(String name, ArmorItem.Type type) {
        return ModItems.ITEMS.registerItem(name, properties -> new ArmorItem(ANCIENT, type, properties.stacksTo(1)
                .rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))) {
            /** One texture for the suit's own model, whichever layer the armor renderer asks for. */
            @Override
            public net.minecraft.resources.ResourceLocation getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity,
                                                                           EquipmentSlot slot, ArmorMaterial.Layer layer, boolean inner) {
                return AdAstraPlanets.id("textures/models/armor/ancient_suit.png");
            }

            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.translatable("tooltip.adastraplanets.ancient_suit").withStyle(ChatFormatting.GRAY));
            }
        });
    }

    /** The Curios slots the suit can also be worn in, one per piece, in armor order: the liner's slots. */
    public static final List<String> SLOTS = com.adastraplanets.environment.LinerSlots.SLOTS;
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    private static final boolean CURIOS = net.neoforged.fml.ModList.get().isLoaded("curios");

    /**
     * Whether every piece is an item of the tag, each worn either in its armor slot or in its liner slot of Curios (so
     * the armor slots can hold other mods' armor).
     */
    public static boolean wearsFullSet(LivingEntity entity, TagKey<Item> tag) {
        for (int i = 0; i < ARMOR_SLOTS.size(); i++) {
            if (entity.getItemBySlot(ARMOR_SLOTS.get(i)).is(tag)) {
                continue;
            }
            // kept behind the check so Curios classes are never touched without Curios
            if (CURIOS && com.adastraplanets.compat.curios.CuriosSuit.worn(entity, SLOTS.get(i)).is(tag)) {
                continue;
            }
            return false;
        }
        return true;
    }

    /** Whether the whole environment-proof suit is worn, in armor slots, liner slots or both. */
    public static boolean suited(LivingEntity entity) {
        return wearsFullSet(entity, ModTags.Items.ENVIRONMENT_PROOF_ARMOR);
    }

    /**
     * Ad Astra checks its suit tags on the armor slots only; these hooks extend its oxygen, heat, cold and acid rain
     * checks to a suit worn (partly) in the Curios slots.
     */
    public static void hookAdAstra() {
        earth.terrarium.adastra.api.events.AdAstraEvents.EntityOxygenEvent.register((entity, oxygen) ->
                oxygen || entity instanceof LivingEntity living && suited(living));
        earth.terrarium.adastra.api.events.AdAstraEvents.HotTemperatureTickEvent.register((level, entity) -> !suited(entity));
        earth.terrarium.adastra.api.events.AdAstraEvents.ColdTemperatureTickEvent.register((level, entity) -> !suited(entity));
        earth.terrarium.adastra.api.events.AdAstraEvents.AcidRainTickEvent.register((level, entity) -> !suited(entity));
    }

    /** Called from the mod constructor so every item is registered before the registers are attached. */
    public static void init() {
    }

    private ModSuit() {
    }
}
