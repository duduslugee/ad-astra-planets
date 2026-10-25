package com.adastraplanets.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The great socket of the Last Door: like a crystal socket, but it wants its system's Light Core (the glow's core
 * keys, an item tag) instead of a shard.
 */
public class CoreSocketBlock extends CrystalSocketBlock {
    public CoreSocketBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected TagKey<Item> keys(BlockState state) {
        return state.getValue(AncientGlow.PROPERTY).coreKeys();
    }
}
