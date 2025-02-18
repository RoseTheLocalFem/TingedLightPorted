package tfc.tingedlights.utils.preprocessor;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.expression.GlslOperationNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslPrimitiveConstructorNode;
import io.github.ocelot.glslprocessor.api.node.primary.GlslFloatConstantNode;
import io.github.ocelot.glslprocessor.api.node.primary.GlslIntConstantNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslStringWriter;
import org.jetbrains.annotations.Nullable;
import tfc.tingedlights.utils.config.Config;
import tfc.tingedlights.utils.preprocessor.patch.PatchVisitor;
import tfc.tingedlights.utils.preprocessor.patches.NodePatch;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LightPreprocessor extends GlslPreprocessor {
    GlslPreprocessor actualProcessor;
    String name;
    Program.Type type;

    public LightPreprocessor(GlslPreprocessor actualProcessor, String name, Program.Type type) {
        this.actualProcessor = actualProcessor;
        this.name = name;
        this.type = type;
    }

    @Nullable
    @Override
    public String applyImport(boolean pUseFullPath, String pDirectory) {
        return null;
    }

    @Override
    public List<String> process(String shaderFile) {
        if (shaderFile.contains("\n//#define tinged_lights\n"))
            return actualProcessor.process(shaderFile);
        if (shaderFile.contains("\n//#define tinged_lights:dynamic\n"))
            return actualProcessor.process(shaderFile);

        boolean containsLight = false;

        for (String line : shaderFile.split("\n")) {
            line = line.trim();

            if (line.startsWith("#moj_import <light.glsl>")) {
                containsLight = true;
                break;
            }
        }

        String text = shaderFile;

        try {
            GlslTree tree = GlslParser.parse(text);
            boolean[] injectedLight = new boolean[]{containsLight};
            if (containsLight) {
                for (int i = 0; i < tree.getDirectives().size(); i++) {
                    String directive = tree.getDirectives().get(i);
                    if (directive.equals("#moj_import <light.glsl>")) {
                        tree.getDirectives().add(i, "in vec3 LightColor;");
                        tree.getDirectives().add(i, "#define TINGEDLIGHTS_PATCHED");
                        break;
                    }
                }
            }

            PatchVisitor visitor = new PatchVisitor();
            visitor.addPatch(new NodePatch() {
                @Override
                public void accept(GlslNode nd) {
                    if (nd instanceof GlslInvokeFunctionNode invoke) {
                        if (invoke.getHeader() instanceof GlslVariableNode var) {
                            if (var.getName().equals("texelFetch")) {
                                System.out.println("yipee");
                                GlslNode varSamp = invoke.getParameters().get(0);
                                if (varSamp instanceof GlslVariableNode vari) {
                                    if (!vari.getName().equals("Sampler2")) {
                                        // this call is not for lightmap sampling
                                        return;
                                    }
                                }

                                GlslNode varCoord = invoke.getParameters().get(1);
                                // TODO: make this better?
                                //       perhaps a shader has domain warping for UV2? idk
                                varCoord = new GlslVariableNode("UV2");

                                // remap call to mc's sample_lightmap
                                invoke.getParameters().remove(2);
                                invoke.getParameters().set(1, varCoord);
                                invoke.setHeader(new GlslVariableNode(
                                        "minecraft_sample_lightmap"
                                ));

                                // if lightmap is used, but the moj light import is not used
                                // then inject it
                                if (!injectedLight[0]) {
                                    tree.getDirectives().add(0, "#moj_import <light.glsl>");
                                    tree.getDirectives().add(0, "in vec3 LightColor;");
                                    tree.getDirectives().add(0, "#define TINGEDLIGHTS_PATCHED");
                                    injectedLight[0] = true;
                                }
                            }
                        }
                    }
                }
            });
            tree.visit(visitor);

            GlslStringWriter writer = new GlslStringWriter();
            tree.visit(writer);
            text = writer.toString();
        } catch (Throwable err) {
            err.printStackTrace();
        }

        if (Config.GeneralOptions.dumpShaders) {
            try {
                String typeStr = type.getExtension();

                File file = new File("shader_dump/tinged_lights/core/" + name.replace(":", "/") + typeStr);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(text.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } catch (Throwable ignored) {
            }
        }

        return actualProcessor.process(text);
    }
}
