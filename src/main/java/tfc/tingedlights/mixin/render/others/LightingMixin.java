package tfc.tingedlights.mixin.render.others;

import com.mojang.blaze3d.platform.Lighting;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.tingedlights.TesselationState;

@Mixin(Lighting.class)
public class LightingMixin {
	@Inject(at = @At("HEAD"), method = "setupFor3DItems")
	private static void setup3DGUI(CallbackInfo ci) {
		TesselationState.guiLighting.set(true);
	}
	
	@Inject(at = @At("HEAD"), method = "setupForEntityInInventory")
	private static void setupEntityGUI(CallbackInfo ci) {
		TesselationState.guiLighting.set(true);
	}
	
	@Inject(at = @At("HEAD"), method = "setupForFlatItems")
	private static void preSetupItems(CallbackInfo ci) {
		TesselationState.guiLighting.set(true);
	}
	
	@Inject(at = @At("HEAD"), method = "setupLevel")
	private static void preSetupOverworld(org.joml.Matrix4f p_254246_, CallbackInfo ci) {
		TesselationState.guiLighting.set(false);
	}
	
	@Inject(at = @At("HEAD"), method = "setupNetherLevel")
	private static void preSetupNether(org.joml.Matrix4f p_254421_, CallbackInfo ci) {
		TesselationState.guiLighting.set(false);
	}
}
