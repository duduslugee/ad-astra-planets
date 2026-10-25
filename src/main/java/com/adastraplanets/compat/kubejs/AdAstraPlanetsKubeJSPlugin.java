package com.adastraplanets.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;

/** Registered through kubejs.plugins.txt; KubeJS only loads it when installed. */
public class AdAstraPlanetsKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(AdAstraPlanetsKubeEvents.GROUP);
    }

    /**
     * Rocket workbench recipes: {@code event.recipes.adastraplanets.advanced_nasa_workbench(result, [ingredients])} (and
     * {@code stellar_nasa_workbench}), up to 14 ingredients in the workbench's slot order, as in Ad Astra's.
     */
    @Override
    public void registerRecipeSchemas(dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry registry) {
        var result = dev.latvian.mods.kubejs.recipe.component.ItemStackComponent.ITEM_STACK.outputKey("result");
        var ingredients = dev.latvian.mods.kubejs.recipe.component.IngredientComponent.INGREDIENT.instance().asList()
                .inputKey("ingredients");
        var schema = new dev.latvian.mods.kubejs.recipe.schema.RecipeSchema(result, ingredients);
        var namespace = registry.namespace(com.adastraplanets.AdAstraPlanets.MOD_ID);
        for (var workbench : com.adastraplanets.workbench.Workbench.values()) {
            namespace.register(workbench.id(), schema);
        }
    }
}
