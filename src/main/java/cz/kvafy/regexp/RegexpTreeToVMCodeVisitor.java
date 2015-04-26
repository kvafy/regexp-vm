package cz.kvafy.regexp;

import cz.kvafy.regexp.RegexpParser.*;

class RegexpTreeToVMCodeVisitor extends RegexpBaseVisitor<VMCode> {
    private int nextCapturingGroupNumber = 1;

    @Override
    public VMCode visitInit(InitContext ctx) {
        // we may not be visiting the parse tree for the first time...
        nextCapturingGroupNumber = 1;
        // surround body inside capturing group 0
        VMCode body = visit(ctx.expr());
        VMCode bodyInCapturingGroup0 = VMCode.codeForSurroundingByCapturingGroup(body, 0);
        // append success
        VMCode success = VMCode.codeForSuccess();
        return VMCode.codeForConcatenation(bodyInCapturingGroup0, success);
    }
    
    // matching operators
    //////////////////////////////////////////////////////////////////////////

    @Override
    public VMCode visitLiteral(LiteralContext ctx) {
        String token = ctx.getText();
        String literal = token.length() == 2 ? token.substring(1) : token; // escaped special char?
        return VMCode.codeForLiteralFromSet(literal);
    }

    @Override
    public VMCode visitDot(DotContext ctx) {
        return VMCode.codeForAnyCharacterMatch();
    }
    
    
    // repetitions
    //////////////////////////////////////////////////////////////////////////

    @Override
    public VMCode visitConcatenation(ConcatenationContext ctx) {
        VMCode code1 = visit(ctx.expr(0));
        VMCode code2 = visit(ctx.expr(1));
        return VMCode.codeForConcatenation(code1, code2);
    }

    @Override
    public VMCode visitAlteration(AlterationContext ctx) {
        VMCode code1 = visit(ctx.expr(0));
        VMCode code2 = visit(ctx.expr(1));
        return VMCode.codeForAlteration(code1, code2);
    }

    @Override
    public VMCode visitRepetitionStarGreedy(RepetitionStarGreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionStar(code, true);
    }

    @Override
    public VMCode visitRepetitionStarNongreedy(RepetitionStarNongreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionStar(code, false);
    }

    @Override
    public VMCode visitRepetitionPlusGreedy(RepetitionPlusGreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionPlus(code, true);
    }

    @Override
    public VMCode visitRepetitionPlusNongreedy(RepetitionPlusNongreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionPlus(code, false);
    }
    
    public VMCode visitRepetitionQuestionmarkGreedy(RepetitionQuestionmarkGreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionQuestionmark(code, true);
    }

    @Override
    public VMCode visitRepetitionQuestionmarkNongreedy(RepetitionQuestionmarkNongreedyContext ctx) {
        VMCode code = visit(ctx.expr());
        return VMCode.codeForRepetitionQuestionmark(code, false);
    }

    
    // special constructs
    //////////////////////////////////////////////////////////////////////////

    @Override
    public VMCode visitCapturingGroup(CapturingGroupContext ctx) {
        int groupNumber = this.nextCapturingGroupNumber++;
        VMCode code = visit(ctx.expr());
        return VMCode.codeForSurroundingByCapturingGroup(code, groupNumber);
    }

    @Override
    public VMCode visitNoncapturingGroup(NoncapturingGroupContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public VMCode visitBackreference(BackreferenceContext ctx) {
        int groupNumber = Integer.valueOf(ctx.DIGIT().getText());
        return VMCode.codeForBackreference(groupNumber);
    }

}