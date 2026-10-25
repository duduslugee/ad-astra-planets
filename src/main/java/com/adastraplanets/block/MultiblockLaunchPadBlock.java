package com.adastraplanets.block;

import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.blocks.properties.LaunchPadPartProperty;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A square launch pad bigger than Ad Astra's 3x3 (5x5, 7x7...). Every part carries Ad Astra's {@link LaunchPadBlock#PART}
 * and {@link LaunchPadBlock#POWERED} properties, with {@code CENTER} on the middle block only, so every Ad Astra rocket
 * (and ours) can be placed on its centre, stays bound to it and launches from a redstone signal exactly as on Ad
 * Astra's pad. Each part renders its own 16x16 slice of the pad (block models cannot span more than 3 blocks).
 * Subclasses give the size and the two part-position properties (block state properties must be static).
 */
public abstract class MultiblockLaunchPadBlock extends Block implements SimpleWaterloggedBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    protected MultiblockLaunchPadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(LaunchPadBlock.POWERED, false)
                .setValue(LaunchPadBlock.PART, LaunchPadPartProperty.CENTER)
                .setValue(this.xProperty(), this.middle())
                .setValue(this.zProperty(), this.middle()));
    }

    /** Blocks along each side. */
    public abstract int size();

    /** The part's position along x, 0 to size - 1. */
    public abstract IntegerProperty xProperty();

    /** The part's position along z, 0 to size - 1. */
    public abstract IntegerProperty zProperty();

    /** The translation key of the description shown while Shift is held. */
    protected abstract String infoKey();

    public int middle() {
        return this.size() / 2;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED, LaunchPadBlock.POWERED, LaunchPadBlock.PART, this.xProperty(), this.zProperty());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /** Like Ad Astra's pad: "Hold Shift for more info", then the description. */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        TooltipUtils.addDescriptionComponent(tooltip, Component.translatable(this.infoKey()).withStyle(ChatFormatting.GRAY));
    }

    /** Placed by its centre: refuses if any spot of the pad is taken. */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos centre = context.getClickedPos();
        int middle = this.middle();
        for (int dx = -middle; dx <= middle; dx++) {
            for (int dz = -middle; dz <= middle; dz++) {
                BlockPos part = centre.offset(dx, 0, dz);
                if (!level.getBlockState(part).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(part)) {
                    return null;
                }
            }
        }
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED,
                level.getFluidState(centre).getType() == Fluids.WATER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        this.placeParts(level, pos, state);
    }

    /** Fills the pad around {@code centre}; only the middle block is Ad Astra's {@code CENTER} part. */
    public void placeParts(Level level, BlockPos centre, BlockState centreState) {
        int middle = this.middle();
        for (int x = 0; x < this.size(); x++) {
            for (int z = 0; z < this.size(); z++) {
                boolean isMiddle = x == middle && z == middle;
                BlockPos pos = centre.offset(x - middle, 0, z - middle);
                level.setBlock(pos, centreState.setValue(this.xProperty(), x).setValue(this.zProperty(), z)
                        .setValue(LaunchPadBlock.PART, isMiddle ? LaunchPadPartProperty.CENTER : LaunchPadPartProperty.TOP)
                        .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER), 3);
            }
        }
    }

    public BlockPos centreOf(BlockState state, BlockPos pos) {
        return pos.offset(this.middle() - state.getValue(this.xProperty()), 0, this.middle() - state.getValue(this.zProperty()));
    }

    /** Any part receiving redstone powers the centre, which is where rockets read it. */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moved) {
        if (level.isClientSide()) {
            return;
        }
        BlockPos centre = this.centreOf(state, pos);
        BlockState centreState = level.getBlockState(centre);
        if (!centreState.is(this)) {
            return;
        }
        int middle = this.middle();
        boolean powered = false;
        for (int dx = -middle; dx <= middle && !powered; dx++) {
            for (int dz = -middle; dz <= middle && !powered; dz++) {
                powered = level.hasNeighborSignal(centre.offset(dx, 0, dz));
            }
        }
        if (centreState.getValue(LaunchPadBlock.POWERED) != powered) {
            level.setBlock(centre, centreState.setValue(LaunchPadBlock.POWERED, powered), 2);
        }
    }

    /** Whatever removes one part (player, explosion, piston, command) takes the whole pad with it. */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(this) && !level.isClientSide()) {
            this.destroyAll(level, pos, state);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /** Breaks every part; only the centre's loot table drops the item. */
    private void destroyAll(Level level, BlockPos pos, BlockState state) {
        BlockPos centre = this.centreOf(state, pos);
        int middle = this.middle();
        for (int dx = -middle; dx <= middle; dx++) {
            for (int dz = -middle; dz <= middle; dz++) {
                BlockPos part = centre.offset(dx, 0, dz);
                if (!part.equals(pos) && level.getBlockState(part).is(this)) {
                    level.destroyBlock(part, true);
                }
            }
        }
    }
}
