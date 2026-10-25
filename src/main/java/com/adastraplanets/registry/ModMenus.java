package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.workbench.Workbench;
import com.adastraplanets.workbench.WorkbenchMenu;
import com.teamresourceful.resourcefullib.common.menu.MenuContentHelper;
import earth.terrarium.adastra.common.menus.content.PositionContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

/** Menu types: one per workbench, opened with the workbench's position as Ad Astra's machines are. */
public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, AdAstraPlanets.MOD_ID);

    private static final Map<Workbench, DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>>> WORKBENCHES = new EnumMap<>(Workbench.class);

    static {
        for (Workbench workbench : Workbench.values()) {
            WORKBENCHES.put(workbench, MENUS.register(workbench.id(),
                    () -> MenuContentHelper.create(WorkbenchMenu::new, PositionContent.SERIALIZER)));
        }
    }

    public static DeferredHolder<MenuType<?>, MenuType<WorkbenchMenu>> workbench(Workbench workbench) {
        return WORKBENCHES.get(workbench);
    }

    private ModMenus() {
    }
}
