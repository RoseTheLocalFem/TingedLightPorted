package tfc.tingedlights.utils.preprocessor.patches;

import io.github.ocelot.glslprocessor.api.node.GlslNode;

public abstract class NodePatch {
    public abstract void accept(GlslNode nd);
}
