package name.synchro.synchroBlocks;

import name.synchro.Synchro;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class GasRenderHandler implements FluidRenderHandler {
    protected final Identifier[] textures;
    protected final Identifier emptyTexture;
    protected final Sprite[][] spritesList;
    protected final int tint;
    public GasRenderHandler(int tint) {
        this.textures = new Identifier[8];
        this.emptyTexture = new Identifier(Synchro.MOD_ID, "block/gas/invisible_gas");
        this.spritesList = new Sprite[8][2];
        this.tint = tint;
    }

    public GasRenderHandler() {
        this(-1);
    }

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return spritesList[state.getLevel()-1];
    }

    @Override
    public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
        return tint;
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas) {
        textures[0] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_0");
        textures[1] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_1");
        textures[2] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_2");
        textures[3] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_3");
        textures[4] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_4");
        textures[5] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_5");
        textures[6] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_6");
        textures[7] = new Identifier(Synchro.MOD_ID,"block/gas/visible_gas_opaqueness_7");
        for (int i = 0; i < 8; ++i){
            spritesList[i][0] = textureAtlas.getSprite(textures[i]);
            spritesList[i][1] = textureAtlas.getSprite(textures[i]);
        }
    }
}
