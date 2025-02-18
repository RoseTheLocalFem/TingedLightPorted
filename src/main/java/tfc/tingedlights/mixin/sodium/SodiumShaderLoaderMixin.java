package tfc.tingedlights.mixin.sodium;

import me.jellysquid.mods.sodium.client.gl.shader.*;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.tingedlights.utils.config.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(value = ShaderParser.class, remap = false)
public class SodiumShaderLoaderMixin {
    @Inject(at = @At("TAIL"), method = "parseShader(Ljava/lang/String;Lme/jellysquid/mods/sodium/client/gl/shader/ShaderConstants;)Ljava/lang/String;", cancellable = true)
    private static void postParse(String src, ShaderConstants constants, CallbackInfoReturnable<String> cir) {
        String str = cir.getReturnValue();

        if (str.contains("\n//#define tinged_lights\n")) {
            cir.setReturnValue(str.replaceFirst("\n//#define tinged_lights\n", "\n#define tinged_lights\n"));
            return;
        }

        int targetMethod = str.indexOf("_sample_lightmap");
        if (targetMethod == -1) return;

        String sub = str.substring(targetMethod);
        targetMethod += sub.indexOf("{");

        String out = str.substring(0, targetMethod + 1);
        out += "\n    #if 1\n" +
                "    	 int g = (uv.x >> 8) & 0xFF;\n" +
                "    	 int r = (uv.x) & 0xFF;\n" +
                "    	 int skyInt = (uv.y >> 8) & 0xFF;\n" +
                "    	 int b = (uv.y) & 0xFF;\n" +

                "        vec4 light = vec4(r / 255.0, g / 255.0, b / 255.0, 1);\n" +

                "        uv = ivec2(15, skyInt);\n" +
                "        vec2 clamped = clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0));\n" +
                "        vec4 sky = texture(lightMap, clamped);\n" +
                "        \n" +
                "        if (tinged_lights_value(light) > tinged_lights_value(sky)) {\n" +
                "            sky += light;\n" +
                "        } else {\n" +
                "            if (tinged_lights_value(light) > tinged_lights_value(sky) - 0.16) {\n" +
                "                sky += light / 1.015;\n" +
                "            } else {\n" +
                "                vec4 lv = light / 1.025;\n" +
                "                sky += lv;\n" +
                "            }\n" +
                "        }\n" +
                "        vec4 vec = max(sky, light);\n" +
                "        vec = min(vec, vec4(1));\n" +
                "vec.w = 1.0;" +
                "        return vec;\n" +
                "    #else\n" +
                "    ";
        sub = str.substring(targetMethod + 1);
        if (sub.startsWith("\n")) sub = sub.substring(1);
        targetMethod = sub.indexOf("}");
        out += sub.substring(1, targetMethod);
        out += "    #endif\n" +
                "}";
        out += sub.substring(targetMethod + 1);

        StringBuilder output = new StringBuilder();
        boolean inserted = false;
        for (String s : out.split("\n")) {
            output.append(s).append("\n");
            if (!inserted) {
                output.append("\n" + "float tinged_lights_value(vec4 color) {\n" + "    return max(color.r, max(color.b, color.g));\n" + "}\n");
                inserted = true;
            }
        }

        if (Config.GeneralOptions.dumpShaders) {
//            if (!Config.wroteLightShader) {
            try {
                File file = new File("shader_dump/tinged_lights/sodium/shader.glsl");
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(output.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
                Config.wroteLightShader = true;
            } catch (Throwable ignored) {
            }
//            }
        }

        cir.setReturnValue(output.toString());
    }
}
