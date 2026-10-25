package com.adastraplanets.block;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** The 5x5 launch pad (see {@link MultiblockLaunchPadBlock}). */
public class LargeLaunchPadBlock extends MultiblockLaunchPadBlock {
    public static final int SIZE = 5;
    public static final IntegerProperty X = IntegerProperty.create("x", 0, SIZE - 1);
    public static final IntegerProperty Z = IntegerProperty.create("z", 0, SIZE - 1);

    public LargeLaunchPadBlock(Properties properties) {
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
        return "info.adastraplanets.large_launch_pad";
    }
}
