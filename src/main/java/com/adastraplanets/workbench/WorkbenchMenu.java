package com.adastraplanets.workbench;

import earth.terrarium.adastra.common.blockentities.machines.NasaWorkbenchBlockEntity;
import earth.terrarium.adastra.common.menus.content.PositionContent;
import earth.terrarium.adastra.common.menus.machines.NasaWorkbenchMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.Optional;

/**
 * Ad Astra's NASA Workbench menu (its slots, building on taking the result) under a menu type of our own per
 * workbench, so each opens its own screen and JEI's "show recipes" lists that workbench's rockets.
 */
public class WorkbenchMenu extends NasaWorkbenchMenu {
    public WorkbenchMenu(int id, Inventory inventory, NasaWorkbenchBlockEntity entity) {
        super(id, inventory, entity);
    }

    public WorkbenchMenu(int id, Inventory inventory, Optional<PositionContent> content) {
        super(id, inventory, content);
    }

    public Workbench workbench() {
        return this.entity instanceof WorkbenchBlockEntity bench ? bench.workbench() : Workbench.ADVANCED;
    }

    @Override
    public MenuType<?> getType() {
        return this.workbench().menuType();
    }
}
