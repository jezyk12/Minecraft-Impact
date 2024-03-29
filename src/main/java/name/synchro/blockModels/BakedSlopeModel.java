package name.synchro.blockModels;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BakedSlopeModel implements BakedModel, FabricBakedModel {
    public static final ModelTransformation MODEL_TRANSFORM_SLOPE = new ModelTransformation(
            ModelHelper.TRANSFORM_BLOCK_3RD_PERSON_RIGHT,
            ModelHelper.TRANSFORM_BLOCK_3RD_PERSON_RIGHT,
            ModelHelper.TRANSFORM_BLOCK_1ST_PERSON_LEFT,
            ModelHelper.TRANSFORM_BLOCK_1ST_PERSON_RIGHT,
            Transformation.IDENTITY,
            getSlopeGuiTransformation(),
            ModelHelper.TRANSFORM_BLOCK_GROUND,
            ModelHelper.TRANSFORM_BLOCK_FIXED);
    private final Mesh mesh;
    private final Sprite particleSprite;

    public BakedSlopeModel(Mesh mesh, Sprite particleSprite) {
        this.mesh = mesh;
        this.particleSprite = particleSprite;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
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
        return this.particleSprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return MODEL_TRANSFORM_SLOPE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        mesh.outputTo(context.getEmitter());
    }

    private static Transformation getSlopeGuiTransformation() {
        Vector3f translation = new Vector3f((float) 0, (float) 0, (float) 0);
        translation.mul(0.0625f);
        translation.set(MathHelper.clamp(translation.x, -5.0F, 5.0F), MathHelper.clamp(translation.y, -5.0F, 5.0F), MathHelper.clamp(translation.z, -5.0F, 5.0F));
        return new Transformation(new Vector3f((float) 30, (float) 135, (float) 0), translation, new Vector3f((float) 0.625, (float) 0.625, (float) 0.625));
    }

}
