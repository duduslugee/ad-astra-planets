package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.workbench.Workbench;
import com.adastraplanets.workbench.WorkbenchRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

/** Recipe types and serializers: one of each per workbench. */
public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, AdAstraPlanets.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AdAstraPlanets.MOD_ID);

    private static final Map<Workbench, DeferredHolder<RecipeType<?>, RecipeType<WorkbenchRecipe>>> WORKBENCH_TYPES = new EnumMap<>(Workbench.class);
    private static final Map<Workbench, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorkbenchRecipe>>> WORKBENCH_SERIALIZERS =
            new EnumMap<>(Workbench.class);

    static {
        for (Workbench workbench : Workbench.values()) {
            WORKBENCH_TYPES.put(workbench, TYPES.register(workbench.id(), () -> RecipeType.simple(workbench.location())));
            WORKBENCH_SERIALIZERS.put(workbench, SERIALIZERS.register(workbench.id(), () -> WorkbenchRecipe.Serializer.of(workbench)));
        }
    }

    public static DeferredHolder<RecipeType<?>, RecipeType<WorkbenchRecipe>> workbenchType(Workbench workbench) {
        return WORKBENCH_TYPES.get(workbench);
    }

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorkbenchRecipe>> workbenchSerializer(Workbench workbench) {
        return WORKBENCH_SERIALIZERS.get(workbench);
    }

    private ModRecipes() {
    }
}
