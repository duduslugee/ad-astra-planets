package com.adastraplanets.workbench;

import earth.terrarium.adastra.common.blockentities.base.ContainerRecipeWrapper;
import earth.terrarium.adastra.common.blockentities.machines.NasaWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Ad Astra's NASA Workbench (its 14 slots, building the rocket and throwing it out on top), matching recipes of its
 * own workbench's type first and then those of every workbench below it, so a better bench still builds older rockets.
 */
public class WorkbenchBlockEntity extends NasaWorkbenchBlockEntity {
    private static final int RESULT = 14;
    private final Workbench workbench;
    private final List<RecipeManager.CachedCheck<RecipeInput, ? extends Recipe<RecipeInput>>> checks;
    @Nullable
    private Recipe<RecipeInput> current;

    public WorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.workbench = ((WorkbenchBlock) state.getBlock()).workbench();
        this.checks = this.workbench.recipeTypes().stream()
                .<RecipeManager.CachedCheck<RecipeInput, ? extends Recipe<RecipeInput>>>map(RecipeManager::createCheck).toList();
    }

    public Workbench workbench() {
        return this.workbench;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new WorkbenchMenu(id, inventory, this);
    }

    @Override
    public void serverTick(ServerLevel level, long time, BlockState state, BlockPos pos) {
        for (int i = 0; i < RESULT; i++) {
            if (!this.getItem(i).isEmpty()) {
                this.spawnWorkingParticles(level, pos);
                break;
            }
        }
        this.setItem(RESULT, this.canCraft() ? this.current.getResultItem(level.registryAccess()).copy() : ItemStack.EMPTY);
        if (time % 30L == 0L && this.canFunction() && this.canCraft()) {
            this.craft();
        }
    }

    @Override
    public void update() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        ContainerRecipeWrapper input = new ContainerRecipeWrapper(this);
        this.current = null;
        for (var check : this.checks) {
            var found = check.getRecipeFor(input, this.level).map(RecipeHolder::value);
            if (found.isPresent()) {
                this.current = found.get();
                return;
            }
        }
    }

    @Override
    public boolean canCraft() {
        return this.current != null && this.level != null && this.current.matches(new ContainerRecipeWrapper(this), this.level);
    }

    @Override
    public void craft() {
        if (this.current == null || this.level == null) {
            return;
        }
        this.spawnResultParticles();
        BlockPos pos = this.getBlockPos();
        Containers.dropItemStack(this.level, pos.getX(), pos.getY() + 1, pos.getZ(), this.getItem(RESULT).copy());
        this.setItem(RESULT, ItemStack.EMPTY);
        for (int i = 0; i < RESULT; i++) {
            this.getItem(i).shrink(1);
        }
        this.current = null;
    }
}
