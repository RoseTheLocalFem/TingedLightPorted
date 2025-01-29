package tfc.tingedlights.util.vanilla;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import tfc.tingedlights.api.data.Light;
import tfc.tingedlights.data.access.TingedLightsBlockAttachments;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ColoredBlockLightingEngine extends BlockLightEngine {
    Light type;
    BlockGetter level;

    public ColoredBlockLightingEngine(Light type, LightChunkGetter lightChunk, BlockGetter level) {
        super(lightChunk);
        this.type = type;
        this.level = level;
    }

    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    Deque<Runnable> events = new ArrayDeque<>();

    @Override
    public int getEmission(long pLevelPos, BlockState state) {
        int i = BlockPos.getX(pLevelPos);
        int j = BlockPos.getY(pLevelPos);
        int k = BlockPos.getZ(pLevelPos);

        pos.set(i, j, k);
//		BlockState state = blockgetter.getBlockState(pos);
        TingedLightsBlockAttachments attachments = (TingedLightsBlockAttachments) state.getBlock();
        // TODO: check if may provide light before getting chunk
        BlockGetter blockgetter = this.chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(i), SectionPos.blockToSectionCoord(k));
        if (blockgetter == null) return 0;
        Light light = attachments.createLight(state, blockgetter, pos);
        if (light == null) return 0;
        if (light.equals(type)) return attachments.getBrightness(state, blockgetter, pos);
        return 0;
    }

    @Override
    public int runLightUpdates() {
        if (!events.isEmpty()) {
            for (int i = 0; i < Minecraft.getInstance().options.renderDistance().get() * Minecraft.getInstance().options.renderDistance().get(); i++) {
                if (events.isEmpty()) break;
                events.pop().run();
            }
        }
        return super.runLightUpdates();
    }

    @Override
    public void updateSectionStatus(SectionPos pPos, boolean pIsEmpty) {
        super.updateSectionStatus(pPos, pIsEmpty);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (!pIsEmpty) {
            events.add(() -> {
                int mpx = pPos.minBlockX();
                int mpy = pPos.minBlockY();
                int mpz = pPos.minBlockZ();

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (x != 15 && y != 15 && x != 0 && y != 0 && z != 0 && z != 15) {
                                z = 15;
                            }

//                            if (x == 0 ||
//                                    x == 15 ||
//                                    z == 0 ||
//                                    z == 15 ||
//                                    y == 0 ||
//                                    y == 15
//                            ) {
                                pos.set(mpx + x, mpy + y, mpz + z);
                                checkNode(pos.asLong());
//                            }
                        }
                    }
                }
            });
        }
    }
}
