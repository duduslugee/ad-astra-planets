package com.adastraplanets.client;

import com.adastraplanets.registry.ModFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.joml.Vector3f;

/**
 * How our fluids look: Minecraft's water textures, referenced (never copied) and tinted, with fog of the fluid's
 * colour when the camera is inside it.
 */
public final class FluidLooks {
    private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");
    private static final ResourceLocation MOLTEN_STILL = com.adastraplanets.AdAstraPlanets.id("block/molten_metal_still");
    private static final ResourceLocation MOLTEN_FLOWING = com.adastraplanets.AdAstraPlanets.id("block/molten_metal_flow");
    private static final ResourceLocation OVERLAY = ResourceLocation.withDefaultNamespace("block/water_overlay");
    /** Liquid methane: clear amber-brown, darkened by the haze's tholins (ARGB). */
    private static final int METHANE = 0xCC7A4E22;

    public static void register(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return OVERLAY;
            }

            @Override
            public int getTintColor() {
                return METHANE;
            }

            @Override
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance,
                                           float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(((METHANE >> 16) & 0xFF) / 255F, ((METHANE >> 8) & 0xFF) / 255F, (METHANE & 0xFF) / 255F);
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick,
                                        float nearDistance, float farDistance, com.mojang.blaze3d.shaders.FogShape shape) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogStart(0.0F);
                com.mojang.blaze3d.systems.RenderSystem.setShaderFogEnd(12.0F);
            }
        }, ModFluids.LIQUID_METHANE_TYPE.get());
        // the molten metals: their own thick, glowing texture, in each metal's colour
        for (var molten : com.adastraplanets.registry.ModMoltenMetals.ALL) {
            int colour = 0xFF000000 | molten.colour();
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return MOLTEN_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return MOLTEN_FLOWING;
                }

                @Override
                public int getTintColor() {
                    return colour;
                }
            }, molten.type.get());
        }
    }

    /** Fluids are drawn see-through. */
    public static void renderLayers() {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.LIQUID_METHANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_LIQUID_METHANE.get(), RenderType.translucent());
    }

    private FluidLooks() {
    }
}
