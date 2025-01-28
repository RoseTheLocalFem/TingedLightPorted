package tfc.tingedlights.mixin.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.tingedlights.TesselationState;
import tfc.tingedlights.data.Color;
import tfc.tingedlights.data.LightManager;
import tfc.tingedlights.data.access.ILightEngine;
import tfc.tingedlights.itf.VertexBufferConsumerExtensions;

@Mixin(LiquidBlockRenderer.class)
public class FluidRendererMixin {
	ThreadLocal<VertexConsumer> p_203176_ = new ThreadLocal<>();
	
	@Inject(at = @At("HEAD"), method = "tesselate")
	public void preDraw(BlockAndTintGetter p_234370_, BlockPos p_234371_, VertexConsumer p_234372_, BlockState p_234373_, FluidState p_234374_, CallbackInfo ci) {
		TesselationState.guiLighting.set(false);
		this.p_203176_.set(p_234372_);
	}
	
	@Inject(at = @At("TAIL"), method = "tesselate")
	public void postDraw(BlockAndTintGetter p_234370_, BlockPos p_234371_, VertexConsumer p_234372_, BlockState p_234373_, FluidState p_234374_, CallbackInfo ci) {
		this.p_203176_.remove();
	}
	
	@Inject(at = @At("HEAD"), method = "getLightColor")
	public void preGetColor(BlockAndTintGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Integer> cir) {
		if (p_203176_.get() instanceof VertexBufferConsumerExtensions extensions) {
			LightManager manager = ((ILightEngine) pLevel.getLightEngine()).getManager();
			Color color = manager.getColor(pPos);
			extensions.setDefault(color);
		}
	}
}
