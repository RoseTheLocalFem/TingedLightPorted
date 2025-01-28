package tfc.tingedlights.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.tingedlights.api.DynamicLightApi;
import tfc.tingedlights.util.OnThread;
import tfc.tingedlights.utils.config.Config;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow @Nullable private ClientLevel level;
	
	// I would hope this doesn't get overwritten
	@Inject(at = @At("HEAD"), method = "renderLevel")
	public void preRenderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_, CallbackInfo ci) {
		if (Config.GeneralOptions.dynamicLights)
			DynamicLightApi.tick();
		OnThread.run();
	}
}
