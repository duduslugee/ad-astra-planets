package com.adastraplanets.block;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** The 7x7 launch pad (see {@link MultiblockLaunchPadBlock}). */
public class HugeLaunchPadBlock extends MultiblockLaunchPadBlock {
    public static final int SIZE = 7;
    public static final IntegerProperty X = IntegerProperty.create("x", 0, SIZE - 1);
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, SIZE - 1);

    public HugeLaunchPadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int size() {
        return SIZE;
    }

    @Override
    public IntegerProperty xProperty() {
        return X;
    }

    @Override
    public IntegerProperty zProperty() {
        return Z;
    }

    @Override
    protected String infoKey() {
        return "info.adastraplanets.huge_launch_pad";
    }
}
