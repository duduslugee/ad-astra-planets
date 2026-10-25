package com.adastraplanets.rocket;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncRocketDefinitionsPayload(Map<ResourceLocation, RocketDefinition> definitions) implements CustomPacketPayload {
    public static final Type<SyncRocketDefinitionsPayload> TYPE = new Type<>(AdAstraPlanets.id("sync_rocket_definitions"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRocketDefinitionsPayload> STREAM_CODEC = ByteBufCodecs
            .map(HashMap::new, ResourceLocation.STREAM_CODEC, RocketDefinition.STREAM_CODEC)
            .map(SyncRocketDefinitionsPayload::new, payload -> new HashMap<>(payload.definitions()));

    @Override
    public Type<SyncRocketDefinitionsPayload> type() {
        return TYPE;
    }

    public static void handle(SyncRocketDefinitionsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> RocketDefinitions.replaceAll(payload.definitions()));
    }
}
