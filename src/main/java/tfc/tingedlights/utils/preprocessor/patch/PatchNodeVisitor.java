package tfc.tingedlights.utils.preprocessor.patch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslNodeVisitor;
import tfc.tingedlights.utils.preprocessor.patches.NodePatch;

import java.util.ArrayList;
import java.util.List;

public class PatchNodeVisitor implements GlslNodeVisitor {
    List<NodePatch> patches = new ArrayList<>();

    public void addPatch(NodePatch patch) {
        patches.add(patch);
    }

    @Override
    public void visitNode(GlslNode node) {
        NodeListWalker.walkList(node, (nd) -> {
            for (NodePatch patch : patches) {
                patch.accept(nd);
            }
        });
    }
}
