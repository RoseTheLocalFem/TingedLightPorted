package tfc.tingedlights.mixin.sodium;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = VertexBufferWriter.class, remap = false)
public interface VertexBufferWriterMixin {
    @Shadow
    boolean canUseIntrinsics();

    /**
     * @author
     * @reason
     */
    @Overwrite
    static @Nullable VertexBufferWriter tryOf(VertexConsumer consumer) {
        // disable fast writer (for now)
        // should setup a custom fast writer mechanism at some point
        return null;
    }
}
