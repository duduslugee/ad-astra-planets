package com.adastraplanets.rocket;

import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import earth.terrarium.adastra.common.tags.ModBlockTags;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.common_storage_lib.context.ItemContext;
import earth.terrarium.common_storage_lib.fluid.impl.SimpleFluidStorage;
import earth.terrarium.common_storage_lib.resources.fluid.FluidResource;
import earth.terrarium.common_storage_lib.resources.fluid.util.FluidAmounts;
import earth.terrarium.common_storage_lib.storage.base.CommonStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;

/** Places a {@link TieredRocket} on a launch pad; its fuel tank follows the rocket's {@link RocketDefinition}. */
public class TieredRocketItem extends RocketItem implements GeoItem {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public TieredRocketItem(Supplier<EntityType<?>> type, Properties properties) {
        super(type, properties);
    }

    @Override
    public CommonStorage<FluidResource> getFluids(ItemStack stack, ItemContext context) {
        RocketDefinition definition = RocketDefinitions.get(this.type());
        long capacity = definition == null ? 0 : FluidAmounts.toPlatformAmount(definition.fuelCapacity());
        return new SimpleFluidStorage(context, ModDataManagers.FLUID_CONTENTS.componentType(), 1, capacity)
                .filter(0, fluid -> definition != null && fluid.is(definition.fuel()));
    }

    /**
     * Only lets the rocket onto the launch pads its definition allows (big rockets need the large pad); otherwise
     * Ad Astra's own placement logic runs unchanged.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        RocketDefinition definition = RocketDefinitions.get(this.type());
        BlockState clicked = context.getLevel().getBlockState(context.getClickedPos());
        if (definition != null && clicked.is(ModBlockTags.LAUNCH_PADS) && !clicked.is(definition.launchPads())) {
            if (context.getPlayer() != null && !context.getLevel().isClientSide()) {
                context.getPlayer().displayClientMessage(Component.translatable("message.adastraplanets.needs_large_launch_pad")
                        .withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
