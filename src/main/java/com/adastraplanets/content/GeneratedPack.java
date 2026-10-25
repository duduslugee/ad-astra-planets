package com.adastraplanets.content;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** A read-only pack held in memory; files are generated from {@link StartupContent} at startup. */
public final class GeneratedPack implements PackResources {
    private final PackLocationInfo location;
    private final PackType type;
    private final Map<ResourceLocation, byte[]> files;
    private final int packFormat;

    public GeneratedPack(PackLocationInfo location, PackType type, Map<ResourceLocation, byte[]> files, int packFormat) {
        this.location = location;
        this.type = type;
        this.files = Map.copyOf(files);
        this.packFormat = packFormat;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        byte[] bytes = packType == this.type ? this.files.get(location) : null;
        return bytes == null ? null : () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
        if (packType != this.type) {
            return;
        }
        String prefix = path.endsWith("/") ? path : path + "/";
        this.files.forEach((id, bytes) -> {
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix)) {
                output.accept(id, () -> new ByteArrayInputStream(bytes));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType == this.type ? this.files.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet()) : Set.of();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        if (deserializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(Component.literal("Ad Astra: Planets generated content"), this.packFormat);
        }
        return null;
    }

    @Override
    public PackLocationInfo location() {
        return this.location;
    }

    @Override
    public void close() {
    }
}
