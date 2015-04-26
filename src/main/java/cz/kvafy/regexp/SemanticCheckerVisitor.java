package cz.kvafy.regexp;

import org.antlr.v4.runtime.misc.NotNull;

import cz.kvafy.regexp.RegexpParser.*;

public class SemanticCheckerVisitor extends RegexpBaseVisitor<Void> {

    private int maxGroupNumber = 0;

    @Override
    public Void visitCapturingGroup(@NotNull CapturingGroupContext ctx) {
        maxGroupNumber++;
        return super.visitCapturingGroup(ctx);
    }

    @Override
    public Void visitBackreference(@NotNull BackreferenceContext ctx) {
        Integer groupNumber = Integer.valueOf(ctx.DIGIT().getText());
        if(groupNumber > maxGroupNumber)
            throw new RuntimeException(String.format("Encountered invalid backreference '%s'", ctx.getText()));

        return super.visitBackreference(ctx);
    }
}
