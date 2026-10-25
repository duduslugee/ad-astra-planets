package com.adastraplanets.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** An ancient block whose runes glow in its system's crystal colour (the glow property, see {@link AncientGlow}). */
public class GlowingAncientBlock extends Block {
    public GlowingAncientBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AncientGlow.PROPERTY, AncientGlow.AMBER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AncientGlow.PROPERTY);
    }
}
