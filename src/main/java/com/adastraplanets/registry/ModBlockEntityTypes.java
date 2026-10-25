package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.entity.GeyserBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdAstraPlanets.MOD_ID);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeyserBlockEntity>> GEYSER = BLOCK_ENTITY_TYPES.register("geyser",
            () -> BlockEntityType.Builder.of(GeyserBlockEntity::new, ModBlocks.GEYSER.get(), ModBlocks.SULFUR_VENT.get(), ModBlocks.NITROGEN_VENT.get(),
                    com.adastraplanets.registry.ModAncient.FORGE_VENT.get()).build(null));
    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.adastraplanets.block.entity.GlobeBlockEntity>> GLOBE =
            BLOCK_ENTITY_TYPES.register("globe", () -> BlockEntityType.Builder.of(com.adastraplanets.block.entity.GlobeBlockEntity::new,
                    ModGlobes.allBlocks()).build(null));

    private static final java.util.Map<com.adastraplanets.workbench.Workbench,
            DeferredHolder<BlockEntityType<?>, BlockEntityType<com.adastraplanets.workbench.WorkbenchBlockEntity>>> WORKBENCHES =
            new java.util.EnumMap<>(com.adastraplanets.workbench.Workbench.class);

    static {
        WORKBENCHES.put(com.adastraplanets.workbench.Workbench.ADVANCED, workbench("advanced_nasa_workbench", ModBlocks.ADVANCED_NASA_WORKBENCH));
        WORKBENCHES.put(com.adastraplanets.workbench.Workbench.STELLAR, workbench("stellar_nasa_workbench", ModBlocks.STELLAR_NASA_WORKBENCH));
    }

    @SuppressWarnings("DataFlowIssue")
    private static DeferredHolder<BlockEntityType<?>, BlockEntityType<com.adastraplanets.workbench.WorkbenchBlockEntity>> workbench(
            String id, net.neoforged.neoforge.registries.DeferredBlock<?> block) {
        return BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of(com.adastraplanets.workbench.WorkbenchBlockEntity::new,
                block.get()).build(null));
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<com.adastraplanets.workbench.WorkbenchBlockEntity>> workbench(
            com.adastraplanets.workbench.Workbench workbench) {
        return WORKBENCHES.get(workbench);
    }

    private ModBlockEntityTypes() {
    }
}
