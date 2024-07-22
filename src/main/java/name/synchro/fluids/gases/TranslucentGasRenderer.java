package name.synchro.fluids.gases;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TranslucentGasRenderer implements FluidRenderHandler {
    public static final Identifier GAS_TEXTURE_ID = new Identifier(Synchro.MOD_ID, "block/gas");
    protected final Identifier[] textures;
    protected final Sprite[] spritesList;
    protected final Gas gas;

    public TranslucentGasRenderer(Gas gas) {
        this.textures = new Identifier[2];
        this.textures[0] = GAS_TEXTURE_ID;
        this.textures[1] = textures[0];
        this.spritesList = new Sprite[2];
        this.gas = gas;
    }

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return this.spritesList;
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return this.gas.color;
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas) {
        spritesList[0] = textureAtlas.getSprite(textures[0]);
        spritesList[1] = textureAtlas.getSprite(textures[1]);
    }

    @Override
    public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        boolean u = shouldRenderFace(world, pos, Direction.UP);
        boolean d = shouldRenderFace(world, pos, Direction.DOWN);
        boolean w = shouldRenderFace(world, pos, Direction.WEST);
        boolean s = shouldRenderFace(world, pos, Direction.SOUTH);
        boolean e = shouldRenderFace(world, pos, Direction.EAST);
        boolean n = shouldRenderFace(world, pos, Direction.NORTH);
        if (!(u || d || w || s || e || n)) return;
        double x0 = pos.getX() & 15;
        double y0 = pos.getY() & 15;
        double z0 = pos.getZ() & 15;
        double[] xyz0 = {x0, y0, z0};
        float u0 = spritesList[0].getMinU();
        float u1 = spritesList[0].getMaxU();
        float v0 = spritesList[0].getMinV();
        float v1 = spritesList[0].getMaxV();
        float[] u0v0u1v1 = {u0, v0, u1, v1};
        float[] rgba = {this.gas.rgb.x, this.gas.rgb.y, this.gas.rgb.z, 0.5f * fluidState.getLevel() / 16f};
        int light = getLight(world, pos);
        if (u) vertexQuad(vertexConsumer, xyz0, 0x7326, rgba, u0v0u1v1, light);
        if (d) vertexQuad(vertexConsumer, xyz0, 0x5401, rgba, u0v0u1v1, light);
        if (w) vertexQuad(vertexConsumer, xyz0, 0x2046, rgba, u0v0u1v1, light);
        if (s) vertexQuad(vertexConsumer, xyz0, 0x6457, rgba, u0v0u1v1, light);
        if (e) vertexQuad(vertexConsumer, xyz0, 0x7513, rgba, u0v0u1v1, light);
        if (n) vertexQuad(vertexConsumer, xyz0, 0x3102, rgba, u0v0u1v1, light);
    }

    protected void vertex(VertexConsumer vertexConsumer, double[] xyz0, double[] xyz, float[] rgba, float[] uv, int light) {
        double x = xyz0[0] + xyz[0];
        double y = xyz0[1] + xyz[1];
        double z = xyz0[2] + xyz[2];
        vertexConsumer.vertex(x, y, z).color(rgba[0], rgba[1], rgba[2], rgba[3]).texture(uv[0], uv[1]).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }

    private void vertexQuad(VertexConsumer vertexConsumer, double[] xyz0, int array, float[] rgba, float[] u0v0u1v1, int light) {
        final int[] uvs = {0, 2, 3, 1};
        for (int i = 0; i < 4; i++) {
            int n = (array >>> ((3 - i) << 2)) & 0b1111;
            double[] xyz = {n & 1, (n >>> 1) & 1, (n >>> 2 ) & 1};
            int m = uvs[i];
            float[] uv = {(m & 1) == 0 ? u0v0u1v1[0] : u0v0u1v1[2] , ((m & 2) >>> 1) == 0 ? u0v0u1v1[1]: u0v0u1v1[3]};
            vertex(vertexConsumer, xyz0, xyz, rgba, uv, light);
        }
    }

    private static boolean shouldRenderFace(BlockRenderView view, BlockPos pos, Direction direction){
        BlockState blockState = view.getBlockState(pos.offset(direction));
        FluidState fluidState = view.getFluidState(pos.offset(direction));
        if (fluidState.getFluid() instanceof Gas) return false;
        return !isSideCovered(view, direction, pos.offset(direction), blockState);
    }

    private static boolean isSideCovered(BlockView world, Direction direction, BlockPos pos, BlockState state) {
        if (state.isOpaque()) {
            VoxelShape voxelShape = VoxelShapes.empty();
            VoxelShape voxelShape2 = state.getCullingShape(world, pos);
            return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
        } else {
            return false;
        }
    }

    /**
     * From Vanilla
     */
    private static int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
        int l = j & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
        int m = i >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
        int n = j >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 15);
        return (Math.max(k, l)) | (Math.max(m, n)) << 16;
    }

}
