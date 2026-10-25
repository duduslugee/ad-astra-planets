package com.adastraplanets.client;

import com.adastraplanets.workbench.Workbench;
import com.adastraplanets.workbench.WorkbenchMenu;
import earth.terrarium.adastra.client.screens.base.MachineScreen;
import earth.terrarium.adastra.client.screens.machines.NasaWorkbenchScreen;
import earth.terrarium.adastra.common.blockentities.machines.NasaWorkbenchBlockEntity;
import earth.terrarium.adastra.common.menus.machines.NasaWorkbenchMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * A workbench's screen: Ad Astra's NASA Workbench screen (its background and slots), as a class of its own per
 * workbench rather than a subclass of Ad Astra's, so JEI's "show recipes" area opens that workbench's rockets.
 */
public abstract class WorkbenchScreen extends MachineScreen<NasaWorkbenchMenu, NasaWorkbenchBlockEntity> {
    protected WorkbenchScreen(WorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, NasaWorkbenchScreen.TEXTURE, STEEL_SLOT, 177, 224);
    }

    public static class Advanced extends WorkbenchScreen {
        public Advanced(WorkbenchMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
        }
    }

    public static class Stellar extends WorkbenchScreen {
        public Stellar(WorkbenchMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
        }
    }

    @SuppressWarnings("unchecked")
    public static void register(RegisterMenuScreensEvent event) {
        // the screens are typed on Ad Astra's menu class (their base class is), our menus extend it
        event.<NasaWorkbenchMenu, Advanced>register((MenuType<NasaWorkbenchMenu>) (MenuType<?>) Workbench.ADVANCED.menuType(),
                (menu, inventory, title) -> new Advanced((WorkbenchMenu) menu, inventory, title));
        event.<NasaWorkbenchMenu, Stellar>register((MenuType<NasaWorkbenchMenu>) (MenuType<?>) Workbench.STELLAR.menuType(),
                (menu, inventory, title) -> new Stellar((WorkbenchMenu) menu, inventory, title));
    }
}
