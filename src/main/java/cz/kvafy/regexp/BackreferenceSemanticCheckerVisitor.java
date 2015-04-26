package cz.kvafy.regexp;

import cz.kvafy.regexp.RegexpParser.*;

class BackreferenceSemanticCheckerVisitor extends RegexpBaseVisitor<Void> {

    private int maxValidGroupNumber;

    @Override
    public Void visitInit(InitContext ctx) {
        maxValidGroupNumber = 0;
        return super.visitInit(ctx);
    }

    @Override
    public Void visitCapturingGroup(CapturingGroupContext ctx) {
        // the group becomes available for backreferences after its content
        // has been processed

        Void result = super.visitCapturingGroup(ctx);
        maxValidGroupNumber++;
        return result;
    }

    @Override
    public Void visitBackreference(BackreferenceContext ctx) {
        Integer groupNumber = Integer.valueOf(ctx.DIGIT().getText());

        if(groupNumber > maxValidGroupNumber) {
            String position = RegexpUtils.lineAndColumnString(ctx.getStart());
            throw new RuntimeException(String.format(
                    "Encountered invalid backreference '%s' at %s", ctx.getText(), position));
        }

        return super.visitBackreference(ctx);
    }
}
