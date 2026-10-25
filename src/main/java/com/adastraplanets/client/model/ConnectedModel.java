package com.adastraplanets.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A full cube whose faces join with neighbouring blocks of the same kind, so a wall of them reads as one panel.
 *
 * <p>Each face is drawn as four quarters. Every quarter looks at the two neighbours beside it in the face's plane and
 * the one diagonal to it, and takes its pixels from one of five tiles laid side by side in an 80x16 sheet:
 * <ol start="0">
 *     <li>alone: the full bordered block;</li>
 *     <li>joined all round: no border;</li>
 *     <li>joined sideways: borders only above and below;</li>
 *     <li>joined up/down: borders only left and right;</li>
 *     <li>joined both ways but not diagonally: an inner corner.</li>
 * </ol>
 *
 * <pre>
 * { "parent": "minecraft:block/block", "loader": "adastraplanets:connected",
 *   "textures": { "sheet": "adastraplanets:block/umbrite_block_connected", "particle": "adastraplanets:block/umbrite_block" } }
 * </pre>
 */
public final class ConnectedModel implements IUnbakedGeometry<ConnectedModel> {
    public static final IGeometryLoader<ConnectedModel> LOADER = (JsonObject json, JsonDeserializationContext context) -> new ConnectedModel();

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> sprites,
                           ModelState state, ItemOverrides overrides) {
        return new Baked(sprites.apply(context.getMaterial("sheet")), sprites.apply(context.getMaterial("particle")),
                context.getTransforms(), context.useAmbientOcclusion());
    }

    /** Which of the 26 surrounding blocks are the same block, one bit per offset. */
    private static final ModelProperty<Integer> NEIGHBOURS = new ModelProperty<>();

    private static int bit(int dx, int dy, int dz) {
        return 1 << ((dx + 1) * 9 + (dy + 1) * 3 + (dz + 1));
    }

    private static final class Baked implements IDynamicBakedModel {
        private static final FaceBakery BAKERY = new FaceBakery();

        private final TextureAtlasSprite sheet;
        private final TextureAtlasSprite particle;
        private final ItemTransforms transforms;
        private final boolean ambientOcclusion;
        private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

        Baked(TextureAtlasSprite sheet, TextureAtlasSprite particle, ItemTransforms transforms, boolean ambientOcclusion) {
            this.sheet = sheet;
            this.particle = particle;
            this.transforms = transforms;
            this.ambientOcclusion = ambientOcclusion;
        }

        @Override
        public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
            int mask = 0;
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if ((dx != 0 || dy != 0 || dz != 0)
                                && level.getBlockState(cursor.setWithOffset(pos, dx, dy, dz)).is(state.getBlock())) {
                            mask |= bit(dx, dy, dz);
                        }
                    }
                }
            }
            return data.derive().with(NEIGHBOURS, mask).build();
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random, ModelData data,
                                        @Nullable RenderType renderType) {
            if (side == null) {
                return List.of();
            }
            Integer stored = data.get(NEIGHBOURS);
            int mask = stored == null ? 0 : stored;
            int[] right = right(side);
            int[] up = up(side);
            int key = side.ordinal();
            int[] tiles = new int[4];
            for (int q = 0; q < 4; q++) {
                int h = (q & 1) == 0 ? -1 : 1;   // left or right quarter
                int v = (q & 2) == 0 ? 1 : -1;   // top or bottom quarter
                boolean beside = connected(mask, right, h, up, 0);
                boolean over = connected(mask, right, 0, up, v);
                boolean diagonal = connected(mask, right, h, up, v);
                tiles[q] = !beside && !over ? 0 : beside && !over ? 2 : !beside ? 3 : diagonal ? 1 : 4;
                key = key * 5 + tiles[q];
            }
            return this.cache.computeIfAbsent(key, k -> this.bakeFace(side, tiles));
        }

        private static boolean connected(int mask, int[] right, int h, int[] up, int v) {
            return (mask & bit(right[0] * h + up[0] * v, right[1] * h + up[1] * v, right[2] * h + up[2] * v)) != 0;
        }

        /** The quarters of one face: each a sub-rectangle of the face, textured from its tile's matching quarter. */
        private List<BakedQuad> bakeFace(Direction side, int[] tiles) {
            List<BakedQuad> quads = new ArrayList<>(4);
            int[] right = right(side);
            int[] up = up(side);
            float tileWidth = 16F / 5F;
            for (int q = 0; q < 4; q++) {
                boolean rightHalf = (q & 1) != 0;
                boolean bottomHalf = (q & 2) != 0;
                float[] from = {0, 0, 0};
                float[] to = {16, 16, 16};
                for (int axis = 0; axis < 3; axis++) {
                    if (right[axis] != 0) {
                        boolean low = (right[axis] > 0) != rightHalf;
                        from[axis] = low ? 0 : 8;
                        to[axis] = low ? 8 : 16;
                    } else if (up[axis] != 0) {
                        boolean low = (up[axis] > 0) == bottomHalf;
                        from[axis] = low ? 0 : 8;
                        to[axis] = low ? 8 : 16;
                    }
                }
                float u0 = tiles[q] * tileWidth + (rightHalf ? tileWidth / 2 : 0);
                float v0 = bottomHalf ? 8 : 0;
                BlockFaceUV uv = new BlockFaceUV(new float[]{u0, v0, u0 + tileWidth / 2, v0 + 8}, 0);
                BlockElementFace face = new BlockElementFace(side, -1, "sheet", uv);
                quads.add(BAKERY.bakeQuad(new Vector3f(from), new Vector3f(to), face, this.sheet, side,
                        net.minecraft.client.resources.model.BlockModelRotation.X0_Y0, null, true));
            }
            return quads;
        }

        /** The world direction a face's texture runs to the right, as vanilla block models map their UVs. */
        private static int[] right(Direction side) {
            return switch (side) {
                case NORTH -> new int[]{-1, 0, 0};
                case SOUTH, UP, DOWN -> new int[]{1, 0, 0};
                case WEST -> new int[]{0, 0, 1};
                case EAST -> new int[]{0, 0, -1};
            };
        }

        /** The world direction a face's texture runs upwards. */
        private static int[] up(Direction side) {
            return switch (side) {
                case UP -> new int[]{0, 0, -1};
                case DOWN -> new int[]{0, 0, 1};
                default -> new int[]{0, 1, 0};
            };
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.ambientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return true;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.particle;
        }

        @Override
        public ItemTransforms getTransforms() {
            return this.transforms;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }
}
