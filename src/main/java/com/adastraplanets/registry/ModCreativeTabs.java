package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.config.AdAstraPlanetsConfig;
import com.adastraplanets.rocket.TieredRocketItem;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.common_storage_lib.context.impl.ModifyOnlyContext;
import earth.terrarium.common_storage_lib.fluid.FluidApi;
import earth.terrarium.common_storage_lib.resources.fluid.FluidResource;
import earth.terrarium.common_storage_lib.storage.base.CommonStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdAstraPlanets.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.adastraplanets.main"))
            .icon(() -> ModItems.TIER_5_ROCKET.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().stream().map(holder -> holder.get()).filter(ModCreativeTabs::enabled)
                        .forEach(output::accept);
                // Like Ad Astra's tab: a fuelled copy of each rocket, which also shows the fuel bar.
                ModItems.rockets().stream().map(holder -> holder.get()).filter(ModCreativeTabs::enabled)
                        .forEach(rocket -> output.accept(fuelled(rocket, ModFluids.FUEL.get())));
            })
            .build());

    /** Content switched off in the config stays registered (saves keep working) but leaves the creative tab. */
    private static boolean enabled(Item item) {
        if (item instanceof TieredRocketItem rocket) {
            return AdAstraPlanetsConfig.rocketEnabled(BuiltInRegistries.ENTITY_TYPE.getKey(rocket.type()));
        }
        if (com.adastraplanets.registry.ModMoltenMetals.ALL.stream().anyMatch(molten -> molten.bucket.get() == item)) {
            return com.adastraplanets.registry.ModMoltenMetals.anySmeltery();
        }
        if (item == ModItems.LARGE_LAUNCH_PAD.get()) {
            return AdAstraPlanetsConfig.LARGE_LAUNCH_PAD.get();
        }
        if (item == ModItems.HUGE_LAUNCH_PAD.get()) {
            return AdAstraPlanetsConfig.HUGE_LAUNCH_PAD.get();
        }
        return true;
    }

    private static ItemStack fuelled(Item item, Fluid fluid) {
        ModifyOnlyContext context = new ModifyOnlyContext(item.getDefaultInstance());
        CommonStorage<FluidResource> tank = context.find(FluidApi.ITEM);
        if (tank == null) {
            return context.stack();
        }
        FluidResource resource = FluidResource.of(fluid);
        tank.insert(resource, tank.getLimit(0, resource), false);
        return context.stack();
    }

    private ModCreativeTabs() {
    }
}
