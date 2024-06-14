package name.synchro.worldRendering;

import com.mojang.blaze3d.systems.RenderSystem;
import name.synchro.Synchro;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class DebugRendering {
    public static final Identifier DEBUG_WHITE = new Identifier(Synchro.MOD_ID, "textures/debug/all_white.png");
    public static final Identifier DEBUG_BLACK = new Identifier(Synchro.MOD_ID, "textures/debug/all_black.png");
    public static final Identifier DEBUG_GRAY = new Identifier(Synchro.MOD_ID, "textures/debug/all_gray.png");
    public static final Identifier DEBUG_SYMBOL = new Identifier(Synchro.MOD_ID, "textures/debug/symbol.png");
    public static VertexBuffer vertexBuffer;
    public static void renderAll(){
        WorldRenderEvents.END.register(context -> {
            MatrixStack matrixStack = new MatrixStack();

            Camera camera = context.camera();
            Vec3d targetPosition = new Vec3d(0, -58, 0);
            Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            if (vertexBuffer == null){
                BufferBuilder buffer = tessellator.getBuffer();

                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
                buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
                buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
                buffer.vertex(positionMatrix, 1, 0, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();

                buffer.vertex(positionMatrix, 0, 2, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
                buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
                buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
                buffer.vertex(positionMatrix, 1, 2, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
                vertexBuffer = new VertexBuffer();
                vertexBuffer.bind();
                vertexBuffer.upload(buffer.end());
                VertexBuffer.unbind();
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, DEBUG_SYMBOL);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            Matrix4f projectionMatrix = context.gameRenderer().getBasicProjectionMatrix(context.gameRenderer().getClient().options.getFov().getValue());
            vertexBuffer.bind();
            vertexBuffer.draw(positionMatrix, projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();

            //tessellator.draw();
            //drawTriangle(buffer, positionMatrix, tessellator);
        });
    }

    private static void drawTriangle(BufferBuilder buffer, Matrix4f positionMatrix, Tessellator tessellator) {
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(positionMatrix, 0,0,0).color(1f, 0f, 0f, 1f).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, 1,1,0).color(0f, 1f, 0f, 1f).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, 1,0,1).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
        tessellator.draw();
    }
}
