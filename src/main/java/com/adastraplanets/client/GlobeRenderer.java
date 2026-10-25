package com.adastraplanets.client;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.entity.GlobeBlockEntity;
import com.adastraplanets.registry.ModGlobes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * Draws a globe's spinning part: the block model block/&lt;id&gt;_cube, turned about the vertical axis by the globe's
 * spin. The stand is the block's ordinary model. In the inventory and the hand the whole globe is drawn, slowly turning.
 */
public class GlobeRenderer implements BlockEntityRenderer<GlobeBlockEntity> {
    public GlobeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GlobeBlockEntity globe, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        renderGlobe(globe.getBlockState(), globe.yRot(partialTick), poseStack, buffer, light, overlay);
    }

    private static ModelResourceLocation cube(ResourceLocation block) {
        return ModelResourceLocation.standalone(block.withPath(path -> "block/" + path + "_cube"));
    }

    private static void renderGlobe(BlockState state, float yRot, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getModel(cube(BuiltInRegistries.BLOCK.getKey(state.getBlock())));
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));     // anticlockwise seen from above, as Ad Astra's globes turn
        poseStack.translate(-0.5, 0, -0.5);
        minecraft.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(Sheets.cutoutBlockSheet()),
                state, model, 1F, 1F, 1F, light, overlay);
        poseStack.popPose();
    }

    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(com.adastraplanets.registry.ModBlockEntityTypes.GLOBE.get(), GlobeRenderer::new);
    }

    /** The spinning parts are not block models of their own blocks, so they are loaded on their own. */
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        ModGlobes.blocks().keySet().forEach(id -> event.register(cube(AdAstraPlanets.id(id))));
    }

    public static void registerItems(RegisterClientExtensionsEvent event) {
        ItemRenderer renderer = new ItemRenderer();
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        };
        ModGlobes.items().values().forEach(item -> event.registerItem(extensions, item.get()));
    }

    /** The whole globe as an item: the stand, then the globe turning slowly with the clock. */
    private static class ItemRenderer extends BlockEntityWithoutLevelRenderer {
        ItemRenderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Override
        public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light,
                                 int overlay) {
            if (!(stack.getItem() instanceof BlockItem item)) {
                return;
            }
            BlockState state = item.getBlock().defaultBlockState();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(Sheets.cutoutBlockSheet()),
                    state, minecraft.getBlockRenderer().getBlockModel(state), 1F, 1F, 1F, light, overlay);
            renderGlobe(state, Util.getMillis() / 25F % 360F, poseStack, buffer, light, overlay);
        }
    }
}
