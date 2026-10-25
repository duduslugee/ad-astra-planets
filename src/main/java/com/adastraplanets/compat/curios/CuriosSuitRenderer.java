package com.adastraplanets.compat.curios;

import com.adastraplanets.client.SuitModels;
import com.adastraplanets.registry.ModSuit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/** Draws a suit piece worn in its Curios slot with the suit's own model, posed with the wearer's body. */
public final class CuriosSuitRenderer implements ICurioRenderer {
    public static void register() {
        CuriosSuitRenderer renderer = new CuriosSuitRenderer();
        ModSuit.PIECES.forEach(item -> CuriosRendererRegistry.register(item.get(), () -> renderer));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slot, PoseStack pose,
                                                                         RenderLayerParent<T, M> parent, MultiBufferSource buffers, int light,
                                                                         float limbSwing, float limbSwingAmount, float partialTicks,
                                                                         float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(stack.getItem() instanceof ArmorItem armor) || !(parent.getModel() instanceof HumanoidModel<?> body)) {
            return;
        }
        HumanoidModel<LivingEntity> model = SuitModels.model(armor.getEquipmentSlot());
        if (model == null) {
            return;
        }
        ((HumanoidModel<LivingEntity>) body).copyPropertiesTo(model);
        model.setAllVisible(true);
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(SuitModels.TEXTURE), stack.hasFoil());
        model.renderToBuffer(pose, consumer, light, OverlayTexture.NO_OVERLAY);
    }
}
