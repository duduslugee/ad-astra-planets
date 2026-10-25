package com.adastraplanets.registry;

import com.adastraplanets.block.AccessReaderBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/** What the lost human expedition left in its bases: the access readers on their doors and the cards that open them. */
public final class ModExpedition {
    public static final DeferredBlock<AccessReaderBlock> ACCESS_READER = ModBlocks.BLOCKS.register("access_reader",
            () -> new AccessReaderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(-1.0F, 3600000.0F).noLootTable()
                    .isRedstoneConductor((state, level, pos) -> false)
                    .lightLevel(state -> state.getValue(AccessReaderBlock.GRANTED) ? 7 : 3)));
    public static final DeferredItem<BlockItem> ACCESS_READER_ITEM = ModItems.ITEMS.registerSimpleBlockItem(ACCESS_READER);
    public static final DeferredItem<Item> ACCESS_CARD = ModItems.ITEMS.registerItem("access_card",
            properties -> new Item(properties.stacksTo(1).rarity(Rarity.UNCOMMON)));

    /** Called from the mod constructor so everything is registered before the registers are attached. */
    public static void init() {
    }

    private ModExpedition() {
    }
}
