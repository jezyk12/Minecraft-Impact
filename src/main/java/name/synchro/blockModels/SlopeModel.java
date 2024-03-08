package name.synchro.blockModels;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SlopeModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private Sprite sprite;
    private final SpriteIdentifier spriteID;
    private final Mesh[] meshes = new Mesh[24];
    private static final Vector3f CENTERED_VECTOR = new Vector3f(0.5f, 0.5f, 0.5f);

    public SlopeModel(Identifier texture) {
        this.spriteID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {}

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        sprite = textureGetter.apply(spriteID);
        Renderer renderer = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer());
        MeshBuilder builder = renderer.meshBuilder();
        for (int j = 0; j < 2; ++j){
            boolean flip = j != 0;
            for (int i = 0; i < 4; ++i) {
                emitNormalSlope(builder,90 * i, flip);
                meshes[i + 4 * j] = builder.build();
                emitOuterSlope(builder, 90 * i, flip);
                meshes[8 + i + 4 * j] = builder.build();
                emitInnerSlope(builder, 90 * i, flip);
                meshes[16 + i + 4 * j] = builder.build();
            }
        }
        return this;
    }

    private void emitNormalSlope(MeshBuilder builder, int rotation, boolean flip) {
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.NORTH, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 0f)}));
        addSimpleQuad(emitter, Direction.WEST, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(0f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.EAST, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.SOUTH, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0f, 1f, 1f)}));
        addSimpleQuad(emitter, flip ? Direction.UP: Direction.DOWN, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)}));
    }

    private void emitOuterSlope (MeshBuilder builder, int rotation, boolean flip){
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.WEST, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.NORTH, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 0f)}));
        addSimpleQuad(emitter, Direction.EAST, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.SOUTH, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)}));
        addSimpleQuad(emitter, flip ? Direction.UP: Direction.DOWN, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)}));
    }

    private void emitInnerSlope (MeshBuilder builder, int rotation, boolean flip){
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.NORTH, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 0f)}));
        addSimpleQuad(emitter, Direction.WEST, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 1f, 0f)}));
        addSimpleQuad(emitter, Direction.NORTH, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 0f, 0f)}));
        addSimpleQuad(emitter, Direction.WEST, rotation, flipUpsideDown(flip, false, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(0f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.EAST, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)}));
        addSimpleQuad(emitter, Direction.SOUTH, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0f, 1f, 1f)}));
        addSimpleQuad(emitter, flip ? Direction.UP: Direction.DOWN, rotation, flipUpsideDown(flip, true, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)}));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        StairShape shape = state.get(StairsBlock.SHAPE);
        Direction facing = state.get(StairsBlock.FACING);
        BlockHalf half = state.get(StairsBlock.HALF);
        Direction equivalentFacing;
        int indexAdder;
        switch (shape) {
            case INNER_LEFT -> {
                equivalentFacing = facing;
                indexAdder = 16;
            }
            case INNER_RIGHT -> {
                equivalentFacing = facing.rotateYClockwise();
                indexAdder = 16;
            }
            case OUTER_LEFT -> {
                equivalentFacing = facing;
                indexAdder = 8;
            }
            case OUTER_RIGHT -> {
                equivalentFacing = facing.rotateYClockwise();
                indexAdder = 8;
            }
            default -> {
                equivalentFacing = facing;
                indexAdder = 0;
            }
        };
        int index = switch (equivalentFacing) {
            case NORTH -> 2;
            case SOUTH -> 0;
            case WEST -> 3;
            case EAST -> 1;
            default -> 0;
        } + switch (half){
            case TOP -> 4;
            case BOTTOM -> 0;
        };
        meshes[index + indexAdder].outputTo(context.getEmitter());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        meshes[0].outputTo(context.getEmitter());
    }

    private QuadEmitter addSimpleQuad(QuadEmitter emitter, Direction direction, int rotation, Vector3f[] positions){
        if (Direction.Type.HORIZONTAL.test(direction)){
            for (int i = 0; i < rotation % 360 / 90; i++) {
                direction = direction.rotateYClockwise();
            }
        }
        emitter.nominalFace(direction);
        for (int index = 0; index < 4; ++index){
            emitter.pos(index, simplyRotateY(positions[index], rotation));
        }
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.emit();
        return emitter;
    }

    private Vector3f simplyRotateY(Vector3f origin, int degree){
        Vector3f relativeVector = origin.sub(CENTERED_VECTOR);
        Vector3f newVector = relativeVector.rotateY((float) (degree / 180f * Math.PI));
        return newVector.add(CENTERED_VECTOR);
    }

    private Vector3f[] flipUpsideDown(boolean flip, boolean changeCyclingDirection, Vector3f[] origins ){
        if (flip){
            Vector3f[] newQuadPos = new Vector3f[4];
            for (int i = 0; i < 4; i++) {
                Vector3f each = origins[i];
                int j = changeCyclingDirection ? switch (i){
                    case 1 -> 3;
                    case 3 -> 1;
                    default -> i;
                } : i;
                newQuadPos[j] = new Vector3f(each.x(), 1f - each.y(), each.z());
            }
            return newQuadPos;
        }
        else return origins;
    }

}
