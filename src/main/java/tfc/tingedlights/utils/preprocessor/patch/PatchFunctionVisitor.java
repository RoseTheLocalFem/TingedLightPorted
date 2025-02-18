package tfc.tingedlights.utils.preprocessor.patch;

import io.github.ocelot.glslprocessor.api.node.branch.GlslReturnNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslPrecisionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslFunctionVisitor;

public class PatchFunctionVisitor implements GlslFunctionVisitor {
    PatchNodeVisitor visitor = new PatchNodeVisitor();

    public PatchFunctionVisitor() {
    }

    @Override
    public void visitReturn(GlslReturnNode node) {
        visitor.visitNode(node);
    }

    @Override
    public void visitAssignment(GlslAssignmentNode node) {
        visitor.visitNode(node);
    }

    @Override
    public void visitPrecision(GlslPrecisionNode node) {
        visitor.visitNode(node);
    }

    @Override
    public void visitInvokeFunction(GlslInvokeFunctionNode node) {
        visitor.visitNode(node);
    }

    @Override
    public void visitNew(GlslNewNode node) {
        visitor.visitNode(node);
    }

    @Override
    public void visitFunctionEnd() {
    }
}
