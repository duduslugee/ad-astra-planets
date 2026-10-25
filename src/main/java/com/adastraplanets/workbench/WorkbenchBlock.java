package com.adastraplanets.workbench;

import earth.terrarium.adastra.common.blocks.base.MachineBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * One of our workbenches: Ad Astra's machine block (facing, opening its menu, dropping its contents), pointed at our
 * own block entity type instead of looking one up among Ad Astra's.
 */
public class WorkbenchBlock extends MachineBlock {
    private final Workbench workbench;

    public WorkbenchBlock(Workbench workbench, Properties properties) {
        super(properties);
        this.workbench = workbench;
    }

    public Workbench workbench() {
        return this.workbench;
    }

    @Override
    public BlockEntityType<?> entity(BlockState state) {
        return this.workbench.blockEntityType();
    }
}
