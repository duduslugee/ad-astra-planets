package com.adastraplanets.workbench;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModBlockEntityTypes;
import com.adastraplanets.registry.ModMenus;
import com.adastraplanets.registry.ModRecipes;
import earth.terrarium.adastra.common.registry.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * The rocket workbenches past Ad Astra's own NASA Workbench: each builds its own range of rockets from recipes of its
 * own type, and every rocket of the benches below it too (Ad Astra's keeps Tiers 1-4). Which rocket needs which bench
 * is only a matter of which recipe type its recipe uses, so datapacks and KubeJS move rockets between benches freely.
 */
public enum Workbench {
    /** Tiers 5-7, and 1-4. */
    ADVANCED("advanced_nasa_workbench"),
    /** Tiers 8-10, and 1-7. */
    STELLAR("stellar_nasa_workbench");

    private final String id;

    Workbench(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public ResourceLocation location() {
        return AdAstraPlanets.id(this.id);
    }

    public RecipeType<WorkbenchRecipe> recipeType() {
        return ModRecipes.workbenchType(this).get();
    }

    /** The recipe types this bench builds, its own first, then the benches below it down to Ad Astra's. */
    @SuppressWarnings("unchecked")
    public List<RecipeType<? extends Recipe<RecipeInput>>> recipeTypes() {
        List<RecipeType<? extends Recipe<RecipeInput>>> types = new ArrayList<>();
        for (int i = this.ordinal(); i >= 0; i--) {
            types.add(values()[i].recipeType());
        }
        types.add((RecipeType<? extends Recipe<RecipeInput>>) (RecipeType<?>) ModRecipeTypes.NASA_WORKBENCH.get());
        return types;
    }

    public RecipeSerializer<WorkbenchRecipe> serializer() {
        return ModRecipes.workbenchSerializer(this).get();
    }

    public BlockEntityType<WorkbenchBlockEntity> blockEntityType() {
        return ModBlockEntityTypes.workbench(this).get();
    }

    public MenuType<WorkbenchMenu> menuType() {
        return ModMenus.workbench(this).get();
    }
}
