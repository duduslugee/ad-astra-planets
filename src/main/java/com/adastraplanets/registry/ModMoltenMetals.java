package com.adastraplanets.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Each metal molten, for the smelteries of other mods (Hephaestus Forge, Productive Metalworks: their melting and
 * casting recipes are in data/adastraplanets/recipe/compat/). Minecraft wants fluids registered whatever is
 * installed, so they always exist; without either smeltery they are kept out of the creative tab and JEI.
 * Ids carry the metal's name (molten_helionite), never a world's.
 */
public final class ModMoltenMetals {
    /** The smelteries these fluids are for. */
    public static final List<String> SMELTERIES = List.of("hephaestus", "productivemetalworks");

    /** Every metal and the colour of its molten form (RGB, a bright tone of the metal's own ramp). */
    public static final Map<String, Integer> COLOURS = new LinkedHashMap<>();

    static {
        String[][] metals = {{"helionite", "e2bf68"}, {"abyssium", "65d6bd"}, {"umbrite", "a07a6c"}, {"pallorite", "ece4cf"},
                {"igneum", "efbf5f"}, {"aurorium", "81ecb3"}, {"eonyx", "ecc46e"}, {"stormium", "efcda1"}, {"nivalum", "c1e5f0"},
                {"ferrolith", "e0e6ec"}, {"vesperine", "ebe6e3"}, {"tholinar", "efc072"}, {"cassinium", "f1e5cc"},
                {"mosaicon", "c2e8e4"}, {"chasmal", "e6cfc0"}, {"obscurex", "cda39f"}, {"azurane", "adeae8"},
                {"nitralis", "eecfce"}, {"tempestine", "a9caea"}, {"mordrine", "deb6a9"}, {"cordium", "eadfbe"},
                {"shardex", "d0ddea"}, {"albedon", "e1ecf1"}};
        for (String[] metal : metals) {
            COLOURS.put(metal[0], Integer.parseInt(metal[1], 16));
        }
    }

    public static final List<MoltenMetal> ALL = new ArrayList<>();

    static {
        COLOURS.keySet().forEach(metal -> ALL.add(new MoltenMetal(metal)));
    }

    /** Whether a smeltery that uses these fluids is installed (otherwise they are hidden). */
    public static boolean anySmeltery() {
        return SMELTERIES.stream().anyMatch(ModList.get()::isLoaded);
    }

    public static final class MoltenMetal {
        public final String metal;
        public final DeferredHolder<FluidType, FluidType> type;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
        public final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
        public final DeferredBlock<LiquidBlock> block;
        public final DeferredItem<BucketItem> bucket;

        MoltenMetal(String metal) {
            this.metal = metal;
            String id = "molten_" + metal;
            // hot and heavy, glowing, like lava
            this.type = ModFluids.FLUID_TYPES.register(id, () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.adastraplanets." + id)
                    .density(3000).viscosity(6000).temperature(1300).lightLevel(12)
                    .canSwim(false).canDrown(false).canExtinguish(false).canConvertToSource(false)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)));
            this.source = ModFluids.FLUIDS.register(id, () -> new BaseFlowingFluid.Source(this.properties()));
            this.flowing = ModFluids.FLUIDS.register("flowing_" + id, () -> new BaseFlowingFluid.Flowing(this.properties()));
            this.block = ModBlocks.BLOCKS.register(id, () -> new LiquidBlock(this.source.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)));
            this.bucket = ModItems.ITEMS.register(id + "_bucket",
                    () -> new BucketItem(this.source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        }

        private BaseFlowingFluid.Properties properties() {
            return new BaseFlowingFluid.Properties(this.type, this.source, this.flowing).block(this.block).bucket(this.bucket)
                    .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(30);
        }

        public int colour() {
            return COLOURS.get(this.metal);
        }
    }

    /** Called from the mod constructor so every fluid is registered before the registers are attached. */
    public static void init() {
    }

    private ModMoltenMetals() {
    }
}
