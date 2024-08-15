package name.synchro.blockModels;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class UnbakedSlopeModel implements UnbakedModel {
    private final SlopeShape slopeShape;
    private Sprite sprite;
    private SpriteIdentifier spriteId = ModelPlugin.DEFAULT_SPRITE_ID;
    private static final Vector3f CENTERED_VECTOR = new Vector3f(0.5f, 0.5f, 0.5f);
    private static final Vector3f STANDARD_VECTOR = new Vector3f(1f, 1f, 1f);

    public enum SlopeShape {
        STRAIGHT,
        INNER,
        OUTER;
    }

    public UnbakedSlopeModel(SlopeShape slopeShape, SpriteIdentifier spriteId) {
        this.slopeShape = slopeShape;
        this.spriteId = spriteId;
    }

    public UnbakedSlopeModel(SlopeShape slopeShape) {
        this.slopeShape = slopeShape;
    }

    public void setSprite(SpriteIdentifier spriteId) {
        this.spriteId = spriteId;
    }


    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {}

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        sprite = textureGetter.apply(spriteId);
        AffineTransformation transformation = rotationContainer.getRotation();
        Renderer renderer = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer());
        MeshBuilder builder = renderer.meshBuilder();
        switch (this.slopeShape) {
            case STRAIGHT-> emitNormalSlope(builder, transformation);
            case INNER -> emitInnerSlope(builder, transformation);
            case OUTER -> emitOuterSlope(builder, transformation);
        }
        Mesh mesh = builder.build();
        return new BakedSlopeModel(mesh, sprite);
    }

    private void emitNormalSlope(MeshBuilder builder, AffineTransformation transformation) {
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.WEST, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 1f, 0f)});     // slope side
        addCulledSimpleQuad(emitter, Direction.NORTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0.5f, 0.5f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 0f, 0f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.SOUTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0.5f, 0.5f, 1f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.EAST, transformation, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)});     // full
        addCulledSimpleQuad(emitter, Direction.DOWN, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)});     // full
    }

    private void emitOuterSlope (MeshBuilder builder, AffineTransformation transformation){
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.WEST, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0.5f, 0.5f, 0.5f)});     // half slope
        addSimpleQuad(emitter, Direction.NORTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0.5f, 0.5f, 0.5f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 0f)});     // half slope
        addCulledSimpleQuad(emitter, Direction.EAST, transformation, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0.5f, 0.5f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.SOUTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0.5f, 0.5f, 1f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.DOWN, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)});     // full
    }

    private void emitInnerSlope (MeshBuilder builder, AffineTransformation transformation){
        QuadEmitter emitter = builder.getEmitter();
        addSimpleQuad(emitter, Direction.NORTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 0f)});     // slope
        addSimpleQuad(emitter, Direction.WEST, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 1f, 0f)});     // slope
        addCulledSimpleQuad(emitter, Direction.NORTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0.5f, 0.5f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 0f, 0f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.WEST, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(0f, 0f, 1f),
                new Vector3f(0f, 1f, 1f),
                new Vector3f(0f, 0.5f, 0.5f)});     // triangle
        addCulledSimpleQuad(emitter, Direction.EAST, transformation, new Vector3f[]{
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 1f, 0f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(1f, 0f, 1f)});     // full
        addCulledSimpleQuad(emitter, Direction.SOUTH, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 1f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(1f, 1f, 1f),
                new Vector3f(0f, 1f, 1f)});     // full
        addCulledSimpleQuad(emitter, Direction.DOWN, transformation, new Vector3f[]{
                new Vector3f(0f, 0f, 0f),
                new Vector3f(1f, 0f, 0f),
                new Vector3f(1f, 0f, 1f),
                new Vector3f(0f, 0f, 1f)});     // full
    }

    private void addCulledSimpleQuad(QuadEmitter emitter, Direction direction, AffineTransformation transformation, Vector3f[] vertexes){
        emitter.cullFace(Direction.transform(transformation.getMatrix(), direction));
        setFace(emitter, transformation, vertexes);
    }

    private void addSimpleQuad(QuadEmitter emitter, Direction direction, AffineTransformation transformation, Vector3f[] vertexes){
        emitter.nominalFace(Direction.transform(transformation.getMatrix(), direction));
        setFace(emitter, transformation, vertexes);
    }

    private void setFace(QuadEmitter emitter, AffineTransformation transformation, Vector3f[] vertexes) {
        for (int index = 0; index < 4; ++index){
            emitter.pos(index, transformVertex(vertexes[index], transformation.getMatrix()) );
        }
        emitter.spriteBake(sprite, MutableQuadView.BAKE_LOCK_UV);
        emitter.color(-1, -1, -1, -1);
        emitter.emit();
    }

    private Vector3f transformVertex(Vector3f vertex, Matrix4f transformationMatrix) {
        Vector3f vertexCopy = new Vector3f(vertex);
        Vector4f vector4f = transformationMatrix.transform(new Vector4f(vertexCopy.x() - UnbakedSlopeModel.CENTERED_VECTOR.x(), vertexCopy.y() - UnbakedSlopeModel.CENTERED_VECTOR.y(), vertexCopy.z() - UnbakedSlopeModel.CENTERED_VECTOR.z(), 1.0f));
        vector4f.mul(new Vector4f(UnbakedSlopeModel.STANDARD_VECTOR, 1.0f));
        vertexCopy.set(vector4f.x() + UnbakedSlopeModel.CENTERED_VECTOR.x(), vector4f.y() + UnbakedSlopeModel.CENTERED_VECTOR.y(), vector4f.z() + UnbakedSlopeModel.CENTERED_VECTOR.z());
        return vertexCopy;
    }
}
