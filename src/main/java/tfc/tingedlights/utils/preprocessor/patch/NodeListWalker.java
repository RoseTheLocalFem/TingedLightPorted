package tfc.tingedlights.utils.preprocessor.patch;

import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.branch.*;
import io.github.ocelot.glslprocessor.api.node.expression.*;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslPrimitiveConstructorNode;
import io.github.ocelot.glslprocessor.api.node.primary.GlslBoolConstantNode;
import io.github.ocelot.glslprocessor.api.node.primary.GlslFloatConstantNode;
import io.github.ocelot.glslprocessor.api.node.primary.GlslIntConstantNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslArrayNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslFieldNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode;

import java.util.function.Consumer;

public class NodeListWalker {
    public static void walkList(GlslNode node, Consumer<GlslNode> func) {
        if (node == null) return;

        func.accept(node);
        if (node instanceof JumpNode jump) {
        } else if (node instanceof GlslCompareNode compare) {
            walkList(compare.getFirst(), func);
            walkList(compare.getSecond(), func);
        } else if (node instanceof GlslConditionalNode conditional) {
            walkList(conditional.getCondition(), func);
            walkList(conditional.getFirst(), func);
            walkList(conditional.getSecond(), func);
        } else if (node instanceof WhileLoopNode loop) {
            walkList(loop.getCondition(), func);
        } else if (node instanceof GlslUnaryNode unary) {
            walkList(unary.getExpression(), func);
        } else if (node instanceof ForLoopNode loop) {
            walkList(loop.getInit(), func);
            walkList(loop.getCondition(), func);
            walkList(loop.getIncrement(), func);
        } else if (node instanceof GlslSelectionNode selection) {
            walkList(selection.getExpression(), func);
            for (GlslNode glslNode : selection.getFirst()) walkList(glslNode, func);
            for (GlslNode glslNode : selection.getSecond()) walkList(glslNode, func);
        } else if (node instanceof GlslAssignmentNode assignment) {
            walkList(assignment.getFirst(), func);
            walkList(assignment.getSecond(), func);
        } else if (node instanceof GlslOperationNode operation) {
            walkList(operation.getFirst(), func);
            walkList(operation.getSecond(), func);
        } else if (node instanceof GlslInvokeFunctionNode invoke) {
            walkList(invoke.getHeader(), func);
            for (GlslNode parameter : invoke.getParameters()) {
                walkList(parameter, func);
            }
        } else if (node instanceof GlslVariableNode variable) {
        } else if (node instanceof GlslFloatConstantNode fc) {
        } else if (node instanceof GlslIntConstantNode ic) {
        } else if (node instanceof GlslBoolConstantNode bc) {
        } else if (node instanceof GlslFieldNode field) {
            walkList(field.getExpression(), func);
        } else if (node instanceof GlslReturnNode ret) {
            walkList(ret.getValue(), func);
        } else if (node instanceof GlslPrimitiveConstructorNode constructorNode) {
        } else if (node instanceof GlslNewNode make) {
            walkList(make.getInitializer(), func);
        } else if (node instanceof GlslArrayNode array) {
            walkList(array.getIndex(), func);
            walkList(array.getExpression(), func);
        } else {
            throw new RuntimeException("TODO");
        }

        if (node.getBody() != null) {
            for (GlslNode glslNode : node.getBody()) {
                walkList(glslNode, func);
            }
        }
    }
}
