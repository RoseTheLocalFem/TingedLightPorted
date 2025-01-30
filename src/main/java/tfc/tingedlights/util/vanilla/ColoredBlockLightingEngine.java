package tfc.tingedlights.util.vanilla;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
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

                            pos.set(mpx + x, mpy + y, mpz + z);
                            checkNode(pos.asLong());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void checkNode(long p_285169_) {
        long i = SectionPos.blockToSection(p_285169_);
        DataLayer layer = this.storage.getDataLayer(
                i, true
        );
        if (layer != null) {
            BlockState blockstate = this.getState(this.pos.set(p_285169_));
            int j = this.getEmission(p_285169_, blockstate);

            if (j < 15) {
                int px, py, pz;

                int k = layer.get(
                        px = SectionPos.sectionRelative(BlockPos.getX(p_285169_)),
                        py = SectionPos.sectionRelative(BlockPos.getY(p_285169_)),
                        pz = SectionPos.sectionRelative(BlockPos.getZ(p_285169_))
                );
                if (j < k) {
                    layer.set(px, py, pz, 0);
                    this.enqueueDecrease(p_285169_, LightEngine.QueueEntry.decreaseAllDirections(k));
                } else {
                    this.enqueueDecrease(p_285169_, PULL_LIGHT_IN_ENTRY);
                }
            }

            if (j > 0) {
                this.enqueueIncrease(p_285169_, LightEngine.QueueEntry.increaseLightFromEmission(j, isEmptyShape(blockstate)));
            }
        }
    }

    @Override
    protected void propagateDecrease(long p_285435_, long p_285230_) {
        int i = LightEngine.QueueEntry.getFromLevel(p_285230_);

        for (Direction direction : PROPAGATION_DIRECTIONS) {
            if (LightEngine.QueueEntry.shouldPropagateInDirection(p_285230_, direction)) {
                long j = BlockPos.offset(p_285435_, direction);
                DataLayer layer = this.storage.getDataLayer(
                        SectionPos.blockToSection(j),
                        true
                );
                if (layer != null) {
                    int px, py, pz;

                    int k = layer.get(
                            px = SectionPos.sectionRelative(BlockPos.getX(j)),
                            py = SectionPos.sectionRelative(BlockPos.getY(j)),
                            pz = SectionPos.sectionRelative(BlockPos.getZ(j))
                    );
                    if (k != 0) {
                        if (k <= i - 1) {
                            BlockState blockstate = this.getState(this.pos.set(j));
                            int l = this.getEmission(j, blockstate);
                            if (l < 15) {
                                if (k > 1 && l < k) {
                                    layer.set(px, py, pz, 0);
                                    this.enqueueDecrease(j, LightEngine.QueueEntry.decreaseSkipOneDirection(k, direction.getOpposite()));
                                }
                            }

                            if (l > 0) {
                                this.enqueueIncrease(j, LightEngine.QueueEntry.increaseLightFromEmission(l, isEmptyShape(blockstate)));
                            }
                        } else {
                            this.enqueueIncrease(j, LightEngine.QueueEntry.increaseOnlyOneDirection(k, false, direction.getOpposite()));
                        }
                    }
                }
            }
        }
    }
}
