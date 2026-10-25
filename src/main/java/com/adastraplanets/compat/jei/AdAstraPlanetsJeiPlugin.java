package com.adastraplanets.compat.jei;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModItems;
import com.adastraplanets.workbench.Workbench;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import com.adastraplanets.client.WorkbenchScreen;
import earth.terrarium.adastra.client.screens.machines.NasaWorkbenchScreen;
import earth.terrarium.adastra.common.compat.jei.categories.NasaWorkbenchCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI: a category per workbench, showing its rockets in Ad Astra's NASA Workbench layout, and information pages for
 * what is only found by exploring. Loaded only with JEI (EMI reads it too).
 */
@JeiPlugin
public class AdAstraPlanetsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return AdAstraPlanets.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        for (Workbench workbench : Workbench.values()) {
            registration.addRecipeCategories(new WorkbenchCategory(registration.getJeiHelpers().getGuiHelper(), workbench));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        for (Workbench workbench : Workbench.values()) {
            registration.addRecipes(WorkbenchCategory.type(workbench),
                    level.getRecipeManager().getAllRecipesFor(workbench.recipeType()).stream().map(RecipeHolder::value).toList());
        }
        // what is only found by exploring gets an information page
        info(registration, "light_core", com.adastraplanets.registry.ModCrystals.ALL.stream().map(crystal -> crystal.core.get()).toList());
        info(registration, "ancient_suit", com.adastraplanets.registry.ModSuit.PIECES.stream().map(piece -> (net.minecraft.world.item.Item) piece.get()).toList());
        info(registration, "ancient_ship", List.of(ModItems.ANCIENT_SHIP.get()));
        info(registration, "access_card", List.of(com.adastraplanets.registry.ModExpedition.ACCESS_CARD.get()));
        info(registration, "core_socket", List.of(com.adastraplanets.registry.ModAncient.CORE_SOCKET.get().asItem(),
                com.adastraplanets.registry.ModAncient.CRYSTAL_SOCKET.get().asItem()));
    }

    /** The molten metals are only for other mods' smelteries: without one, JEI does not list them. */
    @Override
    public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime) {
        if (com.adastraplanets.registry.ModMoltenMetals.anySmeltery()) {
            return;
        }
        var ingredients = runtime.getIngredientManager();
        ingredients.removeIngredientsAtRuntime(mezz.jei.api.constants.VanillaTypes.ITEM_STACK, com.adastraplanets.registry.ModMoltenMetals.ALL.stream()
                .map(molten -> new net.minecraft.world.item.ItemStack(molten.bucket.get())).toList());
        ingredients.removeIngredientsAtRuntime(mezz.jei.api.neoforge.NeoForgeTypes.FLUID_STACK, com.adastraplanets.registry.ModMoltenMetals.ALL.stream()
                .map(molten -> new net.neoforged.neoforge.fluids.FluidStack(molten.source.get(), 1000)).toList());
    }

    private static void info(IRecipeRegistration registration, String key, List<net.minecraft.world.item.Item> items) {
        registration.addIngredientInfo(items.stream().map(net.minecraft.world.item.ItemStack::new).toList(),
                mezz.jei.api.constants.VanillaTypes.ITEM_STACK, net.minecraft.network.chat.Component.translatable("jei.adastraplanets." + key));
    }

    /** Each workbench builds its own rockets and every rocket of the benches below it, down to Ad Astra's. */
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModItems.ADVANCED_NASA_WORKBENCH.get().getDefaultInstance(), categories(Workbench.ADVANCED));
        registration.addRecipeCatalyst(ModItems.STELLAR_NASA_WORKBENCH.get().getDefaultInstance(), categories(Workbench.STELLAR));
    }

    /** "Show recipes" on a workbench's screen opens its rockets, then those of the benches below it. */
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        Rect2i area = NasaWorkbenchScreen.CLICK_AREA;
        registration.addRecipeClickArea(WorkbenchScreen.Advanced.class, area.getX(), area.getY(), area.getWidth(), area.getHeight(),
                categories(Workbench.ADVANCED));
        registration.addRecipeClickArea(WorkbenchScreen.Stellar.class, area.getX(), area.getY(), area.getWidth(), area.getHeight(),
                categories(Workbench.STELLAR));
    }

    private static RecipeType<?>[] categories(Workbench workbench) {
        List<RecipeType<?>> types = new ArrayList<>();
        for (int i = workbench.ordinal(); i >= 0; i--) {
            types.add(WorkbenchCategory.type(Workbench.values()[i]));
        }
        types.add(NasaWorkbenchCategory.RECIPE);
        return types.toArray(RecipeType<?>[]::new);
    }
}
