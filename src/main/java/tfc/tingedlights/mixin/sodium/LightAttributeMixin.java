package tfc.tingedlights.mixin.sodium;

import net.caffeinemc.mods.sodium.api.vertex.attributes.common.LightAttribute;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import tfc.tingedlights.TesselationState;
import tfc.tingedlights.data.Color;

@Mixin(value = LightAttribute.class, remap = false)
public class LightAttributeMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void set(long ptr, int light) {
        Color defaultColor = TesselationState.getDefault();
        if (defaultColor == null) {
            MemoryUtil.memPutInt(ptr, light);
        } else {
            int sl = (light >> 16) & 0xFFFF;
//            MemoryUtil.memPutByte(ptr, (byte) (Mth.clamp(defaultColor.r(), 0, 1) * 255));
//            MemoryUtil.memPutByte(ptr + 1, (byte) (Mth.clamp(defaultColor.g(), 0, 1) * 255));
//            MemoryUtil.memPutByte(ptr + 2, (byte) (Mth.clamp(defaultColor.b(), 0, 1) * 255));
//            MemoryUtil.memPutByte(ptr + 3, (byte) (sl));
            int color = ((byte) (Mth.clamp(defaultColor.r(), 0, 1) * 255) & 0xFF) |
                    (((byte) (Mth.clamp(defaultColor.g(), 0, 1) * 255) & 0xFF) << 8) |
                    (((byte) (Mth.clamp(defaultColor.b(), 0, 1) * 255) & 0xFF) << 16) |
                    (((byte) (sl) & 0xFF) << 24);

            MemoryUtil.memPutInt(ptr, color);
        }
    }
}
