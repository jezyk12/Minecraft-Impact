package name.synchro.fluids.gases;

import net.minecraft.client.render.VertexConsumer;

@Deprecated
public class DebugGasRenderHandler extends TranslucentGasRenderer {
    public DebugGasRenderHandler(Gas gas) {
        super(gas);
    }

    @Override
    protected void vertex(VertexConsumer vertexConsumer, double[] xyz0, double[] xyz, float[] rgba, float[] uv, int light) {
        double x = xyz0[0] + xyz[0];
        double y = xyz0[1] + xyz[1];
        double z = xyz0[2] + xyz[2];
        vertexConsumer.vertex(x, y, z).color(rgba[0], rgba[1], rgba[2], 0.85f).texture(uv[0], uv[1]).light(light).normal(0.0F, 1.0F, 0.0F).next();
    }
}
