package com.adastraplanets.block;

import com.adastraplanets.block.entity.GeyserBlockEntity;
import com.adastraplanets.registry.ModBlockEntityTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** A vent in the ice that erupts on a cycle and throws whatever stands over it upward (see GeyserBlockEntity). */
public class GeyserBlock extends BaseEntityBlock {
    public static final MapCodec<GeyserBlock> CODEC = simpleCodec(GeyserBlock::new);

    public GeyserBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeyserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.GEYSER.get(),
                level.isClientSide() ? GeyserBlockEntity::clientTick : GeyserBlockEntity::serverTick);
    }
}
