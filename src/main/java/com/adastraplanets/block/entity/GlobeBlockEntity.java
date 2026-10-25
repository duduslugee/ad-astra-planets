package com.adastraplanets.block.entity;

import com.adastraplanets.block.GlobeBlock;
import com.adastraplanets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The spin of a globe: a push gives it speed (torque), which runs down a little every tick while the globe turns.
 * Redstone keeps it turning at a gentle speed. Runs on both sides, so the client spins smoothly between updates.
 */
public class GlobeBlockEntity extends BlockEntity {
    /** Degrees per tick a push adds, the most a globe can carry, how much it slows each tick, and the redstone speed. */
    private static final float PUSH = 18F;
    private static final float MAX_SPEED = 45F;
    private static final float FRICTION = 0.6F;
    private static final float POWERED_SPEED = 3F;

    private float speed;
    private float yRot;
    private float lastYRot;

    public GlobeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GLOBE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GlobeBlockEntity globe) {
        if (state.getValue(GlobeBlock.POWERED) && globe.speed < POWERED_SPEED) {
            globe.speed = POWERED_SPEED;
        }
        globe.lastYRot = globe.yRot;
        if (globe.speed > 0) {
            globe.yRot = (globe.yRot + globe.speed) % 360F;
            globe.lastYRot = globe.yRot - globe.speed;
            globe.speed = Math.max(0, globe.speed - FRICTION);
        }
    }

    /** A push: faster with every click, up to a limit. */
    public void spin() {
        this.speed = Math.min(MAX_SPEED, this.speed + PUSH);
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public float yRot(float partialTick) {
        return this.lastYRot + (this.yRot - this.lastYRot) * partialTick;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.speed = tag.getFloat("Speed");
        this.yRot = tag.getFloat("YRot");
        this.lastYRot = this.yRot;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("Speed", this.speed);
        tag.putFloat("YRot", this.yRot);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}
