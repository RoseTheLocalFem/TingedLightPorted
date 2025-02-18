package tfc.tingedlights.utils.preprocessor.patch;

import io.github.ocelot.glslprocessor.api.grammar.GlslVersionStatement;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.branch.GlslReturnNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslPrecisionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslDeclarationNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslStructNode;
import io.github.ocelot.glslprocessor.api.visitor.GlslFunctionVisitor;
import io.github.ocelot.glslprocessor.api.visitor.GlslTreeVisitor;
import org.jetbrains.annotations.Nullable;
import tfc.tingedlights.utils.preprocessor.patches.NodePatch;

import java.util.Map;

public class PatchVisitor implements GlslTreeVisitor {
    PatchFunctionVisitor functionVisitor = new PatchFunctionVisitor();

    public PatchVisitor() {
    }

    @Override
    public void visitMarkers(Map<String, GlslNode> markers) {
//        System.out.println("DECLR");
    }

    @Override
    public void visitVersion(GlslVersionStatement version) {
//        System.out.println("DECLR");
    }

    @Override
    public void visitDirective(String directive) {
//        System.out.println("DECLR");
    }

    @Override
    public void visitMacro(String key, String value) {
//        System.out.println("DECLR");
    }

    @Override
    public void visitField(GlslNewNode newNode) {
        functionVisitor.visitNew(newNode);
    }

    @Override
    public void visitStruct(GlslStructNode structSpecifier) {
//        System.out.println("DECLR");
    }

    @Override
    public void visitDeclaration(GlslDeclarationNode declaration) {
//        System.out.println("DECLR");
    }

    @Override
    public @Nullable GlslFunctionVisitor visitFunction(GlslFunctionNode node) {
        for (GlslNode glslNode : node.getBody()) {
            functionVisitor.visitor.visitNode(glslNode);
        }
        return new GlslFunctionVisitor() {
            @Override
            public void visitReturn(GlslReturnNode node) {

            }

            @Override
            public void visitAssignment(GlslAssignmentNode node) {

            }

            @Override
            public void visitPrecision(GlslPrecisionNode node) {

            }

            @Override
            public void visitInvokeFunction(GlslInvokeFunctionNode node) {

            }

            @Override
            public void visitNew(GlslNewNode node) {

            }

            @Override
            public void visitFunctionEnd() {

            }
        };
    }

    @Override
    public void visitTreeEnd() {
    }

    public void addPatch(NodePatch nodePatch) {
        functionVisitor.visitor.addPatch(nodePatch);
    }
}
