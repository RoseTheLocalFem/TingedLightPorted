package tfc.tingedlights.util.sodium;

import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import tfc.tingedlights.AOFace;
import tfc.tingedlights.data.Color;

import java.util.BitSet;

public class TingedLightsLightingPipeline implements LightPipeline {
    LightDataAccess access;
    private final int[] mutableQuadVertexData = new int[32];
    private final BakedQuad mutableQuadWithoutShade;
    private final BakedQuad mutableQuadWithShade;

    public TingedLightsLightingPipeline(LightDataAccess access) {
        this.mutableQuadWithoutShade = new BakedQuad(this.mutableQuadVertexData, -1, Direction.UP, UnitTextureAtlasSprite.INSTANCE, false);
        this.mutableQuadWithShade = new BakedQuad(this.mutableQuadVertexData, -1, Direction.UP, UnitTextureAtlasSprite.INSTANCE, true);
        this.access = access;
    }

    private BakedQuad generateForgeQuad(ModelQuadView quad, boolean hasShade) {
//        if (quad instanceof BakedQuad) return (BakedQuad) quad;

        int[] vData = this.mutableQuadVertexData;

        for (int i = 0; i < 4; ++i) {
            int vertexBase = i * IQuadTransformer.STRIDE;
            vData[vertexBase + IQuadTransformer.POSITION] = Float.floatToIntBits(quad.getX(i));
            vData[vertexBase + IQuadTransformer.POSITION + 1] = Float.floatToIntBits(quad.getY(i));
            vData[vertexBase + IQuadTransformer.POSITION + 2] = Float.floatToIntBits(quad.getZ(i));
            vData[vertexBase + IQuadTransformer.NORMAL] = quad.getForgeNormal(i);
            vData[vertexBase + IQuadTransformer.UV2] = 0;
        }

        return hasShade ? this.mutableQuadWithShade : this.mutableQuadWithoutShade;
    }

    @Override
    public void calculate(ModelQuadView modelQuadView, BlockPos blockPos, QuadLightData quadLightData, Direction direction, Direction direction1, boolean b) {
        BakedQuad qd = generateForgeQuad(modelQuadView, b);
        AOFace face = new AOFace(
//                (BakedQuad) modelQuadView,
                qd,
                null // unused
        );
        face.calculate(
                direction1,
                ((WorldSlice) access.getWorld()).world,
                access.getWorld().getBlockState(blockPos),
                blockPos, BitSet.valueOf(new byte[]{
                        (byte) ((direction != null ? 2 : 0) + (b ? 1 : 0))
                })
        );
        int[] lms = new int[4];
        for (int i = 0; i < face.getColors().length; i++) {
            Color cl = face.getColors()[i];
//            int sl = (face.getSkylight()[i] >> 16) & 0xFFFF;
            int sl = 0;

            int color = ((byte)(Mth.clamp(cl.r(), 0, 1) * 255) & 0xFF)       |
                    (((byte)(Mth.clamp(cl.g(), 0, 1) * 255) & 0xFF) << 8) |
                    (((byte)(Mth.clamp(cl.b(), 0, 1) * 255) & 0xFF) << 16)|
                    (((byte)(sl) & 0xFF) << 24);
            lms[i] = color;
//            face.getShades()[i] = 1 - face.getShades()[i];
        }

        System.arraycopy(lms, 0, quadLightData.lm, 0, 4);
        System.arraycopy(face.getShades(), 0, quadLightData.br, 0, 4);
    }

    @Override
    public void reset() {
        LightPipeline.super.reset();
    }
}
