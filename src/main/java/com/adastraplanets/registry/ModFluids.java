package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Fluids: liquid methane, which fills Titan's lakes. It is lighter and runnier than water, swimmable, never freezes,
 * and can be picked up in a bucket. Its look (a tinted copy of Minecraft's water textures) is set in AdAstraPlanetsClient.
 */
public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, AdAstraPlanets.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, AdAstraPlanets.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> LIQUID_METHANE_TYPE = FLUID_TYPES.register("liquid_methane",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.adastraplanets.liquid_methane")
                    .density(420).viscosity(120).temperature(94)
                    .canSwim(true).canDrown(true).supportsBoating(true).canExtinguish(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_METHANE = FLUIDS.register("liquid_methane",
            () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LIQUID_METHANE = FLUIDS.register("flowing_liquid_methane",
            () -> new BaseFlowingFluid.Flowing(properties()));

    public static final DeferredBlock<LiquidBlock> LIQUID_METHANE_BLOCK = ModBlocks.BLOCKS.register("liquid_methane",
            () -> new LiquidBlock(LIQUID_METHANE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredItem<BucketItem> LIQUID_METHANE_BUCKET = ModItems.ITEMS.register("liquid_methane_bucket",
            () -> new BucketItem(LIQUID_METHANE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(LIQUID_METHANE_TYPE, LIQUID_METHANE, FLOWING_LIQUID_METHANE)
                .block(LIQUID_METHANE_BLOCK).bucket(LIQUID_METHANE_BUCKET)
                // runnier than water: it spreads a little further and a little faster
                .slopeFindDistance(5).levelDecreasePerBlock(1).tickRate(4);
    }

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
    }

    private ModFluids() {
    }
}
