package cz.kvafy.regexp;

import cz.kvafy.regexp.RegexpParser.*;

class BackreferenceSemanticCheckerVisitor extends RegexpBaseVisitor<Void> {

    private int maxGroupNumber;

    @Override
    public Void visitInit(InitContext ctx) {
        maxGroupNumber = 0;
        return super.visitInit(ctx);
    }

    @Override
    public Void visitCapturingGroup(CapturingGroupContext ctx) {
        maxGroupNumber++;
        return super.visitCapturingGroup(ctx);
    }

    @Override
    public Void visitBackreference(BackreferenceContext ctx) {
        Integer groupNumber = Integer.valueOf(ctx.DIGIT().getText());
        if(groupNumber > maxGroupNumber)
            throw new RuntimeException(String.format("Encountered invalid backreference '%s'", ctx.getText()));

        return super.visitBackreference(ctx);
    }
}
