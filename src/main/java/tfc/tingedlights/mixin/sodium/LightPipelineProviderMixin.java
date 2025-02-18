package tfc.tingedlights.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess;
import me.jellysquid.mods.sodium.client.model.light.flat.FlatLightPipeline;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.tingedlights.util.sodium.TingedLightsLightingPipeline;

import java.util.EnumMap;

@Mixin(LightPipelineProvider.class)
public class LightPipelineProviderMixin {
    @Shadow
    @Final
    private EnumMap<LightMode, LightPipeline> lighters;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(LightDataAccess cache, CallbackInfo ci) {
        this.lighters.clear();
        this.lighters.put(LightMode.SMOOTH, new TingedLightsLightingPipeline(cache));
        this.lighters.put(LightMode.FLAT, new TingedLightsLightingPipeline(cache));
    }
}
